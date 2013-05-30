package backend.engine

import scala.xml.{ Attribute, Elem, Node, NodeSeq, XML, Xhtml }
import scala.xml.{ Null, Text }
import scala.xml.NodeSeq.seqToNodeSeq
import play.api.Play

/**
  * A class implementing the compile method of the TextCompiler. The class is NOT thread safe!
  *
  * @author Stefan Bleibinhaus
  *
  */
// TODO: Make it thread safe, use more functional style, make it less accepting of misused sbtl, testing...
class TextCompilerSbtl() extends TextCompiler {
  private val blogUrl = Play.current.configuration.getString("blogUrl").getOrElse("http://bleibinha.us/blog")
  private val externEvidence = "://"
  private val fileHostUrl = blogUrl + "/file/"
  private var footnotes: List[Footnote] = Nil
  private var footnotesNumber: Int = 1
  private var headers: List[List[Header]] = Nil
  private var refs: List[Ref] = Nil
  private var refNumber: Int = 1
  private var images: Int = 1
  private var codeSnippets: Int = 1
  private var internReferences: List[InternReference] = Nil

  /* (non-Javadoc)
 * @see backend.engine.TextCompiler#compile(java.lang.String)
 */
  override def compile(text: String): (String, String) =
    try {
      val textXml = XML.loadString(text)
      (compilePreviewAbstract(textXml).toString, compilePost(textXml))
    } catch {
      case e: TextCompileException => throw e
      case e: Exception => throw new TextCompileException(e.getMessage())
    }

  private def compilePost(textXml: Elem): String = {
    val textAbstractCompiled1 = compileTextAbstract(textXml \ "abstract")
    val textCompiled1 = compileText(textXml \ "text")
    val footnotesCompiled1 =
      if (!footnotes.isEmpty)
        parseFootnotes()
      else
        NodeSeq.Empty
    val refsCompiled =
      if (!refs.isEmpty)
        parseReferences()
      else
        NodeSeq.Empty
    val contentCompiled =
      if (!headers.isEmpty)
        parseContent()
      else
        throw new TextCompileException("Content is empty!")
    val textAbstractCompiled = compileIntRefs(textAbstractCompiled1)
    val textCompiled = compileIntRefs(textCompiled1)
    val footnotesCompiled = compileIntRefs(footnotesCompiled1)
    Xhtml.toXhtml(textAbstractCompiled ++ contentCompiled ++ textCompiled ++ footnotesCompiled ++ refsCompiled)
  }

  private def compileText(nodes: NodeSeq): NodeSeq =
    nodes flatMap {
      node =>
        node match {
          case <text>{ contents @ _* }</text> => <div class="text">{ compile(contents) }</div>
          case _ => throw new TextCompileException("Could not find text tag in " + nodes + "!")
        }
    }

  private def compileTextAbstract(nodes: NodeSeq): NodeSeq =
    nodes flatMap {
      node =>
        node match {
          case <abstract>{ contents @ _* }</abstract> => <div class="abstract">{ compile(contents) }</div>
          case _ => throw new TextCompileException("Could not find abstract tag in " + nodes + "!")
        }
    }

  private def compile(nodes: NodeSeq): NodeSeq =
    nodes flatMap {
      node =>
        node match {
          case <p>{ contents @ _* }</p> => <p>{ compile(contents) }</p>
          case <b>{ contents @ _* }</b> => <b>{ compile(contents) }</b>
          case <i>{ contents @ _* }</i> => <i>{ compile(contents) }</i>
          case <ul>{ contents @ _* }</ul> => <ul>{ compile(contents) }</ul>
          case <li>{ contents @ _* }</li> => <li>{ compile(contents) }</li>
          case h @ <h1>{ titleNode }</h1> => compileHeader(h, titleNode, 1)
          case h @ <h2>{ titleNode }</h2> => compileHeader(h, titleNode, 2)
          case ref @ <ref>{ contents @ _* }</ref> => compileRef(ref, contents)
          case intref @ <intref>{ contents @ _* }</intref> => intref
          case <footnote>{ contents @ _* }</footnote> => compileFootnote(contents)
          case gist @ <gist>{ contents @ _* }</gist> => compileGistCode(gist, contents)
          case img @ <img>{ contents @ _* }</img> => compileImg(img, contents)
          case contents if !contents.exists(_.toString.exists(_ == '<')) => contents
          case contents => throw new TextCompileException("Could not compile " + contents + "!")
        }
    }

  private def compileIntRefs(nodes: NodeSeq): NodeSeq =
    nodes flatMap {
      node =>
        {
          node match {
            case intref @ <intref>{ contents @ _* }</intref> => compileIntRef(intref, contents)
            case Elem(prefix, label, attributes, scope, child @ _*) =>
              Elem(prefix, label, attributes, scope, false, compileIntRefs(child): _*)
            case node => node
          }
        }
    }

  private def compileIntRef(intRefNode: Node, intRefContents: NodeSeq): NodeSeq = {
    val name = getAttribute(intRefNode, "name").getOrElse(
      throw new TextCompileException("Int ref node " + intRefNode + " has no name!"))
    val title = internReferences.find(_.name == name).getOrElse(
      throw new TextCompileException("Could not find title for name " +
        name + " in internReferences " + internReferences + "!")).title
    <a href={ "#" + name } title={ title }><i>{ title }</i></a>
  }

  private def compileImg(imgNode: Node, imgContent: NodeSeq): NodeSeq = {
    val src = getAttribute(imgNode, "src") match {
      case Some(src) => if (src.contains(externEvidence)) src else fileHostUrl + src
      case None => throw new TextCompileException("Img node " + imgNode + " has no src!")
    }
    val title = "Image " + images
    val description = compile(imgContent)
    val name = getAttribute(imgNode, "name")
    images += 1
    name foreach {
      name => internReferences = InternReference(name, title) :: internReferences
    }
    val imgTag = name match {
      case Some(name) => <a name={ name }></a><a href={ src } target="_blank">
                                                <img src={ src } alt={ title }></img>
                                              </a>
      case None => <a href={ src } target="_blank"><img src={ src } alt={ title }></img></a>
    }
    val titleDescr = if (description.isEmpty) {
      <div class="imgTitleDescr">{ title }</div>
    } else {
      <div class="imgTitleDescr">{ title + ": " } { description }</div>
    }
    <div class="img">{ imgTag } { titleDescr }</div>
  }

  private def compileGistCode(gistNode: Node, gistContent: NodeSeq): NodeSeq = {
    val src = getAttribute(gistNode, "src").getOrElse(
      throw new TextCompileException("Gist node " + gistNode + " has no src!"))
    val title = "Code snippet " + codeSnippets
    val name = getAttribute(gistNode, "name")
    val description = compile(gistContent)
    codeSnippets += 1
    name foreach {
      name => internReferences = InternReference(name, title) :: internReferences
    }
    val codeTag = name match {
      case Some(name) => <a name={ name }></a><script src={ src }></script>
      case None => <script src={ src }></script>
    }
    val titleDescr = if (description.isEmpty) {
      <div class="codeTitleDescr">{ title }</div>
    } else {
      <div class="codeTitleDescr">{ title + ": " } { description }</div>
    }
    <div class="code">
      <div class="gistCode">
        { codeTag }
      </div>
      <div class="codeTitleDescr">
        { titleDescr }
      </div>
    </div>
  }

  private def compileRef(refNode: Node, refContent: NodeSeq): NodeSeq = {
    val link = getAttribute(refNode, "link")
    val author = getAttribute(refNode, "author")
    val title = getAttribute(refNode, "title")
    val origin = refs.find(ref => ref.link == link && ref.author == author && ref.title == title)
    val number = origin match {
      case Some(tag) => tag.number
      case None => {
        val nr = refNumber
        refs = Ref(nr, link, author, title) :: refs
        refNumber += 1
        nr
      }
    }
    val contents = compile(refContent)
    if (link.isEmpty && (title.isEmpty || author.isEmpty))
      throw new TextCompileException("Ref node " + refNode + " has no link or no author and title!")
    val titleText = getTitleTextForRef(link, author, title)
    link match {
      case Some(link) if !link.contains(externEvidence) =>
        <a href={ blogUrl + link } title={ titleText } target="_blank">{ contents } { "[" + number + "]" }</a>
      case Some(link) =>
        <a href={ link } title={ titleText } target="_blank">
          { contents }
          { "[" + number + "]" } <i class="icon-external-link"></i>
        </a>
      case _ =>
        <a href={ "#ref-" + number } title={ titleText }>{ contents } { "[" + number + "]" }</a>
    }
  }

  private def getTitleTextForRef(link: Option[String], author: Option[String], title: Option[String]): String = {
    val shownLink = link match {
      case Some(link) if link.contains(externEvidence) => Some(link)
      case Some(link) => Some(blogUrl + link)
      case None => None
    }
    (shownLink, author, title) match {
      case (Some(shownLink), None, None) => shownLink
      case (Some(shownLink), Some(author), Some(title)) => title + " by " + author + " (" + shownLink + ")"
      case (None, Some(author), Some(title)) => title + " by " + author
      case (Some(shownLink), Some(author), None) => shownLink + " by " + author
      case (Some(shownLink), None, Some(title)) => title + " (" + shownLink + ")"
      case _ =>
        throw new TextCompileException("Unable to get titletext for shownLink="
          + shownLink + ", author=" + author + ", title=" + title + "!")
    }
  }

  private def getTitleTextForRef(ref: Ref): String =
    getTitleTextForRef(ref.link, ref.author, ref.title)

  private def parseReferences(): NodeSeq = {
    val refsXml =
      for (ref <- refs.sortBy(_.number)) yield {
        val titleText = getTitleTextForRef(ref)
        ref match {
          case Ref(number, Some(link), _, _) if !link.contains(externEvidence) =>
            <li class="ref">
              { "[" + number + "] " }
              <a href={ link } title={ titleText } target="_blank">{ titleText }</a>
            </li>
          case Ref(number, Some(link), _, _) =>
            <li class="ref">
              { "[" + number + "] " }
              <a href={ link } title={ titleText } target="_blank">
                { titleText } <i class="icon-external-link"></i>
              </a>
            </li>
          case _ =>
            <li class="ref">
              <a name={ "ref-" + ref.number }></a>{ "[" + ref.number + "] " } { titleText }
            </li>
        }
      }
    <div class="references">
      { compile(<h1>{ "References" }</h1>) } <ul>{ refsXml }</ul>
    </div>
  }

  private def getAttribute(node: Node, attribute: String): Option[String] =
    node \ { "@" + attribute } match {
      case NodeSeq.Empty => None
      case content => Some(content.toString)
    }

  private def compileHeader(hNode: Node, titleNode: Node, level: Int): NodeSeq = {
    val title = titleNode.toString
    val name = getAttribute(hNode, "name")
    level match {
      case 1 => {
        val numeration = (headers.size + 1).toString
        val header = Header(numeration, title, name)
        val numTitle = numeration + " " + title
        name foreach {
          name => internReferences = InternReference(name, numTitle) :: internReferences
        }
        headers = headers :+ List(header)
        val aNode = name match {
          case Some(name) => <a name={ name }></a>
          case None =>
            XML.loadString("<a name=\"header-" + header.numeration + "\"></a>")
        }
        <h4>{ aNode } { numTitle }</h4>
      }
      case 2 => {
        val lastL1HeaderList = headers.last
        val numeration = lastL1HeaderList.head.numeration + "." + lastL1HeaderList.size
        val header = Header(numeration, title, name)
        val numTitle = numeration + " " + title
        name foreach {
          name => internReferences = InternReference(name, numTitle) :: internReferences
        }
        headers = headers.take(headers.size - 1) ++ List(lastL1HeaderList :+ header)
        val aNode = name match {
          case Some(name) => <a name={ name }></a>
          case None =>
            XML.loadString("<a name=\"header-" + header.numeration + "\"></a>")
        }
        <h5>{ aNode } { numTitle }</h5>
      }
      case _ => throw new TextCompileException("Unable to handle header level " + level + "!")
    }
  }

  private def parseContent(): NodeSeq = {
    val headersXml =
      for (headers1 <- headers) yield {
        val h1Href = headers1.head.name match {
          case Some(name) => Attribute(None, "href", Text("#" + name), Null)
          case None => Attribute(None, "href", Text("#header-" + headers1.head.numeration), Null)
        }
        val h1 = <li class="h1">{ <a class="l1header">
            { headers1.head.numeration + " " + headers1.head.title }
          </a>% h1Href }</li>
        val h2s =
          for (subheader <- headers1.tail) yield {
            val h2Href = subheader.name match {
              case Some(name) => Attribute(None, "href", Text("#" + name), Null)
              case None => Attribute(None, "href", Text("#header-" + subheader.numeration), Null)
            }
            <li class="h2">{ <a class="l2header">
                { subheader.numeration + " " + subheader.title }
              </a>% h2Href }</li>
          }
        if (h2s.isEmpty)
          h1
        else
          h1 ++ <ul>{ h2s }</ul>
      }
    <hr/><div class="content"><h4>Content</h4><ul>{ headersXml }</ul></div><hr/>
  }

  private def compileFootnote(nodes: NodeSeq): NodeSeq = {
    val number = footnotesNumber
    footnotesNumber += 1
    footnotes = Footnote(number, compile(nodes)) :: footnotes
    val titleText: Option[String] =
      if (nodes.size == 1)
        nodes head match {
          case contents if !contents.exists(_.toString.exists(_ == '<')) =>
            Some(contents.toString)
          case _ => None
        }
      else
        None
    val aNode = <a href={ "#footnote-" + number }><sup>{ number }</sup></a>
    titleText match {
      case Some(titleText) => aNode % Attribute(None, "title", Text(titleText), Null)
      case None => aNode
    }
  }

  private def parseFootnotes(): NodeSeq = {
    val footnotesXml =
      for (footnote <- footnotes.sortBy(_.number))
        yield <p class="footnote">
                <a name={ "footnote-" + footnote.number }></a>
                <sup>{ footnote.number }</sup>{ " " } { footnote.content }
              </p>;
    <hr/><div class="footnotes">{ footnotesXml }</div><hr/>
  }

  private def compilePreviewAbstract(nodes: NodeSeq): NodeSeq =
    nodes flatMap {
      node =>
        node match {
          case <sbtl>{ contents @ _* }</sbtl> => contents.flatMap(compilePreviewAbstract(_))
          case <abstract>{ contents @ _* }</abstract> => contents.flatMap(compilePreviewAbstract(_))
          case <text>{ contents @ _* }</text> => NodeSeq.Empty
          case <b>{ contents @ _* }</b> => <b>{ compilePreviewAbstract(contents) }</b>
          case <i>{ contents @ _* }</i> => <i>{ compilePreviewAbstract(contents) }</i>
          case <ref>{ contents @ _* }</ref> => compilePreviewAbstract(contents)
          case <footnote>{ contents @ _* }</footnote> => NodeSeq.Empty
          case contents if !contents.exists(_.toString.exists(_ == '<')) => contents
          case contents => throw new TextCompileException("Could not compile " + contents + " for abstract!")
        }
    }

  private case class InternReference(val name: String, val title: String)
  private case class Footnote(val number: Int, val content: NodeSeq)
  private case class Header(val numeration: String, val title: String, val name: Option[String] = None)
  private case class Ref(
    val number: Int,
    val link: Option[String],
    val author: Option[String],
    val title: Option[String])
}
package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import model.blog.{ PostEnriched, StaticPage }
import model.ui.{ BreadcrumbItem, SidebarContainer }
import play.api.Play
import play.api.data.{ Form, FormError }
import play.api.data.Forms.nonEmptyText
import play.api.libs.json.Json
import play.api.mvc.Action

/**
  * The blog controller contains all typical reader options.
  * The order of the methods is the same as in the routes file.
  *
  * @author Stefan Bleibinhaus
  *
  */
object BlogController extends BaseController {
  private val searchForm = Form("searchFieldInput" -> nonEmptyText)
  private val postsOnIndexPage =
    Play.current.configuration.getString("postsOnIndexPage").getOrElse("4").toInt
  private val loadMorePostsCount =
    Play.current.configuration.getString("loadMorePostsCount").getOrElse("12").toInt
  private val rssLogoUrl = Play.current.configuration.getString("rss.logoUrl")
    .getOrElse(blogUrl + "/assets/images/logo.png")
  private val rssLogoWidth = Play.current.configuration.getString("rss.logoWidth")
    .getOrElse("120").toInt
  private val rssLogoHeight = Play.current.configuration.getString("rss.logoHeight")
    .getOrElse("120").toInt
  private val moreBaseUrl = "/blog/more"

  /**
    * Shows the Index
    *
    * @return
    */
  def index = Action {
    implicit request =>
      withUserOption {
        userOption =>
          {
            val breadcrumb = List(homeBcItem)
            Ok(views.html.content.postListing(
              title(),
              userOption,
              breadcrumb,
              SidebarContainer(),
              indexPosts(),
              indexMoreUrl()))
          }
      }
  }

  /**
    * Shows the post of the given year, month and urlTitle, or no post if the post could not be found.
    *
    * @param year
    * @param month
    * @param urlTitle
    * @return
    */
  def showPost(year: Long, month: Long, urlTitle: String) = Action {
    implicit request =>
      val monthString = monthToString(month)
      val relUrl = year + "/" + monthString + "/" + urlTitle
      val enrichedPost = postEnrichedDataService.getByRelUrl(relUrl)
      withUserOption {
        userOption =>
          enrichedPost match {
            // Some post found
            case Some(enrichedPost) => {
              val breadcrumb = List(
                homeBcItem,
                BreadcrumbItem(year.toString, "/blog/" + year, "icon-calendar"),
                BreadcrumbItem(monthString, "/blog/" + year + "/" + monthString),
                BreadcrumbItem(enrichedPost.title, "/blog/" + relUrl, "icon-caret-right"))
              Ok(views.html.content.post(
                title(enrichedPost.title),
                userOption,
                breadcrumb,
                SidebarContainer(),
                Some(enrichedPost)))
            }
            // No post found
            case None => {
              val breadcrumb = List(
                homeBcItem,
                BreadcrumbItem(year.toString, "/blog/" + year),
                BreadcrumbItem(monthString, "/blog/" + year + "/" + monthString),
                BreadcrumbItem("Post \"" + urlTitle + "\" not found", None, Some("icon-exclamation-sign")))
              Ok(views.html.content.post(
                title("Post not found"),
                userOption,
                breadcrumb,
                SidebarContainer()))
            }
          }
      }
  }

  /**
    * Sends the file of the given name to the client,
    * or redirects to root, if it could not have been found.
    *
    * @param name
    * @return
    */
  def file(name: String) = Action {
    val file = fileDataService.getByName(name)
    Async {
      file map { file =>
        file match {
          case Some(file) => Ok.sendFile(file, true)
          case None => redirectToRoot
        }
      }
    }
  }

  /**
    * Shows the posts of the given year and month.
    *
    * @param year
    * @param month
    * @return
    */
  def showPostsYearMonth(year: Long, month: Long) = Action {
    implicit request =>
      val monthString = monthToString(month)
      val enrichedPosts = postEnrichedDataService.getByDate(year, Some(month))
      withUserOption {
        userOption =>
          {
            val breadcrumb = List(
              homeBcItem,
              BreadcrumbItem(year.toString, "/blog/" + year, "icon-calendar"),
              BreadcrumbItem(monthString, "/blog/" + year + "/" + monthString))
            Ok(views.html.content.postListing(
              title(year + "/" + monthString),
              userOption,
              breadcrumb,
              SidebarContainer(),
              enrichedPosts))
          }
      }
  }

  /**
    * Shows the posts of the given year.
    *
    * @param year
    * @return
    */
  def showPostsYear(year: Long) = Action {
    implicit request =>
      val enrichedPosts = postEnrichedDataService.getByDate(year, None)
      withUserOption {
        userOption =>
          {
            val breadcrumb = List(
              homeBcItem,
              BreadcrumbItem(year.toString, "/blog/" + year, "icon-calendar"))
            Ok(views.html.content.postListing(
              title(year.toString),
              userOption,
              breadcrumb,
              SidebarContainer(),
              enrichedPosts))
          }
      }
  }

  /**
    * This method is used for the search input to display suggested values
    * while the user is typing.
    *
    * @param term The current input of the user
    * @return Json containing matched post titles and tags.
    */
  def quicksearch(term: String) = Action {
    val posts = postEnrichedDataService.getByNameParticle(term)
    val tags = tagDataService.getByNameParticle(term)
    val result = Json.toJson(
      posts.map(
        p => Map(
          "name" -> p.title,
          "url" -> ("/blog/" + p.relUrl),
          "type" -> "post")) :::
        tags.map(
          t => Map(
            "name" -> t.name,
            "url" -> ("/blog/tag/" + t.name),
            "type" -> "tag")))
    Ok(result)
  }

  /**
    * Loads more posts and returns them as a snippet to be bound into the page using ajax.
    *
    * @param postIndex The index of the last post, which should NOT be displayed.
    *  Posts after this index will be shown.
    * @return
    */
  def more(postIndex: Long) = Action {
    if (postIndex.isValidInt) {
      val from = postIndex.toInt
      val to = from + loadMorePostsCount
      val posts = postEnrichedDataService.get(from, to)
      Ok(views.html.content.postListingSnippet(posts, indexMoreUrl(to)))
    } else {
      redirectToRoot
    }
  }

  /**
    * The about page
    *
    * @return
    */
  def about = Action {
    implicit request =>
      withUserOption {
        userOption =>
          {
            val aboutPage = staticPageDataService.getByName("about").getOrElse(StaticPage("about"))
            Ok(views.html.about.aboutpage(title("About"), userOption, SidebarContainer(), aboutPage.text))
          }
      }
  }

  /**
    * Shows posts containing the given tag
    *
    * @param tag
    * @return
    */
  def showTag(tag: String) = Action {
    implicit request =>
      withUserOption {
        userOption =>
          {
            val breadcrumb = List(
              homeBcItem,
              BreadcrumbItem(tag, "/blog/tag/" + tag, "icon-tag"))
            Ok(views.html.content.postListing(
              title("Tag: " + tag),
              userOption,
              breadcrumb,
              SidebarContainer(tag),
              postEnrichedDataService.getByTag(tag)))
          }
      }
  }

  /**
    * Does a full text search over all posts (titles, tags and text) and displays the matching ones.
    *
    * @return
    */
  def search = Action {
    implicit request =>
      withUserOption {
        userOption =>
          {
            searchForm.bindFromRequest().fold(
              formWithErrors => {
                redirectToRoot
              }, term => {
                val searchResult = postEnrichedDataService.search(term)
                val breadcrumb = List(
                  homeBcItem,
                  BreadcrumbItem("\"" + term + "\"", None, Some("icon-search")))
                Async {
                  searchResult map { searchResult =>
                    Ok(views.html.content.postListing(
                      title("Search: \"" + term + "\""),
                      userOption,
                      breadcrumb,
                      SidebarContainer(),
                      searchResult))
                  }
                }
              })
          }
      }
  }

  /**
    * The rss feed for the posts of this blog.
    *
    * @return
    */
  def rss = Action {
    val rssPosts = postEnrichedDataService.get(0, 10)
    val lastBuildDate = rssPosts match {
      case head :: tail => head.created.toString()
      case _ => new DateTime().toString()
    }
    // Do NOT change the xml formatting!
    Ok(<rss version="2.0">
<channel>
 <title>{ blogTitle }</title>
 <description>{ blogDescription }</description>
 <link>{ blogUrl }</link>
 <lastBuildDate>{ lastBuildDate }</lastBuildDate>
 <image>
  <url>{ rssLogoUrl }</url>
  <title>{ blogTitle }</title>
  <link>{ blogUrl }</link>
  <width>{ rssLogoWidth }</width>
  <height>{ rssLogoHeight }</height>
 </image>
{ for (post <- rssPosts) yield { <item>
  <title>{ post.title }</title>
  <description>{ post.compiledAbstract }</description>
  <link>{ blogUrl + "/" + post.relUrl }</link>
  <guid>{ post.id.get }</guid>
  <pubDate>{ post.created.toString() }</pubDate>
 </item> } }
</channel>
</rss>)
  }

  /**
    * Trys to login a user
    *
    * @return
    */
  def login = Action {
    implicit request =>
      {
        val mappedForm = adminLoginForm.bindFromRequest()
        mappedForm.fold(
          // form has errors
          formWithErrors => {
            val breadcrumb = List(homeBcItem)
            BadRequest(
              views.html.content.postListing(title(),
                None,
                breadcrumb,
                SidebarContainer(),
                indexPosts(),
                indexMoreUrl())(formWithErrors))
          },
          // form has no errors, check the credentials
          credentials =>
            tryLogin(credentials) match {
              // credentials are not valid
              case Left(msg) => {
                val breadcrumb = List(homeBcItem)
                BadRequest(
                  views.html.content.postListing(
                    title(),
                    None,
                    breadcrumb,
                    SidebarContainer(),
                    indexPosts(),
                    indexMoreUrl())(
                      mappedForm.copy(errors = List(FormError("", msg)))))
              }
              // credentials are valid
              case Right((user, requestEnricher)) =>
                requestEnricher(redirectToRoot)
            })
      }
  }

  /**
    * Logs out the user, if they are logged in
    *
    * @return
    */
  def logout = Action {
    doLogout()
  }

  private def indexPosts(): List[PostEnriched] = postEnrichedDataService.get(0, postsOnIndexPage)

  private def indexMoreUrl(): Option[String] = indexMoreUrl(postsOnIndexPage)

  private def indexMoreUrl(n: Int): Option[String] =
    postEnrichedDataService.hasMoreThan(n) match {
      case true => Some(moreBaseUrl + "/" + n)
      case false => None
    }
}
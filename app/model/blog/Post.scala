package model.blog

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import backend.util.PatternUtil
import model.DBEntity

/**
  * The post information which is stored to the database and used to generate enriched posts
  *
  * @author Stefan Bleibinhaus
  *
  */
case class Post(
  override val id: Option[String],
  override val relUrl: String,
  override val title: String,
  override val author: User,
  override val created: DateTime,
  override val updated: DateTime,
  override val text: String,
  override val tags: List[String])
    extends DBEntity with PostEssential {

}

object Post {
  private val relUrlDateFormat = DateTimeFormat.forPattern("yyyy/MM")

  def apply(id: Option[String], title: String, author: User, text: String, tags: List[String]): Post = {
    val time = new DateTime()
    new Post(id, makeRelUrl(title, time), title, author, time, time, text, tags)
  }

  def apply(title: String, author: User, text: String, tags: List[String]): Post =
    Post(None, title, author, text, tags)

  // method inspired by julienrf's tyco found at https://github.com/julienrf/tyco
  private def makeRelUrl(title: String, date: DateTime): String = {
    val p1 = relUrlDateFormat.print(date)
    val p2 = title.toLowerCase.map {
      _ match {
        case a if PatternUtil.matches(a, "[áàâä]") => 'a'
        case e if PatternUtil.matches(e, "[éèêë]") => 'e'
        case i if PatternUtil.matches(i, "[íìîï]") => 'i'
        case o if PatternUtil.matches(o, "[óòôö]") => 'o'
        case u if PatternUtil.matches(u, "[úùûü]") => 'u'
        case 'ç' => 'c'
        case nonWord if PatternUtil.matches(nonWord, "\\W") => '-'
        case c => c
      }
    }.replaceAll("-{2,}", "-").replaceAll("-+$", "").replaceAll("^-+", "")
    p1 + "/" + p2
  }
}
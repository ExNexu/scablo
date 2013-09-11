package model.blog

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import backend.engine.{ TextCompiler, TextCompilerSbtl }
import model.DBEntity

/**
  * The compiled version of a post.
  * Includes all fields which are essential for displaying them to the user.
  *
  * @author Stefan Bleibinhaus
  *
  */
case class PostEnriched(
  val id: Option[String],
  /*
   * essential fields
   */
  override val relUrl: String,
  override val title: String,
  override val author: User,
  override val created: DateTime,
  override val updated: DateTime,
  override val text: String,
  override val tags: List[String],
  override val listed: Boolean,
  /*
   * enriched fields
   */
  val createdDateRelUrl: String,
  val createdString: String,
  val updatedDateRelUrl: String,
  val updatedString: String,
  val compiledAbstract: String,
  val compiledText: String)
    extends PostEssential {

}

object PostEnriched {
  private val dateStringformat = DateTimeFormat.forPattern("MMMMM d, yyyy")
  private val dateRelUrlformat = DateTimeFormat.forPattern("yyyy/MM")

  def apply(post: Post): PostEnriched = {
    val compiled = new TextCompilerSbtl().compile(post.text)
    new PostEnriched(
      post.id,
      post.relUrl,
      post.title,
      post.author,
      post.created,
      post.updated,
      post.text,
      post.tags,
      post.visible,
      dateRelUrlformat.print(post.created),
      dateStringformat.print(post.created),
      dateRelUrlformat.print(post.updated),
      dateStringformat.print(post.updated),
      compiled._1,
      compiled._2)
  }
}

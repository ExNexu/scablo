package model.blog

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import model.DBEntity

/**
  * A file reference to store information of uploaded files to the database
  *
  * @author Stefan Bleibinhaus
  *
  */
case class FileRef(
  override val id: Option[String],
  val name: String,
  val uploaded: DateTime)
    extends DBEntity {

  private val dateStringformat = DateTimeFormat.forPattern("MMMMM d, yyyy")
  private val dateRelUrlformat = DateTimeFormat.forPattern("yyyy/MM")

  /**
    * @return The uploaded date as string
    */
  def uploadedString(): String = dateStringformat.print(uploaded)

  /**
    * @return The link associated with the upload date (all posts of the corresponding year and month)
    */
  def uploadedPostsLink(): String = "/" + dateRelUrlformat.print(uploaded)

}

object FileRef {
  def apply(name: String): FileRef = FileRef(None, name, new DateTime)
}
package model.blog

/**
  * The class used for tags. They are not associated with a post,
  * but will be generated at the start of the application from the information found in posts.
  *
  * @author Stefan Bleibinhaus
  *
  */
case class Tag(
    val name: String,
    val count: Int) {

}

object Tag {
  def apply(name: String) = new Tag(name, 1)
}
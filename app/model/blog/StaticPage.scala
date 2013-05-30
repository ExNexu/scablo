package model.blog

import model.DBEntity

/**
  * A class containing the information for static pages, e.g. the about page
  *
  * @author Stefan Bleibinhaus
  *
  */
case class StaticPage(
  override val id: Option[String],
  val name: String,
  val text: String)
    extends DBEntity {

}

object StaticPage {
  def apply(name: String): StaticPage = StaticPage(None, name, "")
}
package model.ui

/**
  * A class representing breadcrumb items shown as breadcrumb in the page
  *
  * @author Stefan Bleibinhaus
  *
  */
case class BreadcrumbItem(
    name: String,
    link: Option[String],
    icon: Option[String]) {

}

object BreadcrumbItem {

  def apply(name: String): BreadcrumbItem = BreadcrumbItem(name, None, None)

  def apply(name: String, link: String): BreadcrumbItem = BreadcrumbItem(name, Some(link), None)

  def apply(name: String, link: String, icon: String): BreadcrumbItem = BreadcrumbItem(name, Some(link), Some(icon))
}
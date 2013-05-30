package controllers

import backend.data.mongodb.service.{ FileDataServiceMongo, PostDataServiceMongo, PostEnrichedDataServiceMongo, StaticPageDataServiceMongo, TagDataServiceMongo }
import backend.data.service.{ FileDataService, PostDataService, PostEnrichedDataService, StaticPageDataService, TagDataService }
import controllers.util.{ BlogRoutes, UserRequest }
import model.ui.{ BreadcrumbItem, LoginCredentials }
import play.api.Play
import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText }
import play.api.mvc.Controller

/**
  * An abstract class, which is the base class for all other controllers used in the application.
  * It combines values and methods used in all of its subclasses.
  *
  * @author Stefan Bleibinhaus
  *
  */
abstract class BaseController extends Controller with UserRequest with BlogRoutes {
  implicit protected val adminLoginForm = Form(
    mapping(
      "Username" -> nonEmptyText,
      "Password" -> nonEmptyText)(LoginCredentials.apply)(LoginCredentials.unapplyOmitPw))

  protected val postDataService: PostDataService = PostDataServiceMongo
  protected val postEnrichedDataService: PostEnrichedDataService = PostEnrichedDataServiceMongo
  protected val tagDataService: TagDataService = TagDataServiceMongo
  protected val staticPageDataService: StaticPageDataService = StaticPageDataServiceMongo
  protected val fileDataService: FileDataService = FileDataServiceMongo

  protected val homeBcItem = BreadcrumbItem("Home", "/blog", "icon-home")

  protected val blogUrl =
    Play.current.configuration.getString("blogUrl").getOrElse("http://bleibinha.us/blog")
  protected val blogTitle =
    Play.current.configuration.getString("blogTitle").getOrElse("bleibinha.us/blog")
  protected val blogDescription =
    Play.current.configuration.getString("blogDescription").getOrElse(blogTitle)

  /**
    * Creates the page title for the current page
    *
    * @param pagename
    * @return
    */
  protected def title(pagename: String): String = pagename + " | " + blogTitle

  /**
    * Creates the page title for the current page, if the page has no name
    *
    * @return
    */
  protected def title(): String = blogTitle

  /**
    * Converts a month given as a Long to a String containing the number of the month in two digits.
    * E.g. 10 => "10", 4 => "04".
    *
    * @param month
    * @return
    */
  protected def monthToString(month: Long): String =
    if (month < 10) "0" + month else month.toString
}
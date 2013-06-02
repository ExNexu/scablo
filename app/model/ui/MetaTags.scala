package model.ui

import backend.data.mongodb.service.UserDataServiceMongo
import backend.data.service.UserDataService
import backend.data.mongodb.service.TagDataServiceMongo
import backend.data.service.TagDataService
import play.api.Play
import model.blog.PostEnriched

/**
  * Meta tags which are used to fill the meta tags in the html pages.
  *
  * @author Stefan Bleibinhaus
  *
  */
case class MetaTags(
  val description: String,
  val keywords: String,
  val author: String)

object MetaTags {
  val empty = MetaTags("", "", "")
  private val userDataService: UserDataService = UserDataServiceMongo
  private val tagDataService: TagDataService = TagDataServiceMongo
  private val blogTitle =
    Play.current.configuration.getString("blogTitle").getOrElse("bleibinha.us/blog")
  private val blogDescription =
    Play.current.configuration.getString("blogDescription").getOrElse(blogTitle)

  /**
    * Use this constructor for the Homepage
    *
    * @return
    */
  def apply(): MetaTags =
    MetaTags(
      blogDescription,
      tagDataService.keywords,
      userDataService.getUsername())

  /**
    * Use this constructor for pages which do not show a single post
    *
    * @param pagename The name of page, e.g. "About page"
    * @return
    */
  def apply(pagename: String): MetaTags =
    MetaTags(
      description(pagename),
      "",
      userDataService.getUsername())

  /**
    * Use this constructor for posts
    *
    * @param enrichedPost
    * @return
    */
  def apply(enrichedPost: PostEnriched): MetaTags = MetaTags(
    description(enrichedPost.title),
    enrichedPost.tags.mkString(","),
    userDataService.getUsername())

  private def description(pagename: String): String = blogDescription + ", " + pagename
}
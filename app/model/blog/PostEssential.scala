package model.blog

import org.joda.time.DateTime

/**
  * A trait containing all essential post fields
  *
  * @author Stefan Bleibinhaus
  *
  */
trait PostEssential {
  val relUrl: String
  val title: String
  val author: User
  val created: DateTime
  val updated: DateTime
  val text: String
  val tags: List[String]
}

object PostEssential {
  val postSortFun = (post: PostEssential) => -post.created.getMillis
}
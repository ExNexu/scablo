package backend.data.mongodb.service

import backend.data.mongodb.dao.UserDaoComponentMongo
import backend.data.service.UserDataService
import model.blog.User

/**
  * The UserDataService using MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object UserDataServiceMongo extends UserDataService with UserDaoComponentMongo {
  override protected var cachedUser: Option[User] = dao.allAsList match {
    case head :: Nil => Some(head)
    case _ => None
  }
}
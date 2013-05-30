package backend.data.mongodb.dao

import org.bson.types.ObjectId

import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject

import backend.data.dao.UserDaoComponent
import model.blog.User

/**
  * A trait implementing the UserDaoComponent for MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
trait UserDaoComponentMongo extends UserDaoComponent {
  override val dao = UserDaoMongo

  object UserDaoMongo extends BaseDaoMongo[User] with UserDao {
    override protected def collectionName = "users"

    override protected def writeConverter(user: User): DBObject = {
      val objBuilder = MongoDBObject.newBuilder
      user.id foreach {
        id => objBuilder += ("_id" -> new ObjectId(id))
      }
      objBuilder += ("name" -> user.name)
      objBuilder += ("encryptedPassword" -> user.encryptedPassword)
      objBuilder.result
    }

    override protected def readConverter(dbObject: DBObject): User = {
      new User(Some(dbObject.get("_id").toString),
        dbObject.get("name").toString,
        dbObject.get("encryptedPassword").toString)
    }

    override protected def insertId(user: User, id: String): User = user.copy(id = Some(id))
  }
}
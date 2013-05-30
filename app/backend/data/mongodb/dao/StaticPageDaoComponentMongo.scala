package backend.data.mongodb.dao

import org.bson.types.ObjectId

import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject

import backend.data.dao.StaticPageDaoComponent
import model.blog.StaticPage

/**
  * A trait implementing the StaticPageDaoComponent for MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
trait StaticPageDaoComponentMongo extends StaticPageDaoComponent {
  override val dao = StaticPageDaoMongo

  object StaticPageDaoMongo extends BaseDaoMongo[StaticPage] with StaticPageDao {
    override protected def collectionName = "staticpages"

    override protected def writeConverter(page: StaticPage): DBObject = {
      val objBuilder = MongoDBObject.newBuilder
      page.id foreach {
        id => objBuilder += ("_id" -> new ObjectId(id))
      }
      objBuilder += ("name" -> page.name)
      objBuilder += ("text" -> page.text)
      objBuilder.result
    }

    override protected def readConverter(dbObject: DBObject): StaticPage = {
      new StaticPage(Some(dbObject.get("_id").toString),
        dbObject.get("name").toString,
        dbObject.get("text").toString)
    }

    override protected def insertId(page: StaticPage, id: String): StaticPage = page.copy(id = Some(id))
  }
}
package backend.data.mongodb.dao

import org.bson.types.ObjectId
import org.joda.time.DateTime

import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject

import backend.data.dao.FileRefDaoComponent
import model.blog.FileRef

/**
  * A trait implementing the FileRefDaoComponent for MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
trait FileRefDaoComponentMongo extends FileRefDaoComponent {
  override val dao = FileRefDaoMongo

  object FileRefDaoMongo extends BaseDaoMongo[FileRef] with FileRefDao {
    override protected def collectionName = "fileRefs"

    override protected def writeConverter(file: FileRef): DBObject = {
      val objBuilder = MongoDBObject.newBuilder
      file.id foreach {
        id => objBuilder += ("_id" -> new ObjectId(id))
      }
      objBuilder += ("name" -> file.name)
      objBuilder += ("uploaded" -> file.uploaded)
      objBuilder.result
    }

    override protected def readConverter(dbObject: DBObject): FileRef = {
      new FileRef(Some(dbObject.get("_id").toString),
        dbObject.get("name").toString,
        dbObject.get("uploaded").asInstanceOf[DateTime])
    }

    override protected def insertId(file: FileRef, id: String): FileRef = file.copy(id = Some(id))
  }
}
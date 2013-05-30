package backend.data.mongodb.dao

import scala.annotation.implicitNotFound

import com.mongodb.DBObject
import com.mongodb.casbah.Imports.{ MongoDBObject, ObjectId }

import backend.data.dao.BaseDao
import backend.data.mongodb.MongoDBLayer
import model.DBEntity

/**
  * The base data access object class implementation offering a set of methods for all
  * subclasses implementing the DBEntity trait
  *
  * @author Stefan Bleibinhaus
  *
  * @param <DBEntityClass>
  */
abstract class BaseDaoMongo[DBEntityClass <: DBEntity] extends BaseDao[DBEntityClass] {
  protected def collectionName: String

  protected def writeConverter(entity: DBEntityClass): DBObject

  protected def readConverter(dbObject: DBObject): DBEntityClass

  protected def insertId(entity: DBEntityClass, id: String): DBEntityClass

  protected val mongoColl = MongoDBLayer.mongoDB(collectionName)

  override def save(entity: DBEntityClass): DBEntityClass = {
    val dbObj = writeConverter(entity)
    mongoColl.insert(dbObj)
    insertId(entity, dbObj.get("_id").toString())
  }

  override def delete(entity: DBEntityClass): Unit = {
    mongoColl.remove(writeConverter(entity))
  }

  override def delete(id: String): Unit = {
    mongoColl.remove(writeConverter(get(id).get))
  }

  override def get(id: String): Option[DBEntityClass] =
    mongoColl.findOneByID(new ObjectId(id)) match {
      case Some(x) => Option(readConverter(x))
      case None => None
    }

  override def update(entity: DBEntityClass): DBEntityClass = entity.id match {
    case None =>
      throw new IllegalArgumentException("Entity " + entity + " has no id - can not update!")
    case Some(id) =>
      val dbObj = writeConverter(entity)
      mongoColl.update(MongoDBObject("_id" -> new ObjectId(id)), dbObj)
      readConverter(dbObj)
  }

  override def saveOrUpdate(entity: DBEntityClass): DBEntityClass =
    entity.id match {
      case None => save(entity)
      case Some(id) => update(entity)
    }

  override def countEntities: Long = mongoColl.count()

  override def allAsList: List[DBEntityClass] =
    (for (dbObj <- mongoColl.find) yield readConverter(dbObj)).toList

  /**
    * Finds one entity for the given query
    *
    * @param query
    * @return
    */
  protected def findOne(query: DBObject): Option[DBEntityClass] =
    mongoColl.findOne(query) match {
      case Some(x) => Option(readConverter(x))
      case None => None
    }

  /**
    * Finds all entities for the given query
    *
    * @param query
    * @return
    */
  protected def findAsList(query: DBObject): List[DBEntityClass] =
    (for (dbObj <- mongoColl.find(query)) yield readConverter(dbObj)).toList

  /**
    * Finds all entities for the given query sorted by the given sort function
    *
    * @param query
    * @param sortFun
    * @return
    */
  protected def findAsListSorted(query: DBObject, sortFun: DBObject): List[DBEntityClass] =
    (for (dbObj <- mongoColl.find(query).sort(sortFun)) yield readConverter(dbObj)).toList

  /**
    * Finds entities for the given query sorted by the given sort function.
    * The size of the result is limited by the given limit integer.
    *
    * @param query
    * @param sortFun
    * @param limit
    * @return
    */
  protected def findAsListSortedLimit(query: DBObject, sortFun: DBObject, limit: Int): List[DBEntityClass] =
    (for (dbObj <- mongoColl.find(query).sort(sortFun).limit(limit)) yield readConverter(dbObj)).toList
}
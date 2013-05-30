package backend.data.service

import scala.reflect.ClassTag
import model.DBEntity
import play.api.Play.current
import play.api.cache.Cache
import backend.data.dao.BaseDaoComponent

/**
  * A base data service trait providing standard crud operations implemented by most data services
  *
  * @author Stefan Bleibinhaus
  *
  * @param <DBEntityClass>
  */
trait BaseDataService[DBEntityClass <: DBEntity] {
  this: BaseDaoComponent[DBEntityClass] =>

  /**
    * Saves the entity
    *
    * @param entity
    * @return The entity containing the id under which it is saved
    */
  def save(entity: DBEntityClass): DBEntityClass = dao.save(entity)

  /**
    * Deletes the entity by id
    *
    * @param id
    */
  def delete(id: String): Unit = dao.delete(id)

  /**
    * Deletes the entity
    *
    * @param entity
    */
  def delete(entity: DBEntityClass): Unit = dao.delete(entity)

  /**
    * Get an entity by Id
    *
    * @param id
    * @return
    */
  def get(id: String): Option[DBEntityClass] = dao.get(id)

  /**
    * Update the entity in the database.
    *
    * @param entity
    * @return The entity containing the id under which it is saved
    */
  def update(entity: DBEntityClass): DBEntityClass = dao.update(entity)

  /**
    * Saves or updates the entity
    *
    * @param entity
    * @return The entity containing the id under which it is saved
    */
  def saveOrUpdate(entity: DBEntityClass): DBEntityClass = dao.saveOrUpdate(entity)

  /**
    * Count the entities in the database
    *
    * @return
    */
  def countEntities: Long = dao.countEntities

  /**
    * @return All entities as list
    */
  def allAsList: List[DBEntityClass] = dao.allAsList
}
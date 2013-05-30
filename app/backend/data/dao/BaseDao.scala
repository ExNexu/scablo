package backend.data.dao

/**
  * The base dao trait serving as base trait for all other data access object traits
  *
  * @author Stefan Bleibinhaus
  *
  * @param <DBEntityClass>
  */
trait BaseDao[DBEntityClass] {
  /**
    * Saves the entity
    *
    * @param entity
    * @return The entity containing the id under which it is saved
    */
  def save(entity: DBEntityClass): DBEntityClass

  /**
    * Deletes the entity by id
    *
    * @param id
    */
  def delete(id: String): Unit

  /**
    * Deletes the entity
    *
    * @param entity
    */
  def delete(entity: DBEntityClass): Unit

  /**
    * Get an entity by Id
    *
    * @param id
    * @return
    */
  def get(id: String): Option[DBEntityClass]

  /**
    * Update the entity in the database.
    *
    * @param entity
    * @return The entity containing the id under which it is saved
    */
  def update(entity: DBEntityClass): DBEntityClass

  /**
    * Saves or updates the entity
    *
    * @param entity
    * @return The entity containing the id under which it is saved
    */
  def saveOrUpdate(entity: DBEntityClass): DBEntityClass

  /**
    * Count the entities in the database
    *
    * @return
    */
  def countEntities: Long

  /**
    * @return All entities as list
    */
  def allAsList: List[DBEntityClass]
}
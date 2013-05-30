package backend.data.dao

import model.DBEntity

/**
  * A base dao component offering the dao, which is a base dao
  *
  * @author Stefan Bleibinhaus
  *
  * @param <DBEntityClass>
  */
trait BaseDaoComponent[DBEntityClass <: DBEntity] {
  val dao: BaseDao[DBEntityClass]
}
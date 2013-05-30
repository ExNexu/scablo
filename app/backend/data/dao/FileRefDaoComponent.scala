package backend.data.dao

import model.blog.FileRef

/**
  * The dao component for file references
  *
  * @author Stefan Bleibinhaus
  *
  */
trait FileRefDaoComponent extends BaseDaoComponent[FileRef] {
  trait FileRefDao extends BaseDao[FileRef]
}
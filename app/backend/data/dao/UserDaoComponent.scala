package backend.data.dao

import model.blog.User

/**
  * The dao component for users
  *
  * @author Stefan Bleibinhaus
  *
  */
trait UserDaoComponent extends BaseDaoComponent[User] {
  trait UserDao extends BaseDao[User]
}
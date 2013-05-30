package backend.data.dao

import model.blog.Post

/**
  * The dao component for posts
  *
  * @author Stefan Bleibinhaus
  *
  */
trait PostDaoComponent extends BaseDaoComponent[Post] {
  trait PostDao extends BaseDao[Post]
}
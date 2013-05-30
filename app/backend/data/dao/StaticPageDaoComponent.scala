package backend.data.dao

import model.blog.StaticPage

/**
  * The dao component for static pages
  *
  * @author Stefan Bleibinhaus
  *
  */
trait StaticPageDaoComponent extends BaseDaoComponent[StaticPage] {
  trait StaticPageDao extends BaseDao[StaticPage]
}
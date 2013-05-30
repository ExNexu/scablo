package backend.data.service

import backend.data.dao.StaticPageDaoComponent
import model.blog.StaticPage

/**
  * The data service trait for static pages
  *
  * @author Stefan Bleibinhaus
  *
  */
trait StaticPageDataService extends BaseDataService[StaticPage] {
  this: StaticPageDaoComponent =>

  protected var pageList: List[StaticPage] = Nil

  /**
    * Gets a static page by its name
    *
    * @param name
    * @return
    */
  def getByName(name: String): Option[StaticPage] = pageList.find(_.name == name)

  override def save(page: StaticPage): StaticPage = {
    val savedPage = super.save(page)
    updatePageList()
    savedPage
  }
  override def delete(page: StaticPage): Unit = {
    super.delete(page)
    updatePageList()
  }

  override def delete(id: String): Unit =
    delete(get(id).get)

  override def update(page: StaticPage): StaticPage = {
    val savedPage = super.update(page)
    updatePageList()
    savedPage
  }
  override def saveOrUpdate(page: StaticPage): StaticPage = {
    val savedPage = super.saveOrUpdate(page)
    updatePageList()
    savedPage
  }

  protected def updatePageList(): Unit = {
    pageList = allAsList
  }
}
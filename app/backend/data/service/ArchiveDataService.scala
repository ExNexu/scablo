package backend.data.service

import scala.collection.mutable

import org.joda.time.DateTime

import model.blog.{ Post, PostEssential }
import model.ui.ArchiveItem

/**
  * The data service trait for archive items
  *
  * @author Stefan Bleibinhaus
  *
  */
trait ArchiveDataService extends PostChangeListener {
  private var archiveList: List[ArchiveItem] = Nil

  postDataService.addPostChangeListener(this)

  /**
    * @return All archive items sorted beginning with the most recent items
    */
  def archive(): List[ArchiveItem] = archiveList

  override def update(post: Post) = {
    updateArchive()
  }

  protected def postDataService: PostDataService

  protected def updateArchive(): Unit = {
    val posts = postDataService.allAsList.filter(_.listed).sortBy(PostEssential.postSortFun)
    val currentYear = new DateTime().getYear
    var newArchiveList: List[ArchiveItem] = Nil
    for (post <- posts) {
      val year = post.created.getYear
      if (year == currentYear) {
        val month = post.created.getMonthOfYear
        if (!newArchiveList.exists(a => a.year == year && a.month.getOrElse(-1) == month)) {
          newArchiveList = ArchiveItem(year, month, posts.count(
            p => p.created.getYear == year && p.created.getMonthOfYear == month)) :: newArchiveList
        }
      } else {
        if (!newArchiveList.exists(_.year == year)) {
          newArchiveList = ArchiveItem(year, posts.count(
            p => p.created.getYear == year)) :: newArchiveList
        }
      }
    }
    archiveList = newArchiveList.reverse
  }
}

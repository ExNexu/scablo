package model.ui

import backend.data.mongodb.service.TagDataServiceMongo
import backend.data.service.TagDataService
import backend.data.service.ArchiveDataService
import backend.data.mongodb.service.ArchiveDataServiceMongo

/**
  * Container class used for creating elements in the sidebar
  *
  * @author Stefan Bleibinhaus
  *
  */
case class SidebarContainer(
    /*
     * Tags
     */
    val allTags: List[model.blog.Tag],
    val bigTagMinCount: Int,
    val middleTagMinCount: Int,
    val activeTag: Option[String],
    /*
     * Archive
     */
    val archive: List[ArchiveItem]) {

}

object SidebarContainer {
  private val tagDataService: TagDataService = TagDataServiceMongo
  private val archiveDataService: ArchiveDataService = ArchiveDataServiceMongo

  def apply(): SidebarContainer =
    SidebarContainer(
      tagDataService.tags,
      tagDataService.bigTagMinCount,
      tagDataService.middleTagMinCount,
      None,
      archiveDataService.archive)

  def apply(activeTag: String): SidebarContainer =
    SidebarContainer(
      tagDataService.tags,
      tagDataService.bigTagMinCount,
      tagDataService.middleTagMinCount,
      Some(activeTag),
      archiveDataService.archive)
}
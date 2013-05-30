package backend.data.mongodb.service

import backend.data.service.ArchiveDataService
import backend.data.service.PostDataService

/**
  * The ArchiveDataService using MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object ArchiveDataServiceMongo extends ArchiveDataService {
  override protected def postDataService: PostDataService = PostDataServiceMongo

  updateArchive()
}
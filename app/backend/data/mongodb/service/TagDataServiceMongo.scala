package backend.data.mongodb.service

import backend.data.service.TagDataService
import backend.data.service.PostDataService

/**
  * The TagDataService using MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object TagDataServiceMongo extends TagDataService {
  override protected def postDataService: PostDataService = PostDataServiceMongo
}
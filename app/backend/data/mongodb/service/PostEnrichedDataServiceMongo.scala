package backend.data.mongodb.service

import backend.data.service.PostEnrichedDataService
import backend.data.service.PostDataService

/**
  * The PostEnrichedDataService using MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object PostEnrichedDataServiceMongo extends PostEnrichedDataService {
  override protected def postDataService: PostDataService = PostDataServiceMongo
}
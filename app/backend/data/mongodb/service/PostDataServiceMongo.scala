package backend.data.mongodb.service

import backend.data.mongodb.dao.PostDaoComponentMongo
import backend.data.service.PostDataService

/**
  * The PostDataService using MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object PostDataServiceMongo extends PostDataService with PostDaoComponentMongo
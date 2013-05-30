package backend.data.mongodb.service

import backend.data.mongodb.dao.StaticPageDaoComponentMongo
import backend.data.service.StaticPageDataService

/**
  * The StaticPageDataService using MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object StaticPageDataServiceMongo extends StaticPageDataService with StaticPageDaoComponentMongo {
  updatePageList()
}
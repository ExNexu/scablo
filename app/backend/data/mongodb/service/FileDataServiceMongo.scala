package backend.data.mongodb.service

import backend.data.mongodb.dao.FileRefDaoComponentMongo
import backend.data.service.FileDataService

/**
  * The FileDataService using MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object FileDataServiceMongo extends FileDataService with FileRefDaoComponentMongo {
  updateFilesList()
}
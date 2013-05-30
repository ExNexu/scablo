package backend.data.mongodb

import com.mongodb.casbah.Imports._

/**
  * The layer of MongoDB
  *
  * @author Stefan Bleibinhaus
  *
  */
object MongoDBLayer {
  val mongoDB = getMongoDB()

  private def getMongoDB(): MongoDB = {
    // the collection for storage will be called scablo
    val mongoClient = MongoClient()("scablo")
    // makes sure that stuff is written to the database
    mongoClient.setWriteConcern(WriteConcern.Safe)
    mongoClient
  }
}
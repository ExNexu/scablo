package model

/**
  * A trait for all classes, which can be stored to the database
  *
  * @author Stefan Bleibinhaus
  *
  */
trait DBEntity {
  val id: Option[String]
}
package backend.data.service

import java.io.File

import scala.concurrent.{ Future, blocking, future }
import scala.concurrent.ExecutionContext.Implicits.global

import backend.data.dao.FileRefDaoComponent
import model.blog.FileRef
import play.api.Play
import play.api.libs.Files.TemporaryFile

/**
  * The data service trait for files
  *
  * @author Stefan Bleibinhaus
  *
  */
trait FileDataService {
  this: FileRefDaoComponent =>

  protected var filesList: List[FileRef] = Nil
  private val filesfolder = Play.current.configuration.getString("filesfolder").getOrElse("filesfolder")

  /**
    * @return All file references as list
    */
  def files(): List[FileRef] = filesList

  /**
    * Gets a file by its name
    *
    * @param name
    * @return
    */
  def getByName(name: String): Future[Option[File]] =
    future {
      filesList.find(_.name == name) match {
        case Some(_) => {
          blocking { Some(new File(filesfolder + "/" + name)) }
        }
        case None => None
      }
    }

  /**
    * Saves a file under a suggested name
    *
    * @param name
    * @param file
    * @return the name under which the file is saved
    */
  def save(name: String, file: TemporaryFile): String =
    filesList.find(_.name == name) match {
      case Some(_) => save("_" + name, file)
      case None => {
        save(FileRef(name))
        file.moveTo(new File(filesfolder + "/" + name))
        name
      }
    }

  /**
    * Removes (deletes) the file of the given name
    *
    * @param name
    */
  def remove(name: String): Unit =
    filesList.find(_.name == name) match {
      case Some(fileRef) => {
        new File(filesfolder + "/" + name).delete()
        delete(fileRef)
      }
      case None => None
    }

  protected def save(file: FileRef): FileRef = {
    val savedFile = dao.save(file)
    updateFilesList()
    savedFile
  }

  protected def delete(file: FileRef): Unit = {
    dao.delete(file)
    updateFilesList()
  }

  protected def allAsList: List[FileRef] = dao.allAsList

  protected def updateFilesList(): Unit = {
    filesList = allAsList.sortBy(-_.uploaded.getMillis)
  }
}
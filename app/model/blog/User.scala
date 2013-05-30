package model.blog

import model.DBEntity
import org.mindrot.jbcrypt.BCrypt

/**
  * The user class. In the current implementation there is only one user which is also the admin.
  *
  * @author Stefan Bleibinhaus
  *
  */
case class User(
    override val id: Option[String],
    val name: String,
    val encryptedPassword: String) extends DBEntity {

}

object User {
  def apply(name: String, password: String) =
    new User(None, name, createEncryptedPassword(password))

  /**
    * checks for correctness of the password
    *
    * @param user
    * @param password
    * @return
    */
  def checkPassword(user: User, password: String): Boolean =
    checkPassword(password, user.encryptedPassword)

  private def createEncryptedPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }

  private def checkPassword(candidate: String, encryptedPassword: String): Boolean = {
    BCrypt.checkpw(candidate, encryptedPassword)
  }
}
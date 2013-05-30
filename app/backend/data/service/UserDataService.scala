package backend.data.service

import backend.data.dao.UserDaoComponent
import model.blog.User

/**
  * The data service trait for the user.
  * The application currently only allows one user, which is also the admin.
  *
  * @author Stefan Bleibinhaus
  *
  */
// TODO: Remove BaseDataService!?
trait UserDataService extends BaseDataService[User] {
  this: UserDaoComponent =>

  protected var cachedUser: Option[User]

  override def get(id: String): Option[User] =
    cachedUser match {
      case Some(user) if user.id.get == id => Some(user)
      case _ => None
    }

  /**
    * Trys the login of a user by their name and password.
    * If there is already a user existing, the name and password will be checked for them,
    * if not, this method functions as a register method, registering the user
    * under the given name and password.
    *
    * @param name
    * @param password
    * @return
    */
  def login(name: String, password: String): Option[User] =
    cachedUser match {
      case Some(user) =>
        if (user.name == name && User.checkPassword(user, password))
          Some(user)
        else
          None
      case None =>
        val newUser = Some(dao.save(User(name, password)))
        cachedUser = newUser
        newUser
    }
}
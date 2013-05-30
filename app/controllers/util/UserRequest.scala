package controllers.util

import backend.data.mongodb.service.UserDataServiceMongo
import backend.data.service.UserDataService
import model.blog.User
import play.api.mvc.{ Controller, Result, Session, SimpleResult }
import model.ui.LoginCredentials

/**
  * This trait is abstracting all user lookup, login and logout processes.
  * The user information will be stored in the session of the client.
  *
  * @author Stefan Bleibinhaus
  *
  */
trait UserRequest extends Controller with BlogRoutes {
  private val userDataService: UserDataService = UserDataServiceMongo
  private val USERID_SESSION_IDENTIFIER = "userId"

  /**
    * Trys to login the user with the given login credentials
    *
    * @param credentials the login credentials
    * @param session Implicit session
    * @return An either, containing a failure string for the left side and
    * a function for the right side in case of success.
    * The function on the right side takes a SimpleResult and returns a result,
    * which adds the usersession to the given SimpleResult.
    */
  protected def tryLogin(
    credentials: LoginCredentials)(
      implicit session: Session): Either[String, (User, SimpleResult[_] => Result)] =
    userDataService.login(credentials.name, credentials.password) match {
      case Some(user) =>
        Right((user,
          (result: SimpleResult[_]) =>
            result.withSession(session + (USERID_SESSION_IDENTIFIER -> user.id.get))))
      case None => Left("Wrong password")
    }

  /**
    * Logs out the user
    * @param simpleResult Optional, defaults to redirectToRoot
    * @return The Result
    */
  protected def doLogout(simpleResult: SimpleResult[_] = redirectToRoot): Result =
    simpleResult.withNewSession

  /**
    * A method which makes sure, that there is a user in the given function userFn
    *
    * @param userFn The function which will be called when a user is defined
    * @param noUserResult Optional, the Result which will be called when there is no user defined
    * @param session Implicit session
    * @return The Result
    */
  protected def withUser(
    userFn: User => Result, noUserResult: Result = noUserFn())(
      implicit session: Session): Result =
    getUser() match {
      case Some(user) => userFn(user)
      case None => noUserResult
    }

  /**
    * Gives the current logged in user (or None) to the given function fn
    *
    * @param fn The function which takes an user option
    * @param session Implicit session
    * @return The Result
    */
  protected def withUserOption(fn: Option[User] => Result)(implicit session: Session): Result =
    fn(getUser())

  private def noUserFn(): Result = doLogout()

  private def getUser()(implicit session: Session): Option[User] =
    session.get(USERID_SESSION_IDENTIFIER) match {
      case Some(userId) => userDataService.get(userId)
      case None => None
    }
}
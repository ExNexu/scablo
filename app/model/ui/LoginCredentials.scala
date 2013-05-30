package model.ui

/**
  * Login credentials of a user, when they are trying to log in to the page
  *
  * @author Stefan Bleibinhaus
  *
  */
case class LoginCredentials(
    val name: String,
    val password: String) {

}

object LoginCredentials {
  def unapplyOmitPw(credentials: LoginCredentials): Option[(String, String)] =
    Some(credentials.name, "")
}
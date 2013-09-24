package controllers.util

import play.mvc.Results.Redirect
import play.api.mvc.Controller
import play.api.mvc.SimpleResult
import play.api.Play

/**
  * A trait offering redirects to major parts of the application.
  *
  * @author Stefan Bleibinhaus
  *
  */
// TODO: Use play's reverse routing
trait BlogRoutes extends Controller {
  protected val redirectToRoot: SimpleResult = Redirect("/blog")
  protected val redirectToAbout: SimpleResult = Redirect("/blog/about")
  protected val redirectToAdmin: SimpleResult = Redirect("/blog/admin")
}

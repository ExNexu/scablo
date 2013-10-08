package test

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

import org.specs2.mutable.Specification

import akka.util.Timeout
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import play.api.test.Helpers.OK
import play.api.test.Helpers.contentAsString
import play.api.test.Helpers.contentType
import play.api.test.Helpers.route
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import play.api.test.Helpers.writeableOf_AnyContentAsEmpty

class ApplicationSpec extends Specification {
  implicit val timeout = Timeout(FiniteDuration(3, TimeUnit.SECONDS))

  "Application" should {

    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone
      }
    }

    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/blog")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain("bleibinha.us")
      }
    }
  }
}
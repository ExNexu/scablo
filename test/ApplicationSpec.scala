package test

import org.specs2.mutable.Specification

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
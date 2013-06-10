package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws.WS

/**
 * TODO: add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends Specification {

  "Application" should {
    "run in a server" in new WithServer {
      await(WS.url("http://localhost:" + port + "/blog").get).status must equalTo(OK)
      await(WS.url("http://localhost:" + port + "/blog/about").get).status must equalTo(OK)
    }
  }
}
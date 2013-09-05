package test

import org.specs2.mutable.Specification

import play.api.libs.ws.WS
import play.api.test.Helpers.OK
import play.api.test.Helpers.await
import play.api.test.WithServer

class IntegrationSpec extends Specification {

  "Application" should {
    "run in a server" in new WithServer {
      await(WS.url("http://localhost:" + port + "/blog").get).status must equalTo(OK)
      await(WS.url("http://localhost:" + port + "/blog/about").get).status must equalTo(OK)
    }
  }
}
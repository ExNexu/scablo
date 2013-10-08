package test

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

import org.specs2.mutable.Specification

import akka.util.Timeout
import play.api.libs.ws.WS
import play.api.test.Helpers.OK
import play.api.test.Helpers.{await => awaitResponse}
import play.api.test.WithServer

class IntegrationSpec extends Specification {
  implicit val timeout = Timeout(FiniteDuration(3, TimeUnit.SECONDS))

  "Application" should {
    "run in a server" in new WithServer {
      awaitResponse(WS.url("http://localhost:" + port + "/blog").get).status must equalTo(OK)
      awaitResponse(WS.url("http://localhost:" + port + "/blog/about").get).status must equalTo(OK)
    }
  }
}
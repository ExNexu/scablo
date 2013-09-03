package backend.engine

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class TextCompilerSbtlTest extends Specification {

  "TextCompilerSbtl" should {
    "be able to compile a simple sbtl text" in {
      running(FakeApplication()) {
        val encoded = new TextCompilerSbtl().compile("""<sbtl>
  <abstract>
    abstract
  </abstract>
  <text>
    <h1>header</h1>
    <p>text</p>
  </text>
</sbtl>""")

        encoded._1.trim() must equalTo("abstract")
        encoded._2.trim() must equalTo("""<div class="abstract">
    abstract
  </div><hr /><div class="contents"><h4>Contents</h4><ul><li class="h1"><a href="#header-1" class="l1header">
            1 header
          </a></li></ul></div><hr /><div class="text">
    <h4><a name="header-1"></a> 1 header</h4>
    <p>text</p>
  </div>""")
      }
    }
  }

}
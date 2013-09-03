package backend.engine

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class TextCompilerSbtlTest extends Specification {

  "TextCompilerSbtl" should {
    "be able to compile a simple sbtl text" in {
      running(FakeApplication()) {
        val textCompiler: TextCompiler = new TextCompilerSbtl()
        val encoded = textCompiler.compile("""<sbtl>
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
    "be able to compile a complex sbtl text" in {
      running(FakeApplication()) {
        val textCompiler: TextCompiler = new TextCompilerSbtl()
        val encoded = textCompiler.compile("""<sbtl>
  <abstract>
    abstract<b>b</b><i>i</i><b><i>bi</i></b><ref link="https://github.com/ExNexu/scablo" author="au">la<b>b</b><i>i</i><b><i>bi</i></b></ref><ref title="sad" author="au">la<b>b</b><i>i</i><b><i>bi</i></b></ref>
  </abstract>
  <text>
    <h1>header1</h1>
    <p>la<b>b</b><i>i</i><b><i>bi</i></b><footnote>f<b>b</b><i>i</i><b><i>bi</i></b><intref name="h21"><b>b</b><i>i</i><b><i>bi</i></b></intref><footnote>f<b>b</b><i>i</i><b><i>bi</i></b><intref name="h2">removed</intref></footnote><ref link="https://github.com/ExNexu/scablo">la<b>b</b><i>i</i><b><i>bi</i></b></ref></footnote></p>
    <h1 name="h2">header2</h1>
    <ul>
      <li><ref title="t" author="a" link="https://github.com/ExNexu/scablo">asd<footnote>f<b>b</b><i>i</i><b><i>bi</i></b><intref name="h2"></intref></footnote></ref></li>
      <li><img src="https://github.com/ExNexu/scablo"></img></li>
      <li><img name="img" src="https://github.com/ExNexu/scablo"></img></li>
    </ul>
    <p><intref name="h21"></intref>-<intref name="img"></intref>-<intref name="img2"></intref>-<intref name="gist"></intref></p>
    <p><b>b</b><i>i</i><b><i>bi</i></b></p>
    <h2 name="h21">header21</h2>
    <img src="https://github.com/ExNexu/scablo"></img>
    <img name="img2" src="https://github.com/ExNexu/scablo"></img>
    <gist src="https://github.com/ExNexu/scablo" name="gist"></gist>
    <p><b>b</b><i>i</i><b><i>bi</i></b>
    <ref link="https://github.com/ExNexu/scablo" author="au">la<b>b</b><i>i</i><b><i>bi</i></b></ref><ref title="sad" author="au">la<b>b</b><i>i</i><b><i>bi</i></b></ref></p>
  </text>
</sbtl>""")

        encoded._1.trim() must equalTo("""abstract<b>b</b><i>i</i><b><i>bi</i></b><a href="https://github.com/ExNexu/scablo" title="https://github.com/ExNexu/scablo by au" target="_blank">
             la<b>b</b><i>i</i><b><i>bi</i></b>
             <i class="icon-external-link"></i>
            </a>la<b>b</b><i>i</i><b><i>bi</i></b>""")
        encoded._2.trim() must equalTo("""<div class="abstract">
    abstract<b>b</b><i>i</i><b><i>bi</i></b><a href="https://github.com/ExNexu/scablo" title="https://github.com/ExNexu/scablo by au" target="_blank">
          la<b>b</b><i>i</i><b><i>bi</i></b>
          [1] <i class="icon-external-link"></i>
        </a><a href="#ref-2" title="sad by au">la<b>b</b><i>i</i><b><i>bi</i></b> [2]</a>
  </div><hr /><div class="contents"><h4>Contents</h4><ul><li class="h1"><a href="#header-1" class="l1header">
            1 header1
          </a></li><li class="h1"><a href="#h2" class="l1header">
            2 header2
          </a></li><ul><li class="h2"><a href="#h21" class="l2header">
                2.1 header21
              </a></li></ul><li class="h1"><a href="#header-3" class="l1header">
            3 References
          </a></li></ul></div><hr /><div class="text">
    <h4><a name="header-1"></a> 1 header1</h4>
    <p>la<b>b</b><i>i</i><b><i>bi</i></b><a href="#footnote-1"><sup>1</sup></a></p>
    <h4><a name="h2"></a> 2 header2</h4>
    <ul>
      <li><a href="https://github.com/ExNexu/scablo" title="t by a (https://github.com/ExNexu/scablo)" target="_blank">
          asd<a href="#footnote-3"><sup>3</sup></a>
          [4] <i class="icon-external-link"></i>
        </a></li>
      <li><div class="img"><a href="https://github.com/ExNexu/scablo" target="_blank"><img src="https://github.com/ExNexu/scablo" alt="Image 1" /></a> <div class="imgTitleDescr">Image 1</div></div></li>
      <li><div class="img"><a name="img"></a><a href="https://github.com/ExNexu/scablo" target="_blank">
                                                <img src="https://github.com/ExNexu/scablo" alt="Image 2" />
                                              </a> <div class="imgTitleDescr">Image 2</div></div></li>
    </ul>
    <p><a href="#h21" title="2.1 header21"><i>2.1 header21</i></a>-<a href="#img" title="Image 2"><i>Image 2</i></a>-<a href="#img2" title="Image 4"><i>Image 4</i></a>-<a href="#gist" title="Code snippet 1"><i>Code snippet 1</i></a></p>
    <p><b>b</b><i>i</i><b><i>bi</i></b></p>
    <h5><a name="h21"></a> 2.1 header21</h5>
    <div class="img"><a href="https://github.com/ExNexu/scablo" target="_blank"><img src="https://github.com/ExNexu/scablo" alt="Image 3" /></a> <div class="imgTitleDescr">Image 3</div></div>
    <div class="img"><a name="img2"></a><a href="https://github.com/ExNexu/scablo" target="_blank">
                                                <img src="https://github.com/ExNexu/scablo" alt="Image 4" />
                                              </a> <div class="imgTitleDescr">Image 4</div></div>
    <div class="code">
      <div class="gistCode">
        <a name="gist"></a><script src="https://github.com/ExNexu/scablo"></script>
      </div>
      <div class="codeTitleDescr">
        <div class="codeTitleDescr">Code snippet 1</div>
      </div>
    </div>
    <p><b>b</b><i>i</i><b><i>bi</i></b>
    <a href="https://github.com/ExNexu/scablo" title="https://github.com/ExNexu/scablo by au" target="_blank">
          la<b>b</b><i>i</i><b><i>bi</i></b>
          [1] <i class="icon-external-link"></i>
        </a><a href="#ref-2" title="sad by au">la<b>b</b><i>i</i><b><i>bi</i></b> [2]</a></p>
  </div><hr /><div class="footnotes"><p class="footnote">
                <a name="footnote-1"></a>
                <sup>1</sup>  f<b>b</b><i>i</i><b><i>bi</i></b><a href="#h21" title="2.1 header21"><i>2.1 header21</i></a><a href="#footnote-2"><sup>2</sup></a><a href="https://github.com/ExNexu/scablo" title="https://github.com/ExNexu/scablo" target="_blank">
          la<b>b</b><i>i</i><b><i>bi</i></b>
          [3] <i class="icon-external-link"></i>
        </a>
              </p><p class="footnote">
                <a name="footnote-2"></a>
                <sup>2</sup>  f<b>b</b><i>i</i><b><i>bi</i></b><a href="#h2" title="2 header2"><i>2 header2</i></a>
              </p><p class="footnote">
                <a name="footnote-3"></a>
                <sup>3</sup>  f<b>b</b><i>i</i><b><i>bi</i></b><a href="#h2" title="2 header2"><i>2 header2</i></a>
              </p></div><hr /><div class="references">
      <h4><a name="header-3"></a> 3 References</h4> <ul><li class="ref">
              [1] 
              <a href="https://github.com/ExNexu/scablo" title="https://github.com/ExNexu/scablo by au" target="_blank">
                https://github.com/ExNexu/scablo by au <i class="icon-external-link"></i>
              </a>
            </li><li class="ref">
              <a name="ref-2"></a>[2]  sad by au
            </li><li class="ref">
              [3] 
              <a href="https://github.com/ExNexu/scablo" title="https://github.com/ExNexu/scablo" target="_blank">
                https://github.com/ExNexu/scablo <i class="icon-external-link"></i>
              </a>
            </li><li class="ref">
              [4] 
              <a href="https://github.com/ExNexu/scablo" title="t by a (https://github.com/ExNexu/scablo)" target="_blank">
                t by a (https://github.com/ExNexu/scablo) <i class="icon-external-link"></i>
              </a>
            </li></ul>
    </div>""")
      }
    }
  }

}
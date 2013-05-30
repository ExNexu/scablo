import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scablo"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "org.mongodb" %% "casbah" % "2.6.0",
    "com.github.nscala-time" %% "nscala-time" % "0.4.0",
    "org.mindrot" % "jbcrypt" % "0.3m"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
	lessEntryPoints <<= baseDirectory((base: File) =>
	  	base / "app" / "assets" / "stylesheets" / "bootstrap" ** "bootstrap.less"
	    +++ base / "app" / "assets" / "stylesheets" / "bootstrap" ** "responsive.less"
	    +++ base / "app" / "assets" / "stylesheets" / "codemirror" ** "codemirror.less"
	    +++ base / "app" / "assets" / "stylesheets" / "codemirror" ** "show-hint.less"
	    +++ base / "app" / "assets" / "stylesheets" / "jqueryui" ** "jquery-ui.less"
	    +++ base / "app" / "assets" / "stylesheets" / "jasny" ** "bootstrap-fileupload.less"
	    +++ base / "app" / "assets" / "stylesheets" ** "admin.less"
	    +++ base / "app" / "assets" / "stylesheets" ** "main.less"),
	javascriptEntryPoints <<= baseDirectory(base =>
        base / "app" / "assets" / "javascripts" / "codemirror" ** "*.js"
        +++ base / "app" / "assets" / "javascripts" / "jasny" ** "*.js"
    )
  )

}

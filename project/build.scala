import sbt._

object Builds extends sbt.Build {
  import Keys._

  /** Aggregates tasks for all projects */
  lazy val root = Project(
    "dispatch-all", file("."), settings =
      Defaults.defaultSettings ++ Common.settings ++ Seq(
        ls.Plugin.LsKeys.skipWrite := true,
        publish := { }
      )
  ).aggregate(core, liftjson, jsoup, tagsoup, json4sJackson, json4sNative)

  def module(name: String) =
    Project(name,
      file(name.replace("-", "")),
      settings = Defaults.defaultSettings ++
        Common.settings ++
        Common.testSettings)
      .dependsOn(ufcheck % "test->test")

  lazy val core = module("core")

  lazy val liftjson = module("lift-json")
    .dependsOn(core)
    .dependsOn(core % "test->test")

  lazy val json4sJackson = module("json4s-jackson")
    .dependsOn(core)
    .dependsOn(core % "test->test")

  lazy val json4sNative = module("json4s-native")
    .dependsOn(core)
    .dependsOn(core % "test->test")

  lazy val jsoup = module("jsoup")
    .dependsOn(core)
    .dependsOn(core % "test->test")

  lazy val tagsoup = module("tagsoup")
    .dependsOn(core)
    .dependsOn(core % "test->test")

  /** Util module for using unfiltered with scalacheck */
  lazy val ufcheck = Project(
    "ufcheck", file("ufcheck")
  ).dependsOn(scalacheck % "test->compile")

  lazy val scalacheck = RootProject(
    uri("git://github.com/n8han/scalacheck.git#3aad8be")
  )
}


object Common {
  import Keys._

  val testSettings:Seq[Setting[_]] = Seq(
    testOptions in Test += Tests.Cleanup { loader =>
      val c = loader.loadClass("unfiltered.spec.Cleanup$")
      c.getMethod("cleanup").invoke(c.getField("MODULE$").get(c))
    }
  )

  val settings: Seq[Setting[_]] = ls.Plugin.lsSettings ++ Seq(
    version := "0.9.4-Future",

    crossScalaVersions :=
      Seq("2.8.1", "2.8.2", "2.9.0-1", "2.9.1", "2.9.1-1", "2.9.2", "2.10.0-RC5"),

    organization := "net.databinder.dispatch",

    homepage :=
      Some(new java.net.URL("http://reboot.databinder.net/")),

    publishMavenStyle := true,

    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },

    publishArtifact in Test := false,

    licenses := Seq("LGPL v3" -> url("http://www.gnu.org/licenses/lgpl.txt")),

    pomExtra := (
      <scm>
        <url>git@github.com:dispatch/reboot.git</url>
        <connection>scm:git:git@github.com:dispatch/reboot.git</connection>
      </scm>
        <developers>
          <developer>
            <id>n8han</id>
            <name>Nathan Hamblen</name>
            <url>http://twitter.com/n8han</url>
          </developer>
        </developers>)
  )
}


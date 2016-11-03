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
    ).aggregate(core, jsoup, tagsoup, // liftjson - no 2.12 artifact
      json4sJackson, json4sNative)

  def module(name: String, settings: Seq[Def.Setting[_]] = Seq.empty) =
    Project(name,
            file(name.replace("-", "")),
            settings = Defaults.defaultSettings ++
              Common.settings ++
              Common.testSettings ++
              settings)
      .dependsOn(ufcheck % "test->test")

  lazy val core = module("core", xmlDependency)

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
    
  lazy val xmlDependency = libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, v)) if v >= 11 =>
        Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.6")
      case _ =>
        Nil
    }
  }

  /** Util module for using unfiltered with scalacheck */
  lazy val ufcheck = Project(
    "ufcheck", file("ufcheck")
  ).settings(
    scalaVersion := Common.defaultScalaVersion
  )
}

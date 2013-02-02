import sbt._

object Builds extends sbt.Build {
  import Keys._

  /** Aggregates tasks for all projects */
  lazy val root = Project(
    "dispatch-all", file("."), settings =
      Defaults.defaultSettings ++ Common.settings ++ Seq(
        ls.Plugin.LsKeys.skipWrite := true,
        scalaVersion := "2.10.0",
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
    
  /** Util module for using unfiltered with scalacheck  */
  lazy val ufcheck = Project("ufcheck",file("ufcheck")).settings(
    Common.settings : _*
  )

}


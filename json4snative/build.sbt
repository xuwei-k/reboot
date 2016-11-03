name := "dispatch-json4s-native"

description :=
  "Dispatch module providing json4s native support"

Seq(lsSettings :_*)

val json4sVersion = "3.4.2"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-core" % json4sVersion,
  "org.json4s" %% "json4s-native" % json4sVersion
)

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v < 12 =>
      Seq("net.databinder" %% "unfiltered-netty" % "0.8.4" % "test")
    case _ =>
      // workaround circular dependency
      Nil
  }
}

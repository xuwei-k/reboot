name := "dispatch-json4s-jackson"

description :=
  "Dispatch module providing json4s support"

Seq(lsSettings :_*)

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.4.2"
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

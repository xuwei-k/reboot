libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test",
  "org.slf4j" % "slf4j-simple" % "1.6.4" % "test"
)

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v < 12 =>
      Seq("net.databinder" %% "unfiltered-netty-server" % "0.8.0" % "test")
    case _ =>
      // workaround circular dependency
      Nil
  }
}

sources in Test := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v < 12 =>
      (sources in Test).value
    case _ =>
      Nil
  }
}

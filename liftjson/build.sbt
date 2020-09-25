name := "dispatch-lift-json"

description :=
  "Dispatch module providing lift json support"

scalacOptions += "-Xfatal-warnings"

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-json" % "3.4.2",
  "org.mockito" % "mockito-core" % "3.5.13" % "test"
)

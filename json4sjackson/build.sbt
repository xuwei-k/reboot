name := "dispatch-json4s-jackson"

description :=
  "Dispatch module providing json4s support"

seq(lsSettings :_*)

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.1.0",
  "net.databinder" %% "unfiltered-netty" % "0.6.5" % "test"
)


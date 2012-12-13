name := "json4s-jackson"

description :=
  "Dispatch module providing json4s support"

seq(lsSettings :_*)

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.0.0" cross CrossVersion.binaryMapped {
                                                                                                                  case "2.10.0-RC5" => "2.9.2"
                                                                                                                  case x => x
                                                                                                                },
  "net.databinder" %% "unfiltered-netty" % "0.6.4" % "test" cross CrossVersion.binaryMapped {
                                                                                                                                 case "2.10.0-RC5" => "2.9.2"
                                                                                                                                 case x => x
                                                                                                                               }
)



name := "json4s-native"

description :=
  "Dispatch module providing json4s native support"

seq(lsSettings :_*)

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-core" % "3.0.0" cross CrossVersion.binaryMapped {
                                                                                                               case "2.10.0-RC3" => "2.9.2"
                                                                                                               case x => x
                                                                                                             },
  "org.json4s" %% "json4s-native" % "3.0.0" cross CrossVersion.binaryMapped {
                                                                                                                 case "2.10.0-RC3" => "2.9.2"
                                                                                                                 case x => x
                                                                                                               },
  "net.databinder" %% "unfiltered-netty" % "0.6.1" % "test" cross CrossVersion.binaryMapped {
                                                                                                                                 case "2.10.0-RC3" => "2.9.2"
                                                                                                                                 case x => x
                                                                                                                               }
)


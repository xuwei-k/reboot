libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-netty-server" % "0.6.4" % "test" cross CrossVersion.binaryMapped {
    case "2.10.0-RC5" => "2.9.2"
    case "2.10.0-RC4" => "2.9.2"
    case "2.10.0-RC3" => "2.9.2"
    case "2.10.0-RC2" => "2.9.2"
    case "2.10.0-RC1" => "2.9.2"
    case x => x
  }
  ,
  "org.slf4j" % "slf4j-simple" % "1.6.4" % "test"
)

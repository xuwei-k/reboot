import sbt._

object Common {
  import Keys._

  val defaultScalaVersion = "2.10.4"

  val testSettings:Seq[Setting[_]] = Seq(
    testOptions in Test ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v < 12 =>
          Seq(
            Tests.Cleanup { loader =>
              val c = loader.loadClass("unfiltered.spec.Cleanup$")
              c.getMethod("cleanup").invoke(c.getField("MODULE$").get(c))
            }
          )
        case _ =>
          Nil
      }
    }
  )

  val settings: Seq[Setting[_]] = ls.Plugin.lsSettings ++ Seq(
    version := "0.11.2",

    crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.0"),

    scalaVersion := defaultScalaVersion,

    organization := "net.databinder.dispatch",

    homepage :=
      Some(new java.net.URL("http://dispatch.databinder.net/")),

    publishMavenStyle := true,

    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) 
        Some("snapshots" at nexus + "content/repositories/snapshots") 
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },

    publishArtifact in Test := false,

    licenses := Seq("LGPL v3" -> url("http://www.gnu.org/licenses/lgpl.txt")),

    pomExtra := (
      <scm>
        <url>git@github.com:dispatch/reboot.git</url>
        <connection>scm:git:git@github.com:dispatch/reboot.git</connection>
      </scm>
      <developers>
        <developer>
          <id>n8han</id>
          <name>Nathan Hamblen</name>
          <url>http://twitter.com/n8han</url>
        </developer>
      </developers>)
  )
}

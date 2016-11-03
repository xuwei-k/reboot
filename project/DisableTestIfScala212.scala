package dispatch.build

import sbt._, Keys._

// workaround circular dependency
// TODO remove this when unfiltered_2.12 released
object DisableTestIfScala212 extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    sources in Test := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v < 12 =>
          (sources in Test).value
        case _ =>
          Nil
      }
    }
  )

}

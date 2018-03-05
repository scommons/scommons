package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._

object ScommonsClientCore extends ScalaJsModule {

  override val id: String = "scommons-client-core"

  override def definition: Project = {
    super.definition
      .settings(
        description := "Core Scala.js, React.js UI utilities and components"
      )
  }

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Nil

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.sjsReactJs.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scalaTestJs.value,
    TestLibs.scalaMockJs.value
  ).map(_ % "test"))
}

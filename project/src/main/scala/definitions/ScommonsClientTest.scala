package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._

import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object ScommonsClientTest extends ScalaJsModule {

  override val id: String = "scommons-client-test"

  override def definition: Project = {
    super.definition
      .settings(
        description := "Common Scala.js, React.js testing utilities and components",

        npmDependencies in Compile ++= Seq(
          "react-addons-test-utils" -> "15.6.0",
          "react-test-renderer" -> "15.6.1"
        )
      )
  }

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Nil

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.sjsReactJs.value,
    Libs.scalajsDom.value,
    TestLibs.scalaTestJs.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Nil)
}

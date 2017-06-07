package common

import org.sbtidea.SbtIdeaPlugin.ideaExcludeFolders
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.coverageMinimum

object Common {

  val settings: Seq[Setting[_]] = Seq(
    organization := "com.github.viktor-podzigun",
    //maintainer := "viktor.podzigun@gmail.com",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      //"-Xcheckinit",
      "-Xfatal-warnings",
      "-Xlint:_",
      "-explaintypes",
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    //ivyScala := ivyScala.value map {
    //  _.copy(overrideScalaVersion = true)
    //},
    ideaExcludeFolders := List(
      ".idea"
    ),
    //when run tests with coverage: sbt clean coverage test coverageReport
    coverageMinimum := 80,
    resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )
}

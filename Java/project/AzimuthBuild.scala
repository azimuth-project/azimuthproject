import sbt._
import Keys._

object Dependencies {
  val scallop = "org.rogach" %% "scallop" % "0.3.9"
  val junit = "junit" % "junit" % "4.10" % "test"
  val scalatest = "org.scalatest" %% "scalatest" % "1.7.2" % "test"
}

object AzimuthBuild extends Build {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.azimuthproject",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.9.1",
    compileOrder := CompileOrder.JavaThenScala // use CompileOrder.Mixed for circular Java <-> Scala dependencies
  )

  import Dependencies._
  val deps = Seq(junit, scalatest)
  val commonDeps = deps
  val mathDeps = deps
  val sdeModelsDeps = deps
  val visualizationDeps = deps ++ Seq(scallop)

  lazy val root = Project(
    "azimuth",
    file("."),
    settings = buildSettings
  ) aggregate (common, math, sdeModels, visualization)

  lazy val common = Project(
    "common",
    file("azimuth-common"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= commonDeps
    )
  )

  lazy val math = Project(
    "math",
    file("azimuth-math"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= mathDeps
    )
  )

  lazy val sdeModels = Project(
    "sde-models",
    file("azimuth-sde-models"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= sdeModelsDeps
    )
  ) dependsOn (common)

  lazy val visualization = Project(
    "visualization",
    file("azimuth-visualization"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= visualizationDeps
    )
  ) dependsOn (sdeModels)
}

val Scala3 = "3.1.3" // scala-steward:off
ThisBuild / scalaVersion       := "2.13.10"
ThisBuild / crossScalaVersions := Seq("2.12.17", "2.13.10", Scala3)
ThisBuild / organization       := "com.alejandrohdezma"

addCommandAlias("ci-test", "fix --check; mdoc; +test")
addCommandAlias("ci-docs", "github; mdoc; headerCreateAll")
addCommandAlias("ci-publish", "github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .dependsOn(dummy)
  .settings(mdocOut := file("."))

lazy val dummy = module

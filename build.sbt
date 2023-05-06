val Scala3 = "3.1.3" // scala-steward:off
ThisBuild / scalaVersion       := "2.13.10"
ThisBuild / crossScalaVersions := Seq("2.12.17", "2.13.10", Scala3)
ThisBuild / organization       := "com.alejandrohdezma"

addCommandAlias("ci-test", "fix --check; mdoc; publishLocal; +test")
addCommandAlias("ci-docs", "github; mdoc; headerCreateAll")
addCommandAlias("ci-publish", "github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .dependsOn(dummy)

lazy val dummy = module
  .settings(libraryDependencies += "org.ocpsoft.prettytime" % "prettytime-nlp" % "5.0.6.Final")
  .settings(libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test)
  .settings(libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.10.0")

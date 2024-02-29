val Scala3 = "3.1.3" // scala-steward:off
ThisBuild / scalaVersion           := "2.13.13"
ThisBuild / crossScalaVersions     := Seq("2.13.13", Scala3)
ThisBuild / organization           := "com.alejandrohdezma"
ThisBuild / versionPolicyIntention := Compatibility.BinaryAndSourceCompatible

addCommandAlias("ci-test", "fix --check; versionPolicyCheck; mdoc; publishLocal; +test")
addCommandAlias("ci-docs", "github; mdoc; headerCreateAll")
addCommandAlias("ci-publish", "versionCheck; github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .dependsOn(dummy)

lazy val dummy = module
  .settings(libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test)

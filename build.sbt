ThisBuild / scalaVersion           := "2.13.15"
ThisBuild / crossScalaVersions     := Seq("2.13.15", "3.3.4")
ThisBuild / organization           := "com.alejandrohdezma"
ThisBuild / versionPolicyIntention := Compatibility.BinaryAndSourceCompatible

addCommandAlias("ci-test", "fix --check; versionPolicyCheck; mdoc; publishLocal; +test")
addCommandAlias("ci-docs", "github; mdoc; headerCreateAll")
addCommandAlias("ci-publish", "versionCheck; github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .dependsOn(dummy)

lazy val dummy = module
  .settings(libraryDependencies += "org.scalameta" %% "munit" % "1.0.3" % Test)

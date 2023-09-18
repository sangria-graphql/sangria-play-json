val isScala3 = Def.setting(
  CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
)

name := "sangria-play-json"
organization := "org.sangria-graphql"
mimaPreviousArtifacts := {
  if (isScala3.value)
    Set.empty
  else
    Set("org.sangria-graphql" %% "sangria-play-json" % "2.0.1")
}
description := "Sangria play-json marshalling"
homepage := Some(url("http://sangria-graphql.github.io/"))
licenses := Seq(
  "Apache License, ASL Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

ThisBuild / crossScalaVersions := Seq("2.12.18", "2.13.11", "3.3.0")
ThisBuild / scalaVersion := crossScalaVersions.value.last
ThisBuild / githubWorkflowPublishTargetBranches := List()
ThisBuild / githubWorkflowBuildPreamble ++= List(
  WorkflowStep.Sbt(List("mimaReportBinaryIssues"), name = Some("Check binary compatibility")),
  WorkflowStep.Sbt(List("scalafmtCheckAll"), name = Some("Check formatting"))
)

scalacOptions ++= {
  if (isScala3.value)
    Seq("-Xtarget:8")
  else
    Seq("-target:jvm-1.8")
} ++ Seq("-deprecation", "-feature")
javacOptions ++= Seq("-source", "8", "-target", "8")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria-marshalling-api" % "1.0.8",
  "com.typesafe.play" %% "play-json" % "2.10.1",
  "org.sangria-graphql" %% "sangria-marshalling-testkit" % "1.0.4" % Test,
  "org.scalatest" %% "scalatest" % "3.2.16" % Test
)

// Publishing
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

startYear := Some(2016)
organizationHomepage := Some(url("https://github.com/sangria-graphql"))
developers := Developer(
  "OlegIlyenko",
  "Oleg Ilyenko",
  "",
  url("https://github.com/OlegIlyenko")) :: Nil
scmInfo := Some(
  ScmInfo(
    browseUrl = url("https://github.com/sangria-graphql/sangria-play-json"),
    connection = "scm:git:git@github.com:sangria-graphql/sangria-play-json.git"))

// nice *magenta* prompt!
ThisBuild / shellPrompt := { state =>
  scala.Console.MAGENTA + Project.extract(state).currentRef.project + "> " + scala.Console.RESET
}

import PlayAxis._

ThisBuild / organization := "org.sangria-graphql"
ThisBuild / mimaPreviousArtifacts := Set("org.sangria-graphql" %% "sangria-play-json" % "2.0.2")
ThisBuild / description := "Sangria play-json marshalling"
ThisBuild / homepage := Some(url("https://sangria-graphql.github.io/"))
ThisBuild / licenses := Seq(
  "Apache License, ASL Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

ThisBuild / githubWorkflowPublishTargetBranches := List()
ThisBuild / githubWorkflowBuildPreamble ++= List(
  WorkflowStep.Sbt(List("mimaReportBinaryIssues"), name = Some("Check binary compatibility")),
  WorkflowStep.Sbt(List("scalafmtCheckAll"), name = Some("Check formatting"))
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

ThisBuild / startYear := Some(2016)
ThisBuild / organizationHomepage := Some(url("https://github.com/sangria-graphql"))
ThisBuild / developers := Developer(
  "OlegIlyenko",
  "Oleg Ilyenko",
  "",
  url("https://github.com/OlegIlyenko")) :: Nil
ThisBuild / scmInfo := Some(
  ScmInfo(
    browseUrl = url("https://github.com/sangria-graphql/sangria-play-json"),
    connection = "scm:git:git@github.com:sangria-graphql/sangria-play-json.git"))

// nice *magenta* prompt!
ThisBuild / shellPrompt := { state =>
  scala.Console.MAGENTA + Project.extract(state).currentRef.project + "> " + scala.Console.RESET
}

val scala212 = "2.12.18"
val scala213 = "2.13.12"
val scala3 = "3.3.1"

lazy val sangriaPlayJson = (projectMatrix in file("sangria-play-json"))
  .settings(
    name := "sangria-play-json",
    scalacOptions ++= Seq("-deprecation", "-feature"),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oFD"),
    libraryDependencies ++= Seq(
      "org.sangria-graphql" %% "sangria-marshalling-api" % "1.0.8",
      "org.sangria-graphql" %% "sangria-marshalling-testkit" % "1.0.4" % Test,
      "org.scalatest" %% "scalatest" % "3.2.17" % Test
    )
  )
  .customRow(
    scalaVersions = Seq(scala212, scala213),
    axisValues = Seq(play28, VirtualAxis.jvm),
    _.settings(
      moduleName := name.value + "-play28",
      javacOptions ++= Seq("-source", "8", "-target", "8"),
      scalacOptions ++= Seq("-target:jvm-1.8"),
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-json" % "2.8.2",
      )
    )
  )
  .customRow(
    scalaVersions = Seq(scala213, scala3),
    axisValues = Seq(play29, VirtualAxis.jvm),
    _.settings(
      moduleName := name.value + "-play29",
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-json" % "2.10.4",
      )
    )
  )
  .customRow(
    scalaVersions = Seq(scala213, scala3),
    axisValues = Seq(play30, VirtualAxis.jvm),
    _.settings(
      moduleName := name.value + "-play30",
      libraryDependencies ++= Seq(
        "org.playframework" %% "play-json" % "3.0.2",
      )
    )
  )

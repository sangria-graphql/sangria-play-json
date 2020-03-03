name := "sangria-play-json"
organization := "org.sangria-graphql"
mimaPreviousArtifacts := Set("org.sangria-graphql" %% "sangria-play-json" % "1.0.5")

description := "Sangria play-json marshalling"
homepage := Some(url("http://sangria-graphql.org"))
licenses := Seq("Apache License, ASL Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.13.1"
crossScalaVersions := Seq("2.12.10", scalaVersion.value)

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria-marshalling-api" % "1.0.4",
  "com.typesafe.play" %% "play-json" % "2.8.1",

  "org.sangria-graphql" %% "sangria-marshalling-testkit" % "1.0.2" % Test,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test)

// Publishing

releaseCrossBuild := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := (_ => false)
publishTo := Some(
  if (version.value.trim.endsWith("SNAPSHOT"))
    "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else
    "releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
startYear := Some(2016)
organizationHomepage := Some(url("https://github.com/sangria-graphql-org"))
developers := Developer("OlegIlyenko", "Oleg Ilyenko", "", url("https://github.com/OlegIlyenko")) :: Nil
scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/sangria-graphql-org/sangria-play-json.git"),
  connection = "scm:git:git@github.com:sangria-graphql-org/sangria-play-json.git"))

// nice *magenta* prompt!

shellPrompt in ThisBuild := { state =>
  scala.Console.MAGENTA + Project.extract(state).currentRef.project + "> " + scala.Console.RESET
}

name := "sangria-play-json"
organization := "org.sangria-graphql"
version := "1.0.6-SNAPSHOT"

description := "Sangria play-json marshalling"
homepage := Some(url("http://sangria-graphql.org"))
licenses := Seq("Apache License, ASL Version 2.0" → url("http://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.12.7"
crossScalaVersions := Seq("2.11.11", "2.12.7")

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria-marshalling-api" % "1.0.3",
  "com.typesafe.play" %% "play-json" % "2.6.13",

  "org.sangria-graphql" %% "sangria-marshalling-testkit" % "1.0.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test")

// Publishing

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := (_ ⇒ false)
publishTo := Some(
  if (version.value.trim.endsWith("SNAPSHOT"))
    "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else
    "releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
startYear := Some(2016)
organizationHomepage := Some(url("https://github.com/sangria-graphql"))
developers := Developer("OlegIlyenko", "Oleg Ilyenko", "", url("https://github.com/OlegIlyenko")) :: Nil
scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/sangria-graphql/sangria-play-json.git"),
  connection = "scm:git:git@github.com:sangria-graphql/sangria-play-json.git"))

// nice *magenta* prompt!

shellPrompt in ThisBuild := { state ⇒
  scala.Console.MAGENTA + Project.extract(state).currentRef.project + "> " + scala.Console.RESET
}

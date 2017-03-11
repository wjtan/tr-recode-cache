name := """play-cache-status"""

version := "0.1.0"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8")

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.1.0",
  "com.github.ben-manes.caffeine" % "caffeine" % "2.4.0"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)


//*******************************
// Maven settings
//*******************************

publishMavenStyle := true

organization := "com.reincarnation.cache"

description := "Play Status display for Caffeine Cache"

homepage := Some(url("https://github.com/wjtan/tr-recode-cache"))

licenses := Seq("Apache License" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

startYear := Some(2017)

//publishTo := {
//  val nexus = "https://oss.sonatype.org/"
//  if (isSnapshot.value)
//    Some("snapshots" at nexus + "content/repositories/snapshots")
//  else
//    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
//}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:wjtan/tr-recode-cache.git</url>
    <connection>scm:git:git@github.com:wjtan/tr-recode-cache.git</connection>
  </scm>
  <developers>
    <developer>
      <id>wjtan</id>
      <name>Tan Wen Jun</name>
      <url>https://github.com/wjtan</url>
    </developer>
  </developers>
)
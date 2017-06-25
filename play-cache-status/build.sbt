name := """play-cache-status"""

version := "0.1.1"

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.12.2")

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.1.0",
  "com.github.ben-manes.caffeine" % "caffeine" % "2.4.0"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)


//*******************************
// Maven settings
//*******************************

bintrayRepository := "maven"

bintrayPackage := "tr-recode-cache"

licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

publishMavenStyle := true

organization := "com.reincarnation.cache"

publishArtifact in Test := false

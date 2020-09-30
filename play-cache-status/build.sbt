name := """play-cache-status"""

version := "0.4.0"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.2.2",
  "com.github.ben-manes.caffeine" % "caffeine" % "2.8.5"
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

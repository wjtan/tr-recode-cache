resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.0")

// publishing to bintray
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.12")

// publishing to bintray
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
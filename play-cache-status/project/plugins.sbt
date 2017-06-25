resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0")

// publishing to bintray
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
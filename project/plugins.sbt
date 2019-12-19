lazy val root = project in file(".") dependsOn(RootProject(uri("git://github.com/kamon-io/kamon-sbt-umbrella.git#kamon-2.x")))
resolvers += Resolver.jcenterRepo
addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver" % "0.19.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")



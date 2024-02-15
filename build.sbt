name := """newScalaApi"""
organization := "com.example"

version := "1.0-SNAPSHOT"

//
libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play-json" % "5.0.0",
  "com.pauldijou" %% "jwt-core" % "5.0.0",
  "com.auth0" % "jwks-rsa" % "0.22.0"
)


assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "versions", "9", "module-info.class") => MergeStrategy.discard
  case "module-info.class" => MergeStrategy.discard
  case "play/reference-overrides.conf" => MergeStrategy.concat
  case x => (assembly / assemblyMergeStrategy).value(x)
}




lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.12"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

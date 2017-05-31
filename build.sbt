name := """fun-akka-streams"""

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.1",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.1",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.6",
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-jackson" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-xml" % "10.0.6",
  "com.typesafe.akka" %% "akka-parsing" % "10.0.6",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

name := "github-kafka-connect"

version := "0.1"

scalaVersion := "2.12.8"

val kafkaVersion = "2.1.0"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "jakarta.ws.rs" % "jakarta.ws.rs-api" % "2.1.4",
  "org.apache.kafka" % "connect-api" % kafkaVersion,
  "com.softwaremill.sttp" %% "spray-json" % "1.5.9",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
).map(_ exclude("javax.ws.rs", "javax.ws.rs-api"))

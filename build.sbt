name := "github-kafka-connect"

version := "0.3.0"

ThisBuild / scalaVersion := "2.13.18"

val kafkaVersion = "4.1.1"

ThisBuild / dependencyOverrides += "org.lz4" % "lz4-java" % "1.8.1"

ThisBuild / libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "2.0.17",
  "jakarta.ws.rs" % "jakarta.ws.rs-api" % "4.0.0",
  "org.apache.kafka" % "connect-api" % kafkaVersion,
  "com.softwaremill.sttp" %% "spray-json" % "1.7.2",
  "org.scalatest" %% "scalatest" % "3.2.19" % "test"
).map(_ exclude ("javax.ws.rs", "javax.ws.rs-api"))

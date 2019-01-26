name := "github-kafka-connect"

version := "0.1"

scalaVersion := "2.12.8"

val kafkaVersion = "2.1.0"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1",
  "org.apache.kafka" % "connect-api" % kafkaVersion,
//  "com.47deg" %% "github4s" % "0.20.0"
  "com.softwaremill.sttp" %% "play-json" % "1.5.8",
//  "org.json4s" %% "json4s-native" % "3.6.0"
)

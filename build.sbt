ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.7"

val kafkaVersion = "2.8.1"
val circeVersion = "0.14.1"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.5.2" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9" % Test

lazy val root = ( project in file ( "." ) )
  .settings (
    name := "favourite-color-kafka-scala",
    libraryDependencies ++=  Seq (
      "org.apache.kafka" % "kafka-clients" % kafkaVersion,
      "org.apache.kafka" % "kafka-streams" % kafkaVersion,
      "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.slf4j" % "slf4j-api" % "1.7.32",
      "ch.qos.logback" % "logback-core" % "1.2.10",
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      macwire,

      // Test dependencies
      scalaTest,
      "org.mockito" %% "mockito-scala" % "1.16.49" % Test
    )
  )

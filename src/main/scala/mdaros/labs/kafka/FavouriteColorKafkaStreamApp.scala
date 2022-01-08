package mdaros.labs.kafka

import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig }
import org.apache.kafka.streams.scala.serialization.Serdes
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.Properties

object FavouriteColorKafkaStreamApp extends App {

  val logger = LoggerFactory.getLogger ( FavouriteColorKafkaStreamApp.getClass )

  val kafkaConfig = new Properties ()
  kafkaConfig.put ( StreamsConfig.APPLICATION_ID_CONFIG, "favourite-color-application" )
  kafkaConfig.put ( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092" )
  kafkaConfig.put ( StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass )
  kafkaConfig.put ( StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass )

  // Disabling the cache to show all the steps during the transformations
  // DO NOT IN PRODUCTION !!
  kafkaConfig.put ( StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0" )

  val streamsBuilder = new KafkaStreamsTopologyBuilder ()
  val topology = streamsBuilder.buildTopology ()

  logger.info ( topology.describe ().toString )

  val application: KafkaStreams = new KafkaStreams ( topology, kafkaConfig )
  application.start ()

  logger.info ( "Setting up a shutdown hook" )

  val _ = sys.ShutdownHookThread {

    application.close ( Duration.ofSeconds (10 ) )
  }
}
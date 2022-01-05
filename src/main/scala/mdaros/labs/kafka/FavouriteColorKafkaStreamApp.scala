package mdaros.labs.kafka

import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig, Topology }
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.Properties

object FavouriteColorKafkaStreamApp extends App {

  val logger = LoggerFactory.getLogger ( FavouriteColorKafkaStreamApp.getClass )

  val props = new Properties ()
  props.put ( StreamsConfig.APPLICATION_ID_CONFIG, "favourite-color-application" )
  props.put ( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092" )
  props.put ( StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass )
  props.put ( StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass )

  // Disabling the cache to show all the steps during the transformations
  // DO NOT IN PRODUCTION !!
  props.put ( StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0" )

  val builder = new StreamsBuilder ()

  val inputStream: KStream [ String, String ] = builder.stream [ String, String ] ( Topics.FAVOURITE_COLOR_INPUT )

  val favouriteColorsByUser = inputStream
    .filter ( ( _, value ) => value != null && value.contains ( "," ) )                                         // Filter out invalid data
    .selectKey ( ( _, value ) => value.split ( "," ) ( 0 ).toLowerCase () )                              // Extracts the key
    .mapValues ( value => value.split ( "," ) ( 1 ).toLowerCase () )                                     // Set the values
    .filter ( ( _, color ) => color.equals ( "red" ) || color.equals ( "green" ) || color.equals ( "blue" ) )   // Takes only read, green or blue
    .to ( Topics.FAVOURITE_COLOR_BY_USER )

  val favouriteColours = builder.table [ String, String ] ( Topics.FAVOURITE_COLOR_BY_USER )
    .groupBy ( ( user, color ) => ( color, color ) )
    .count ()
    .toStream
    .to ( Topics.FAVOURITE_COLOR_OUTPUT )

  val topology: Topology = builder.build ()

  logger.info ( topology.describe ().toString )

  val application: KafkaStreams = new KafkaStreams ( topology, props )
  application.start ()

  logger.info ( "Setting up a shutdown hook" )

  sys.ShutdownHookThread {

    application.close ( Duration.ofSeconds (10 ) )
  }
}
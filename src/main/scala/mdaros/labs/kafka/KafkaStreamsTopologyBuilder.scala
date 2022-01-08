package mdaros.labs.kafka

import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._

class KafkaStreamsTopologyBuilder {

  def buildTopology (): Topology = {

    val builder = new StreamsBuilder ()

    val inputStream: KStream [ String, String ] = builder.stream [ String, String ] ( Topics.FAVOURITE_COLOR_INPUT )

    val favouriteColorsByUser = inputStream
      .filter ( ( _, value) => value != null && value.contains ( "," ) )                                          // Filter out invalid data
      .selectKey ( ( _, value) => value.split ( "," ) ( 0 ).toLowerCase () )                               // Extracts the key
      .mapValues ( value => value.split ( "," ) ( 1 ).toLowerCase () )                                     // Set the values
      .filter ( ( _, color ) => color.equals ( "red" ) || color.equals ( "green" ) || color.equals ( "blue" ) )   // Takes only read, green or blue
      .to ( Topics.FAVOURITE_COLOR_BY_USER )

    val favouriteColors = builder.table [ String, String ] ( Topics.FAVOURITE_COLOR_BY_USER )
      .groupBy ( ( user, color ) => ( color, color ) )
      .count ()
      .toStream
      .to ( Topics.FAVOURITE_COLOR_OUTPUT )

    builder.build ()
  }
}
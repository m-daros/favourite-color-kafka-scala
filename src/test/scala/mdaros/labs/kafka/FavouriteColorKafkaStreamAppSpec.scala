package mdaros.labs.kafka

import org.apache.kafka.common.serialization.{ LongDeserializer, StringDeserializer, StringSerializer }
import org.apache.kafka.streams.TopologyTestDriver
import org.scalatest.{ BeforeAndAfter, GivenWhenThen }
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

class FavouriteColorKafkaStreamAppSpec extends AnyFeatureSpec
  with GivenWhenThen
  with BeforeAndAfter
  with Matchers {

  var testDriver: TopologyTestDriver = null

  before {

    val streamsBuilder = new KafkaStreamsTopologyBuilder ()
    val topology = streamsBuilder.buildTopology ()

    testDriver = new TopologyTestDriver ( topology )
  }

  after {

    testDriver.close ()
  }

  Feature ( "Grouping and counting colors" ) {

    Scenario ( "Valid topology" ) {

      Given ( s"I have an inpout topic ${Topics.FAVOURITE_COLOR_INPUT} and an output topic ${Topics.FAVOURITE_COLOR_OUTPUT}" )

      val input = testDriver.createInputTopic [ String, String ] ( Topics.FAVOURITE_COLOR_INPUT, new StringSerializer (), new StringSerializer () )
      val output = testDriver.createOutputTopic [ String, java.lang.Long ] ( Topics.FAVOURITE_COLOR_OUTPUT, new StringDeserializer (), new LongDeserializer () )

      When ( "I send John,red to input topic" )
      input.pipeInput ( "John,red" )

      Then ( "I expect to read from output topic ( key, value ) red,1" )
      val keyValue1 = output.readKeyValue ()
      keyValue1.key shouldBe ( "red" )
      keyValue1.value shouldBe ( 1 )

      When ( "I send Albert,red to input topic" )
      input.pipeInput ( "Albert,red" )

      Then ( "I expect to read from output topic ( key, value ) red,2" )
      val keyValue2 = output.readKeyValue ()
      keyValue2.key shouldBe ( "red" )
      keyValue2.value shouldBe ( 2 )

      When ( "I send Sandra,green to input topic" )
      input.pipeInput ( "Sandra,green" )

      Then ( "I expect to read from output topic ( key, value ) green,1" )
      val keyValue3 = output.readKeyValue ()
      keyValue3.key shouldBe ( "green" )
      keyValue3.value shouldBe ( 1 )

      When ( "I send Robert,blue to input topic" )
      input.pipeInput ( "Robert,blue" )

      Then ( "I expect to read from output topic ( key, value ) blue,1" )
      val keyValue4 = output.readKeyValue ()
      keyValue4.key shouldBe ( "blue" )
      keyValue4.value shouldBe ( 1 )

      When ( "I send Sandra,blue to input topic" )
      input.pipeInput ( "Sandra,blue" )

      Then ( "I expect to read from output topic ( key, value ) green,0" )
      val keyValue5 = output.readKeyValue ()
      keyValue5.key shouldBe ( "green" )
      keyValue5.value shouldBe ( 0 )

      And ( "also ( key, value ) blue,2" )
      val keyValue6 = output.readKeyValue ()
      keyValue6.key shouldBe ( "blue" )
      keyValue6.value shouldBe ( 2 )
    }
  }
}
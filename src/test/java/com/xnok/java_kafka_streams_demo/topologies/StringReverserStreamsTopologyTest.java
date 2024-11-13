package com.xnok.java_kafka_streams_demo.topologies;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StringReverserStreamsTopologyTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;


    @BeforeEach
    public void setup() {
        // Manually create the StreamsBuilder and topology so we don't need SpringBoot for unit test
        StreamsBuilder builder = new StreamsBuilder();
        StringReverserStreamsTopology stringReverserStreamsTopology = new StringReverserStreamsTopology();
        stringReverserStreamsTopology.register(builder);
        Topology topology = builder.build();

        // Manually create properties (mimicking application.properties)
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-uppercase-topology");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

        Properties testConfig = new KafkaStreamsConfiguration(props).asProperties();

        testDriver = new TopologyTestDriver(topology, testConfig);
        inputTopic = testDriver.createInputTopic(StringReverserStreamsTopology.INPUT_TOPIC, new StringSerializer(), new StringSerializer());
        outputTopic = testDriver.createOutputTopic(StringReverserStreamsTopology.OUTPUT_TOPIC, new StringDeserializer(), new StringDeserializer());
    }


    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void shouldConvertToUpperCase() {
        inputTopic.pipeInput("key1", "hello");
        assertThat(outputTopic.readValue()).isEqualTo("olleh");

        inputTopic.pipeInput("key2", "123, 953, ABC");
        assertThat(outputTopic.readValue()).isEqualTo("CBA ,359 ,321");
    }
}
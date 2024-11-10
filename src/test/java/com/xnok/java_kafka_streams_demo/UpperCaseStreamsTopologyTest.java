package com.xnok.java_kafka_streams_demo;

import com.xnok.java_kafka_streams_demo.topologies.UpperCaseStreamsTopology;
import org.apache.kafka.common.serialization.Serdes;
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

public class UpperCaseStreamsTopologyTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;


    @BeforeEach
    public void setup() {
        // Manually create the StreamsBuilder and topology so we don't need SpringBoot for unit test
        StreamsBuilder builder = new StreamsBuilder();
        UpperCaseStreamsTopology topologyConfig = new UpperCaseStreamsTopology();
        topologyConfig.register(builder);
        Topology topology = builder.build();

        // Manually create properties (mimicking application.properties)
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-uppercase-topology");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        Properties testConfig = new KafkaStreamsConfiguration(props).asProperties();
        TopologyTestDriver testDriver = new TopologyTestDriver(topology, testConfig);

        inputTopic = testDriver.createInputTopic("input-topic", new StringSerializer(), new StringSerializer());
        outputTopic = testDriver.createOutputTopic("output-topic", new StringDeserializer(), new StringDeserializer());
    }


    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void shouldConvertToUpperCase() {
        inputTopic.pipeInput("key1", "hello");

        assertThat(outputTopic.readValue()).isEqualTo("HELLO");
    }
}

package com.xnok.java_kafka_streams_demo.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class UpperCaseStreamsTopology {

    public static final String SOURCE = "input-topic";
    public static final String SINK = "output-toipic";

    @Bean
    public KStream<String, String> uppercaseStream(StreamsBuilder builder) {
        // instantiate streams
        KStream<String, String> inputStream = builder.stream(SOURCE);
        KStream<String, String> outputStream = inputStream.mapValues(value -> value.toUpperCase());

        // Print streams to Std out
        inputStream.print(Printed.<String, String>toSysOut().withLabel(SOURCE));
        outputStream.print(Printed.<String, String>toSysOut().withLabel(SOURCE));

        // Send output stream to sink
        outputStream.to(SINK);
        return outputStream;
    }
}
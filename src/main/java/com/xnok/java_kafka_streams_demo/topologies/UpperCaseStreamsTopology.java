package com.xnok.java_kafka_streams_demo.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpperCaseStreamsTopology {

    public static final String SOURCE = "input-text";
    public static final String SINK = "output-text";

    @Autowired
    public void register(StreamsBuilder builder) {
        // instantiate streams
        KStream<String, String> inputStream = builder.stream(SOURCE, Consumed.with(Serdes.String(), Serdes.String()));
        KStream<String, String> outputStream = inputStream.mapValues(value -> value.toUpperCase());

        // Print streams to Std out
        inputStream.print(Printed.<String, String>toSysOut().withLabel(SOURCE));
        outputStream.print(Printed.<String, String>toSysOut().withLabel(SINK));

        // Send output stream to sink
        outputStream.to(SINK, Produced.with(Serdes.String(), Serdes.String()));
    }
}
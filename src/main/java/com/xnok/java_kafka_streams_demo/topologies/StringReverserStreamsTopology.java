package com.xnok.java_kafka_streams_demo.topologies;

import com.xnok.java_kafka_streams_demo.processor.StringReverserProcessor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StringReverserStreamsTopology {

    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";

    @Autowired
    public void register(StreamsBuilder builder) {
        // 1. Consume from the input topic
        KStream<String, String> inputStream = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));

        // 2. Apply the StringReverserProcessor
        KStream<String, String> reversedStream = inputStream.process(StringReverserProcessor::new);

        // 3. Send the reversed strings to the output topic
        reversedStream.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }
}
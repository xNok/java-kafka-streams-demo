package com.xnok.java_kafka_streams_demo.processor;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

public class StringReverserProcessor implements Processor<String, String, String, String> {

    private ProcessorContext<String, String> context; // Add this line

    @Override
    public void init(ProcessorContext<String, String> context) {
        this.context = context; // Initialize the context here
    }

    @Override
    public void process(Record<String, String> record) {
        // 1. Reverse the string
        String reversedString = new StringBuilder(record.value()).reverse().toString();

        // 2. Forward the reversed string with the same key
        context.forward(record.withValue(reversedString));
    }

    @Override
    public void close() {
        // No resources to close in this example
    }
}
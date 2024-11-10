package com.xnok.java_kafka_streams_demo.configs;

import com.xnok.java_kafka_streams_demo.topologies.OrderStreamsTopology;
import com.xnok.java_kafka_streams_demo.topologies.UpperCaseStreamsTopology;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Configuration
public class UpperCaseStreamsConfiguration {

    @Bean
    public NewTopic SourceTopic() {
        return TopicBuilder.name(UpperCaseStreamsTopology.SOURCE)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic SinkTopic() {
        return TopicBuilder.name(UpperCaseStreamsTopology.SINK)
                .replicas(1)
                .build();
    }
}

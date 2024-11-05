package com.xnok.java_kafka_streams_demo.configs;

import com.xnok.java_kafka_streams_demo.topologies.OrderStreamsTopology;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class OrderStreamsConfiguration {
    
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(OrderStreamsTopology.ORDERS)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic orderAnaliticsTopic() {
        return TopicBuilder.name(OrderStreamsTopology.ORDERS_ANALYTICS)
            .replicas(1)
            .build();
    }
}

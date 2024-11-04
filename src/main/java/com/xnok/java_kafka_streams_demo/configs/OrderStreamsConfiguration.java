package com.xnok.java_kafka_streams_demo.configs;

@Configuration
public class OrderStreamsConfiguration {
    
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(OrderStreamsTopology.ORDERS)
            .partition(2)
            .replicas(1)
            .build()
    }

    @Bean
    public NewTopic orderAnaliticsTopic() {
        return TopicBuilder.name(OrderStreamsTopology.ORDERS_ANALYTICS)
            .partition(2)
            .replicas(1)
            .build()
    }
}

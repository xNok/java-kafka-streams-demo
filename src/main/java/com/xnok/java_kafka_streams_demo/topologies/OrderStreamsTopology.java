package com.xnok.java_kafka_streams_demo.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderStreamsTopology {

    public static String ORDERS="orders";

    public static String ORDERS_ANALYTICS="orders-analytics";
    
    @Autowired
    public void register(StreamsBuilder streamsBuilder){
        var orderStream = streamsBuilder.stream(
            ORDERS, Consumed.with(Serdes.String(), Serdes.String())
        );

        orderStream.
            print(Printed.<String, String>toSysOut().withLabel("ordersStream"));

        var analyticsStream = orderStream
            .mapValues((readOnlyKey, value) -> value.toUpperCase());

        analyticsStream.
            print(Printed.<String, String>toSysOut().withLabel("analyticsStream"));

        analyticsStream.
            to(ORDERS_ANALYTICS, 
                Produced.with(Serdes.String(), Serdes.String()));
    }
}

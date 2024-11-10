package com.xnok.java_kafka_streams_demo.topologies;

import com.xnok.java_kafka_streams_demo.models.ProductData;
import com.xnok.java_kafka_streams_demo.models.SearchEvent;
import com.xnok.java_kafka_streams_demo.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import  org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * KeywordSearchSearchTopology takes a streams of user keyword search from our e-commerce platform cleans
 * the data and correlate the result with available product data.
 */
@Component
@Slf4j
public class KeywordSearchSearchTopology {

    public static final String SOURCE = "raw-search-queries";
    public static final String SINK = "search-queries";

    @Autowired
    private ProductService productService; // Autowired ProductService

    @Autowired
    public void register(StreamsBuilder builder) {
        // 1. Consume search events from the input topic
        KStream<String, SearchEvent> searchStream = builder.stream(SOURCE,
                Consumed.with(Serdes.String(), Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(SearchEvent.class))));

        searchStream.print(Printed.<String, SearchEvent>toSysOut().withLabel(SOURCE));

        // 2. Preprocess search events
        KStream<String, SearchEvent> preprocessedStream = searchStream
                .map((key, value) -> {
                    String query = value.getQuery();

                    // Data cleaning: Remove special characters and extra whitespace
                    query = query.replaceAll("[^a-zA-Z0-9\\s]", "").trim().replaceAll("\\s+", " ");

                    // Data cleaning: replace or discard unrecognized words (potential typos)
                    // ... (some magic dictionary or AI function)

                    // Data cleaning: Remove duplicate words within the same query
                    List<String> uniqueWords = Arrays.stream(query.toLowerCase().split("\\s+"))
                            .distinct()
                            .collect(Collectors.toList());

                    value.setQuery(String.join(" ", uniqueWords));

                    return KeyValue.pair(key, value);
                })
                .filter((key, value) -> !Objects.equals(value.getQuery(), "")); // Remove empty queries


        // 3. Enrich stream
        KStream<String, SearchEvent> enrichedStream = preprocessedStream.map((key, value) -> {

            ProductData product = productService.searchProductByKeywords(value.getQuery());
            value.setProductData(product);

            return KeyValue.pair(key, value);
        });

        enrichedStream.print(Printed.<String, SearchEvent>toSysOut().withLabel(SINK));
        enrichedStream.to(SINK,
                Produced.with(
                        Serdes.String(),
                        Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(SearchEvent.class))
                )
        );
    }
}

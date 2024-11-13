package com.xnok.java_kafka_streams_demo.topologies;

import com.xnok.java_kafka_streams_demo.models.ProductData;
import com.xnok.java_kafka_streams_demo.models.SearchEvent;
import com.xnok.java_kafka_streams_demo.services.ProductService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyString;

public class KeywordSearchSearchTopologyTests {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, SearchEvent> inputTopic;
    private TestOutputTopic<String, SearchEvent> outputTopic;
    private TestOutputTopic<Windowed<String>, Long> analiticsTopic;
    private ProductService productServiceMock; // Mock ProductService

    @BeforeEach
    public void setup() {
        // 1. Create a mock for ProductService
        productServiceMock = Mockito.mock(ProductService.class);

        // 2. Set up the topology
        StreamsBuilder builder = new StreamsBuilder();
        new KeywordSearchSearchTopology(productServiceMock).register(builder); // Inject the mock
        testDriver = new TopologyTestDriver(builder.build(), new Properties());

        // 3. Setup input and output topics
        inputTopic = testDriver.createInputTopic(
                KeywordSearchSearchTopology.SOURCE,
                new StringSerializer(),
                new JsonSerializer<>());
        outputTopic = testDriver.createOutputTopic(
                KeywordSearchSearchTopology.SINK,
                new StringDeserializer(),
                new JsonDeserializer<>(SearchEvent.class));

        analiticsTopic = testDriver.createOutputTopic(
                KeywordSearchSearchTopology.SINK_ANALYTICS,
                WindowedSerdes.timeWindowedSerdeFrom(
                        String.class,
                        Duration.ofMinutes(10).toMillis()
                ).deserializer(),
                Serdes.Long().deserializer()
        );
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void testVerySimpleStreamWithSingleQuery() {
        // 1. Define test data and expected output
        String query = "Running shoes!!  with   red  laces ";
        String expectedQuery = "running shoes with red laces";
        ProductData mockProductData = new ProductData("running-shoes-id", "An amazing pair of running shoes");
        Mockito.when(productServiceMock.searchProductByKeywords(anyString()))
                .thenReturn(mockProductData);

        // 2. Pipe the input data to the input topic
        inputTopic.pipeInput("key1", new SearchEvent("user1", query));

        // 3. Read and verify the output from the output topic
        KeyValue<String, SearchEvent> output = outputTopic.readKeyValue();
        assertEquals(expectedQuery, output.value.getQuery());
        assertEquals(mockProductData.getProductID(), output.value.getProductData().getProductID());
    }

    @Test
    public void testTrendingKeywords() {
        // 1. Define test data
        List<SearchEvent> searchEvents = Arrays.asList(
                new SearchEvent("user1", "Running shoes with red laces"),
                new SearchEvent("user2", "blue shoes"),
                new SearchEvent("user3", "Running shoes"),
                new SearchEvent("user4", "red shoes"),
                new SearchEvent("user5", "Running shoes with laces"),
                new SearchEvent("user6", "Running shoes with red laces")
        );
        // Define the expected results with windowed keys and counts
        long windowStartTime = 1731460800000L;
        long windowEndTime = windowStartTime + Duration.ofMinutes(10).toMillis();
        List<KeyValue<Windowed<String>, Long>> expectedResults = Arrays.asList(
                new KeyValue<>(new Windowed<>("shoes", new TimeWindow(windowStartTime, windowEndTime)), 5L),
                new KeyValue<>(new Windowed<>("shoes", new TimeWindow(windowStartTime, windowEndTime)), 6L)
        );

        // 2. Pipe input data to the input topic
        long eventTime = windowStartTime;
        for (SearchEvent event : searchEvents) {
            inputTopic.pipeInput("key", event, eventTime);
            eventTime += Duration.ofSeconds(1).toMillis(); // next event slided by 10s
        }

        // 3. Verify trending keywords in the output topic
        List<KeyValue<Windowed<String>, Long>> trendingKeywords = analiticsTopic.readKeyValuesToList();
        assertIterableEquals(expectedResults, trendingKeywords);
    }
}

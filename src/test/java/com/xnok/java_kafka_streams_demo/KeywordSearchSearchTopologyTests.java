package com.xnok.java_kafka_streams_demo;

import com.xnok.java_kafka_streams_demo.models.ProductData;
import com.xnok.java_kafka_streams_demo.models.SearchEvent;
import com.xnok.java_kafka_streams_demo.services.ProductService;
import com.xnok.java_kafka_streams_demo.topologies.KeywordSearchSearchTopology;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

public class KeywordSearchSearchTopologyTests {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, SearchEvent> inputTopic;
    private TestOutputTopic<String, SearchEvent> outputTopic;
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
}

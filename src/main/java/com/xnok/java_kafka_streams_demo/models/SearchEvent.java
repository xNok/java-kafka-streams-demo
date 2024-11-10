package com.xnok.java_kafka_streams_demo.models;

import lombok.Getter;
import lombok.Setter;

public class SearchEvent {
    private String userId;
    @Getter
    @Setter
    private String query;
    private long timestamp;
    @Setter
    private ProductData productData; // To store data from the API
}

package com.xnok.java_kafka_streams_demo.models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SearchEvent {
    private long timestamp;
    private String userId;
    private String query;
    private ProductData productData; // To store search result from the API

    public SearchEvent() {}

    public SearchEvent(String userId, String query) {
        this.userId = userId;
        this.query = query;
    }
}

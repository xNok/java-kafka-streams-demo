package com.xnok.java_kafka_streams_demo.models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductData {
    private String productID;
    private String description;

    public ProductData(){}

    public ProductData(String productID, String description) {
        this.description = description;
        this.productID = productID;
    }
}

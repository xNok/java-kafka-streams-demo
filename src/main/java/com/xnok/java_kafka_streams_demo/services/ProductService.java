package com.xnok.java_kafka_streams_demo.services;

import com.xnok.java_kafka_streams_demo.models.ProductData;

public interface ProductService {
    ProductData searchProductByKeywords(String query);
}

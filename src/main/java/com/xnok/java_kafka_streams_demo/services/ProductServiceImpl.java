package com.xnok.java_kafka_streams_demo.services;

import com.xnok.java_kafka_streams_demo.models.ProductData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductServiceImpl implements ProductService{
    @Override
    public ProductData searchProductByKeywords(String query) {
        return null;
    }
}

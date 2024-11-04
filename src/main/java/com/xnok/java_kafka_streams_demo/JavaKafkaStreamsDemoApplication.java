package com.xnok.java_kafka_streams_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafkaStreams
public class JavaKafkaStreamsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaKafkaStreamsDemoApplication.class, args);
	}

}

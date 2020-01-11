package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.concurrent.ExecutionException;

public interface ProducerBase {
    ApiResponse producerHandle(String stringToMq);
    KafkaProducer<String, String> getProducer();
}

package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class PositionsAndPosUrlAndDetailProducer {
    @Autowired
    private PositionsUrlHtmlMqConfig positionsUrlHtmlMqConfig;
    @Autowired
    private PositionUrlMqConfig positionUrlMqConfig;
    @Autowired
    private PositionDetailHtmlMqConfig positionDetailHtmlMqConfig;

    public ApiResponse PositionsUrlHtmlProducer(DataToMq dataToMq){
        DataToMqJson dataToMqJson = new DataToMqJson();
        String jsonString = dataToMqJson.toJson(dataToMq);

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionsUrlHtmlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(positionsUrlHtmlMqConfig.getTopic(), jsonString);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + jsonString);
        }

        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }

    public ApiResponse PositionUrlProducer(String positionUrl){

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionUrlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(positionUrlMqConfig.getTopic(), positionUrl);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + positionUrl);
        }

        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }

    public ApiResponse PositionDetailHtmlProducer(DataToMq dataToMq){
        DataToMqJson dataToMqJson = new DataToMqJson();
        String jsonString = dataToMqJson.toJson(dataToMq);

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionDetailHtmlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(positionDetailHtmlMqConfig.getTopic(), jsonString);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + jsonString);
        }

        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }
}

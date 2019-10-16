package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class PositionDetailAndPositionsProductor {

    public ApiResponse productor(PosDetailAndPositionsHtml posDetailAndPositionsHtml, String host, String topic){
        PosDetailAndPositionsHtmlJson posDetailAndListJson = new PosDetailAndPositionsHtmlJson();
        String jsonString = posDetailAndListJson.toJson(posDetailAndPositionsHtml);
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonString);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + jsonString);
        }

        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }

    public ApiResponse positionUrlProductor(PositionUrl positionUrl, String host, String topic){
        Gson gson = new Gson();
        String positionUrlString = gson.toJson(positionUrl);
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, positionUrlString);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + positionUrlString);
        }

        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }
}

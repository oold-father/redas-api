package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.mqConfig.PositionUrlMqConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class PositionUrlProducer implements ProducerBase {

    @Autowired
    private PositionUrlMqConfig positionUrlMqConfig;

    private static KafkaProducer<String, String> lastConn;

    @Bean
    @Override
    public KafkaProducer<String, String> getProducer() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionUrlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        if(PositionUrlProducer.lastConn == null) {
            PositionUrlProducer.lastConn = new KafkaProducer<>(p);
        }
        return PositionUrlProducer.lastConn;
    }

    @Override
    public ApiResponse producerHandle(String stringToMq){
        ProducerRecord<String, String> record = new ProducerRecord<>(positionUrlMqConfig.getTopic(), stringToMq);
        KafkaProducer<String, String> kafkaProducer = this.getProducer();
        try {
            kafkaProducer.send(record);
        }catch (Exception e){
            PositionUrlProducer.lastConn = null;
            return new ApiResponse(ResponseCode.FAILED, "上报失败");
        }
        System.out.println("消息发送成功:" + stringToMq);
        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }
}

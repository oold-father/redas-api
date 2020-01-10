package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.mqConfig.PositionsUrlHtmlMqConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class PositionsUrlHtmlProducer implements ProducerBase {

    @Autowired
    private PositionsUrlHtmlMqConfig positionsUrlHtmlMqConfig;

    private static KafkaProducer<String, String> lastConn;


    @Override
    public KafkaProducer<String, String> getProducer() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionsUrlHtmlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        if(PositionsUrlHtmlProducer.lastConn == null) {
            PositionsUrlHtmlProducer.lastConn = new KafkaProducer<>(p);
        }
        return PositionsUrlHtmlProducer.lastConn;
    }

    @Override
    public ApiResponse producerHandle(String stringToMq){
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(positionsUrlHtmlMqConfig.getTopic(), stringToMq);
        KafkaProducer<String, String> kafkaProducer = this.getProducer();
        try {
            kafkaProducer.send(record);
        }catch (Exception e){
            PositionsUrlHtmlProducer.lastConn = null;
            return new ApiResponse(ResponseCode.FAILED, "上报失败");
        }
        System.out.println("消息发送成功:" + stringToMq);
        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }
}

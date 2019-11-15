package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.mqConfig.RedasMqConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class RecrPageProducer implements ProducerBase {

    @Autowired
    private RedasMqConfig redasMqConfig;

    @Override
    public ApiResponse producerHandle(String stringToMq){
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, redasMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(redasMqConfig.getTopic(), stringToMq);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + stringToMq);
        }catch (Exception e){
            return new ApiResponse(ResponseCode.FAILED, "上报失败");
        }
        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }
}

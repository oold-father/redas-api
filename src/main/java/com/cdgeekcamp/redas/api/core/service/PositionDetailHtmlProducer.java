package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.mqConfig.PositionDetailHtmlMqConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class PositionDetailHtmlProducer implements ProducerBase {

    @Autowired
    private PositionDetailHtmlMqConfig positionDetailHtmlMqConfig;

    private static KafkaProducer<String, String> lastConn;

    @Override
    public KafkaProducer<String, String> getProducer() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionDetailHtmlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        if(PositionDetailHtmlProducer.lastConn == null) {
            PositionDetailHtmlProducer.lastConn = new KafkaProducer<>(p);
        }
        return PositionDetailHtmlProducer.lastConn;
    }

    //    发送到消息队列
    @Override
    public ApiResponse producerHandle(String stringToMq) {

        KafkaProducer<String, String> kafkaProducer = this.getProducer();
        ProducerRecord<String, String> record = new ProducerRecord<>(positionDetailHtmlMqConfig.getTopic(), stringToMq);
        try {
            kafkaProducer.send(record);
        }catch (Exception e){
            PositionDetailHtmlProducer.lastConn = null;
            return new ApiResponse(ResponseCode.FAILED, "上报失败");
        }
        System.out.println("消息发送成功:" + stringToMq);
        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }

}

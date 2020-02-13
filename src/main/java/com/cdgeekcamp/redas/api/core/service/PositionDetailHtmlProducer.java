package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.api.core.config.PositionDetailHtmlMqConfig;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.Future;

@Service
public class PositionDetailHtmlProducer implements ProducerBase {

    @Autowired
    private PositionDetailHtmlMqConfig positionDetailHtmlMqConfig;

    private static KafkaProducer<String, String> lastConn;

    private Logger log = LoggerFactory.getLogger(PositionDetailHtmlProducer.class);

    @Override
    public KafkaProducer<String, String> getProducer() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionDetailHtmlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);

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
        Future<RecordMetadata> response = kafkaProducer.send(record);
        RecordMetadata x =null;
        try {
            x= response.get();
        } catch (Exception e) {
            log.error("消息发送失败:" + stringToMq);
            return new ApiResponse(ResponseCode.FAILED, "上报失败");
        }
        if (x.hasOffset()){
            log.info("消息发送成功:" + stringToMq);
        }
        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }

}

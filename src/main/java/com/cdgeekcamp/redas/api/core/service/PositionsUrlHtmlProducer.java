package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.api.core.config.PositionsUrlHtmlMqConfig;
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
public class PositionsUrlHtmlProducer implements ProducerBase {

    @Autowired
    private PositionsUrlHtmlMqConfig positionsUrlHtmlMqConfig;

    private static KafkaProducer<String, String> lastConn;

    private Logger log = LoggerFactory.getLogger(PositionsUrlHtmlProducer.class);

    @Override
    public KafkaProducer<String, String> getProducer() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, positionsUrlHtmlMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);

        if(PositionsUrlHtmlProducer.lastConn == null) {
            PositionsUrlHtmlProducer.lastConn = new KafkaProducer<>(p);
        }
        return PositionsUrlHtmlProducer.lastConn;
    }

    @Override
    public ApiResponse producerHandle(String stringToMq){
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(positionsUrlHtmlMqConfig.getTopic(), stringToMq);
        KafkaProducer<String, String> kafkaProducer = this.getProducer();
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

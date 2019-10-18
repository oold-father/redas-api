package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
import com.cdgeekcamp.redas.lib.core.jsonObject.RecrPageJson;
import com.cdgeekcamp.redas.lib.core.mqConfig.RedasMqConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequestMapping(value = "/recr_page")
public class RecrPageController {
    @Autowired
    private RedasMqConfig redasMqConfig;

    @PostMapping(value = "add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addRecrPage(@RequestBody RecrPage RecrPage) {
        RecrPageJson RecrPageJson = new RecrPageJson();
        String jsonString = RecrPageJson.toJson(RecrPage);
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, redasMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(redasMqConfig.getTopic(), jsonString);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + jsonString);
        }

        return new ApiResponse(ResponseCode.SUCCESS, "上报成功");
    }
}

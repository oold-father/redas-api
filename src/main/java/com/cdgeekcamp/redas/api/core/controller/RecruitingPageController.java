package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.RecruitingPageApiMessage;
import com.cdgeekcamp.redas.lib.core.RecruitingPage;
import com.cdgeekcamp.redas.lib.core.RecruitingPageJson;
import com.cdgeekcamp.redas.lib.core.RedasMqConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
public class RecruitingPageController {
    @Autowired
    private RedasMqConfig redasMqConfig;

    @PostMapping(value = "/add_recruiting_page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RecruitingPageApiMessage addRecruitingPage(@RequestBody RecruitingPage recruitingPage) throws JsonProcessingException {
        RecruitingPageJson recruitingPageJson = new RecruitingPageJson();
        String json_msg = recruitingPageJson.toJson(recruitingPage);
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, redasMqConfig.getHost());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(redasMqConfig.getTopic(), json_msg);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + json_msg);
        }

        return new RecruitingPageApiMessage(recruitingPage.getSpiderUuid(), "上报成功");
    }
}

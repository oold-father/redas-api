package com.cdgeekcamp.redas.api.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
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
    private ObjectMapper objectMapper;

    @PostMapping(value = "/add_recruiting_page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public RecruitingPageApiMessage addRecruitingPage(@RequestBody RecruitingPage recruitingPage) throws JsonProcessingException {
        String json_msg = objectMapper.writeValueAsString(recruitingPage);
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.2.2:9092");
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        JSONObject result = new JSONObject();

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p)) {
            ProducerRecord<String, String> record = new ProducerRecord<>("test", json_msg);
            kafkaProducer.send(record);
            System.out.println("消息发送成功:" + json_msg);
        }

        return new RecruitingPageApiMessage(recruitingPage.getUuid(), "上报成功");
    }
}

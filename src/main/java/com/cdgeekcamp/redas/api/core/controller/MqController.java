package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.api.core.controller.json.MqJson;
import com.cdgeekcamp.redas.api.core.service.PositionDetailHtmlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionUrlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionsUrlHtmlProducer;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.DataToMq;
import com.cdgeekcamp.redas.lib.core.jsonObject.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/mq")
public class MqController {
    @Autowired
    private PositionUrlProducer positionUrlProducer;

    @Autowired
    private PositionsUrlHtmlProducer positionsUrlHtmlProducer;

    @Autowired
    private PositionDetailHtmlProducer positionDetailHtmlProducer;

    @PostMapping(value = "addPositionUrl")
    public ApiResponse mqAddPositionUrl(@RequestBody MqJson mqJson) {
        return positionUrlProducer.producerHandle(mqJson.getMsg());
    }

    @PostMapping(value = "/addPositionsUrlHtml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddPositionsUrlHtml(@RequestBody DataToMq dataToMq) {
        JsonObject<DataToMq> RecrPageJson = new JsonObject();
        String data = RecrPageJson.toJson(dataToMq);
        return positionUrlProducer.producerHandle(data);
    }

    @PostMapping(value = "/addPositionDetailHtml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddPositionDetailHtml(@RequestBody DataToMq dataToMq) {
        JsonObject<DataToMq> RecrPageJson = new JsonObject();
        String data = RecrPageJson.toJson(dataToMq);
        return positionUrlProducer.producerHandle(data);
    }
}

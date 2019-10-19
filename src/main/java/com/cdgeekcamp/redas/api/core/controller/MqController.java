package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.api.core.service.PositionDetailHtmlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionUrlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionsUrlHtmlProducer;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.HtmlToMq;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.UrlToMq;
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
    public ApiResponse mqAddPositionUrl(@RequestBody UrlToMq urlToMq) {
        return positionUrlProducer.producerHandle(urlToMq.getUrl());
    }

    @PostMapping(value = "/addPositionsUrlHtml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddPositionsUrlHtml(@RequestBody HtmlToMq htmlToMq) {
        JsonObject<HtmlToMq> htmlJson = new JsonObject();
        String data = htmlJson.toJson(htmlToMq);
        return positionsUrlHtmlProducer.producerHandle(data);
    }

    @PostMapping(value = "/addPositionDetailHtml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddPositionDetailHtml(@RequestBody HtmlToMq htmlToMq) {
        JsonObject<HtmlToMq> htmlJson = new JsonObject();
        String data = htmlJson.toJson(htmlToMq);
        return positionDetailHtmlProducer.producerHandle(data);
    }

    @PostMapping(value = "/addRecrPage", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddRecrPage(@RequestBody RecrPage recrPage) {
        JsonObject<RecrPage> htmlJson = new JsonObject();
        String data = htmlJson.toJson(recrPage);
        return positionUrlProducer.producerHandle(data);
    }
}

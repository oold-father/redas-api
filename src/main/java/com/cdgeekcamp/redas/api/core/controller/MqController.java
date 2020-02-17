package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.api.core.service.PositionDetailHtmlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionUrlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionsUrlHtmlProducer;
import com.cdgeekcamp.redas.api.core.service.RecrPageProducer;
import com.cdgeekcamp.redas.api.core.controller.json.HtmlToMq;
import com.cdgeekcamp.redas.api.core.controller.json.UrlToMq;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
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

    @Autowired
    private RecrPageProducer recrPageProducer;

    /**
     * 发送职位列表页面到消息队列
     * @param htmlToMq 需要发送到消息队列的html相关数据
     * @return ApiResponse
     */
    @PostMapping(value = "/addPositionsUrlHtml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddPositionsUrlHtml(@RequestBody HtmlToMq htmlToMq) {
        JsonObject htmlJson = new JsonObject();
        String data = htmlJson.toJson(htmlToMq);
        return positionsUrlHtmlProducer.producerHandle(data);
    }

    /**
     * 发送职位url到消息队列
     * @param urlToMq 需要发送到消息队列的url
     * @return ApiResponse
     */
    @PostMapping(value = "addPositionUrl")
    public ApiResponse mqAddPositionUrl(@RequestBody UrlToMq urlToMq){
        return positionUrlProducer.producerHandle(urlToMq.getUrl());
    }

    /**
     * 发送职位详情页面到消息队列
     * @param htmlToMq 需要发送到消息队列的职位详情html相关数据
     * @return ApiResponse
     */
    @PostMapping(value = "/addPositionDetailHtml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddPositionDetailHtml(@RequestBody HtmlToMq htmlToMq) {
        JsonObject htmlJson = new JsonObject();
        String data = htmlJson.toJson(htmlToMq);
        return positionDetailHtmlProducer.producerHandle(data);
    }

    /**
     * 发送职位信息元数据到消息队列
     * @param recrPage 职位信息元数据
     * @return ApiResponse
     */
    @PostMapping(value = "/addRecrPage", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse mqAddRecrPage(@RequestBody RecrPage recrPage) {
        JsonObject htmlJson = new JsonObject();
        String data = htmlJson.toJson(recrPage);
        ApiResponse apiResponse = recrPageProducer.producerHandle(data);
        if ((ResponseCode.SUCCESS).equals(apiResponse.getCode())){
            recrPageProducer.urlStateHandle(recrPage);
        }
        return recrPageProducer.producerHandle(data);
    }
}

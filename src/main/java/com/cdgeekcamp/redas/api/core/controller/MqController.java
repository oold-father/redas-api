package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.api.core.controller.json.MqJson;
import com.cdgeekcamp.redas.api.core.service.PositionDetailHtmlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionUrlProducer;
import com.cdgeekcamp.redas.api.core.service.PositionsUrlHtmlProducer;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "addUrl")
    public ApiResponse mqAddUrl(@RequestBody MqJson mqJson) {

        return new ApiResponse(ResponseCode.FAILED, "添加Url失败，Url已存在");
    }
}

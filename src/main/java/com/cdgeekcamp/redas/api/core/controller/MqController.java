package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.api.core.service.PositionsAndPosUrlAndDetailProducer;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mq")
public class MqController {
    @Autowired
    private PositionsAndPosUrlAndDetailProducer positionsAndPosUrlAndDetailProducer;

    @PostMapping(value = "addUrl")
    public ApiResponse mqAdd(
            @RequestParam(value="host",required = false,defaultValue = "1") String url
    ) {

        return new ApiResponse(ResponseCode.FAILED, "添加Url失败，Url已存在");
    }
}

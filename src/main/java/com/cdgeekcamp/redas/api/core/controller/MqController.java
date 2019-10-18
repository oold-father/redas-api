package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.api.core.service.PositionsAndPosUrlAndDetailProducer;
import com.cdgeekcamp.redas.lib.core.DataToMq;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/mq")
public class MqController {
    @Autowired
    private PositionsAndPosUrlAndDetailProducer positionsAndPosUrlAndDetailProducer;

    @PostMapping(value = "addUrl")
    public ApiResponse mqAdd(@RequestBody DataToMq dataToMq) {

        return new ApiResponse(ResponseCode.FAILED, "添加Url失败，Url已存在");
    }
}

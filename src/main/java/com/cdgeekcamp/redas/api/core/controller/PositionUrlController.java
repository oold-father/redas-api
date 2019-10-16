package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.PositionsAndPosUrlAndDetailProductor;
import com.cdgeekcamp.redas.lib.core.PositionDetailHtmlMqConfig;
import com.cdgeekcamp.redas.lib.core.PositionsUrlHtmlMqConfig;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/position")
public class PositionUrlController {
    @Autowired
    private PositionsAndPosUrlAndDetailProductor positionsAndPosUrlAndDetailProductor;
    @Autowired
    private PositionsUrlHtmlMqConfig positionsUrlHtmlMqConfig;
    @Autowired
    private PositionDetailHtmlMqConfig positionDetailHtmlMqConfig;

    @PostMapping(value = "/positions_url_html", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse positionsUrlHtml(@RequestBody Map<String, String> map) {

        return positionsAndPosUrlAndDetailProductor.productor(map,
                positionsUrlHtmlMqConfig.getHost(), positionsUrlHtmlMqConfig.getTopic());
    }

    @PostMapping(value = "/url", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse position_url(@RequestBody Map<String, String> map) {

        return positionsAndPosUrlAndDetailProductor.productor(map,
                positionsUrlHtmlMqConfig.getHost(), positionsUrlHtmlMqConfig.getTopic());
    }

    @PostMapping(value = "/detail_html", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse positionDetail(@RequestBody Map<String, String> map) {

        return positionsAndPosUrlAndDetailProductor.productor(map,
                positionDetailHtmlMqConfig.getHost(), positionDetailHtmlMqConfig.getTopic());
    }

}

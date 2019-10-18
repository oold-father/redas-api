package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.PositionsAndPosUrlAndDetailProducer;
import com.cdgeekcamp.redas.lib.core.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/position")
public class PositionUrlController {
    @Autowired
    private PositionsAndPosUrlAndDetailProducer positionsAndPosUrlAndDetailProducer;

    @PostMapping(value = "/positions_url_html", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse positionsUrlHtml(@RequestBody PositionsUrlHtml positionsUrlHtml) {

        return positionsAndPosUrlAndDetailProducer.PositionsUrlHtmlProducer(positionsUrlHtml);
    }

    @PostMapping(value = "/url", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse position_url(@RequestBody PositionUrl positionUrl) {

        return positionsAndPosUrlAndDetailProducer.PositionUrlProducer(positionUrl);
    }

    @PostMapping(value = "/detail_html", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse positionDetail(@RequestBody PositionDetailHtml positionDetailHtml) {

        return positionsAndPosUrlAndDetailProducer.PositionDetailHtmlProducer(positionDetailHtml);
    }

}


package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.PositionDetailAndPositionsProductor;
import com.cdgeekcamp.redas.lib.core.PosDetailAndPositionsHtml;
import com.cdgeekcamp.redas.lib.core.PositionDetailHtmlMqConfig;
import com.cdgeekcamp.redas.lib.core.PositionUrl;
import com.cdgeekcamp.redas.lib.core.PositionsUrlHtmlMqConfig;
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
    private PositionDetailAndPositionsProductor positionDetailAndPositionsProductor;
    @Autowired
    private PositionsUrlHtmlMqConfig positionsUrlHtmlMqConfig;
    @Autowired
    private PositionDetailHtmlMqConfig positionDetailHtmlMqConfig;

    @PostMapping(value = "/positions_url_html", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse positionsUrlHtml(@RequestBody PosDetailAndPositionsHtml posDetailAndPositionsHtml) {

        return positionDetailAndPositionsProductor.productor(posDetailAndPositionsHtml,
                positionsUrlHtmlMqConfig.getHost(), positionsUrlHtmlMqConfig.getTopic());
    }

    @PostMapping(value = "/position_url", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse position_url(@RequestBody PositionUrl positionUrl) {

        return positionDetailAndPositionsProductor.positionUrlProductor(positionUrl,
                positionsUrlHtmlMqConfig.getHost(), positionsUrlHtmlMqConfig.getTopic());
    }

    @PostMapping(value = "/detail_html", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse positionDetail(@RequestBody PosDetailAndPositionsHtml posDetailAndPositionsHtml) {

        return positionDetailAndPositionsProductor.productor(posDetailAndPositionsHtml,
                positionDetailHtmlMqConfig.getHost(), positionDetailHtmlMqConfig.getTopic());
    }

}

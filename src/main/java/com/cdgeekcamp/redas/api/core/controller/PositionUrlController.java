package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.db.model.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.UrlToMq;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.UrlsToDB;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/position")
public class PositionUrlController {

    @Autowired
    PositionUrlRepository positionUrls;

    @Autowired
    PositionsUrlRepository positionsUrl;

    @Autowired
    R_PositionsPositionUrlRepository r_PositionsPositionUrl;

    @PostMapping(value = "/addUrl", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addPositionsUrl(@RequestBody UrlsToDB urlsToDB) {

        // 判断职位列表非空
        if (urlsToDB.getUrls() == null) {
            return new ApiResponse(ResponseCode.FAILED, "添加Url失败，提交参数内容没有Url");
        }

        // 查询来源Url是否存在
        Optional<PositionsUrl> positionsUrlResult = positionsUrl.findByUrl(urlsToDB.getSourceUrl());
        if (positionsUrlResult.isEmpty()){
            return new ApiResponse(ResponseCode.FAILED, "添加Url失败，数据来源url有误");
        }

        // 遍历职位列表
        ApiResponseList<String> responseList = new ApiResponseList<>(ResponseCode.SUCCESS,"Url添加完成");
        for (String positionUrlSting : urlsToDB.getUrls()) {
            Optional<PositionUrl> positionUrl = positionUrls.findByUrl(positionUrlSting);
            if (positionUrl.isEmpty()) {
                // 查询到非空则保存
                PositionUrl result = positionUrls.save(new PositionUrl(positionUrlSting, RedasString.getNowTimeStamp(), false, null, RedasString.getPlatform(positionUrlSting)));
                // 保存关系表
                r_PositionsPositionUrl.save(new R_PositionsPositionUrl(result.getId(), positionsUrlResult.get().getId()));

                // 发送到消息队列
                String url = "http://127.0.0.1:8080/mq/addPositionUrl";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

                Map<String, String> map= new LinkedHashMap<>();
                map.put("url", positionUrlSting);

                HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.POST,request, ApiResponse.class);

                System.out.println(response.getBody());
                responseList.addValue("添加url："+positionUrlSting + "成功");
            } else {
                responseList.addValue("添加url："+positionUrlSting + "失败，Url已存在");
            }
        }

        // 来源Url状态更改
        PositionsUrl newPositionsUrl = positionsUrlResult.get();
        newPositionsUrl.setState(true);
        positionsUrl.save(newPositionsUrl);
        return responseList;
    }
}


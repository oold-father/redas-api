package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.db.model.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping(value = "/backstage")
public class BackstageController {
    @Autowired
    PositionUrlRepository positionUrls;

    @Autowired
    PositionsUrlRepository positionsUrl;

    @Autowired
    NatureRepository nature;

    @GetMapping(value = "/positionUrl/add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addPositionsUrl(@RequestParam Integer isAdd) {
        if (isAdd == 0){
            Iterable<PositionUrl> result = positionUrls.findAllByState(0);
            Integer num = 0;
            for (PositionUrl item: result){

                String url = "http://127.0.0.1:8080/mq/addPositionUrl";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

                Map<String, String> map= new LinkedHashMap<>();
                map.put("url", item.getUrl());

                HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.POST,request, ApiResponse.class);
                // 添加成功，url状态改为1
                if(Objects.requireNonNull(response.getBody()).getCode() == ResponseCode.SUCCESS){
                    item.setState(1);
                    positionUrls.save(item);
                    num ++;
                }
            }

            return new ApiResponse(ResponseCode.SUCCESS, String.format("操作成功, 添加%s条url到消息队列", num.toString()));
        }else {
            return new ApiResponse(ResponseCode.FAILED, "参数错误, 重要接口, 注意不要误操作");
        }
    }

    @GetMapping(value = "/natureList")
    public ApiResponse getAddressInfo() {
        Iterable<Nature> natureAll  = nature.findAll();

        ArrayList<String> natures = new ArrayList<>();
        for (Nature item: natureAll){
            String value = item.getName();
            if(!value.equals("不限")){
                natures.add(item.getName());
            }
        }
        ApiResponseList<String> result = new ApiResponseList<String>(ResponseCode.SUCCESS, "查询成功");
        result.setResult(natures);
        return result;
    }
}

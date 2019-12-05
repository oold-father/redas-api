package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.db.model.*;
import com.cdgeekcamp.redas.lib.core.api.*;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.UrlsToDB;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.websocket.server.PathParam;
import java.util.*;

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
            // 声明一个PositionUrl对象，根据不同的逻辑赋值
            PositionUrl result;
            if (positionUrl.isEmpty()) {
                // 查询到非空则保存
                result = positionUrls.save(new PositionUrl(positionUrlSting, RedasString.getNowTimeStamp(), 0, null, RedasString.getPlatform(positionUrlSting)));
                // 保存关系表
                r_PositionsPositionUrl.save(new R_PositionsPositionUrl(result.getId(), positionsUrlResult.get().getId()));

                responseList.addValue("添加url："+positionUrlSting + "成功");
            } else {
                result= positionUrl.get();
                responseList.addValue("添加url："+positionUrlSting + "失败，Url已存在");
            }

            // 状态为0的url发送到消息队列
            if(result.isState() == 0){
                // 发送到消息队列
                String url = "http://127.0.0.1:8080/mq/addPositionUrl";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

                Map<String, String> map= new LinkedHashMap<>();
                map.put("url", positionUrlSting);

                HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.POST,request, ApiResponse.class);
                // 添加成功，url状态改为1
                if(Objects.requireNonNull(response.getBody()).getCode() == ResponseCode.SUCCESS){
                    result.setState(1);
                    positionUrls.save(result);
                }
            }
        }

        // 来源Url状态更改
        PositionsUrl newPositionsUrl = positionsUrlResult.get();
        newPositionsUrl.setState(2);
        positionsUrl.save(newPositionsUrl);
        return responseList;
    }

    @GetMapping(value = "/getUrlList")
    public ApiResponseX getPositionUrlList(@PathParam("page") Integer page){
        if(page == null || page <= 0){
            page = 0;
        }else {
            page = page-1;
        }
        Pageable pageable = PageRequest.of(page, 20, Sort.Direction.ASC, "Id");
        Page<PositionUrl> posUrls = positionUrls.findAll(pageable);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("totalPage", posUrls.getTotalPages());

        ArrayList<PositionUrl> positionUrlList = new ArrayList<>();
        for (PositionUrl posUrl : posUrls) {
            positionUrlList.add(posUrl);
        }
        map.put("positionUrlList", positionUrlList);

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", map);
    }
}


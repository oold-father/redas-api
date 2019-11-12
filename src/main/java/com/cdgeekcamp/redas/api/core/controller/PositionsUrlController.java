package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.controller.json.PositionUrlJsonClass;
import com.cdgeekcamp.redas.db.model.PositionsUrl;
import com.cdgeekcamp.redas.db.model.PositionsUrlRepository;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/positions")
public class PositionsUrlController {
    @Autowired
    private PositionsUrlRepository PositionsUrls;

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addPositionsUrl(PositionUrlJsonClass positionUrlJsonClass) {
        String url = positionUrlJsonClass.getUrl();
        Integer maxSize = positionUrlJsonClass.getMaxSize();
        Optional<PositionsUrl> result = PositionsUrls.findByUrl(url);

        if (result.isEmpty()){
            PositionsUrls.save(new PositionsUrl(url, RedasString.getNowTimeStamp(), false, null, RedasString.getPlatform(url), maxSize));
            return new ApiResponse(ResponseCode.SUCCESS, "添加Url成功");
        }else {
            return new ApiResponse(ResponseCode.FAILED, "添加Url失败，Url已存在");
        }
    }

    @GetMapping(value = "get")
    public ApiResponse getPositionsUrl() {
        Iterable<PositionsUrl> result = PositionsUrls.findByState(false);

        if (0 == StreamSupport.stream(result.spliterator(), false).count()){
            return new ApiResponse(ResponseCode.FAILED, "获取Url失败,没有新的Url");
        }
        PositionUrlJsonClass response = new PositionUrlJsonClass(result.iterator().next().getUrl(),result.iterator().next().getmaxSize());
        return new ApiResponseX<>(ResponseCode.SUCCESS, "获取Url成功",response);
    }


}

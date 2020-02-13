package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.controller.json.PositionUrl;
import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.db.model.PositionsUrl;
import com.cdgeekcamp.redas.db.model.PositionsUrlRepository;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/positions")
public class PositionsUrlController {
    @Autowired
    private PositionsUrlRepository PositionsUrls;

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addPositionsUrl(@RequestBody PositionUrl positionUrlJsonClass) {
        String url = positionUrlJsonClass.getUrl();
        Integer maxSize = positionUrlJsonClass.getMaxSize();
        Optional<PositionsUrl> result = PositionsUrls.findByUrl(url);

        if (result.isEmpty()){
            PositionsUrls.save(new PositionsUrl(url, RedasString.getNowTimeStamp(), 0, null, RedasString.getPlatform(url), maxSize));
            return new ApiResponse(ResponseCode.SUCCESS, "添加Url成功");
        }else {
            return new ApiResponse(ResponseCode.FAILED, "添加Url失败，Url已存在");
        }
    }

    @GetMapping(value = "get")
    public ApiResponse getPositionsUrl() {
        Iterable<PositionsUrl> result = PositionsUrls.findByState(0);

        if (0 == StreamSupport.stream(result.spliterator(), false).count()){
            return new ApiResponse(ResponseCode.FAILED, "获取Url失败,没有新的Url");
        }

        //        如果存在没有爬取的URL，获得第一个，并将其状态改为正在爬取
        PositionsUrl resultUrl = result.iterator().next();
        resultUrl.setState(1);
        PositionsUrls.save(resultUrl);

        PositionUrl response = new PositionUrl(result.iterator().next().getUrl(),result.iterator().next().getmaxSize());
        return new ApiResponseX<>(ResponseCode.SUCCESS, "获取Url成功",response);
    }

    @GetMapping(value = "/getPostionsUrlList")
    public ApiResponseX<LinkedHashMap<String, Object>> getPositionsUrlList(@PathParam("page") Integer page){
        Integer pageNum = new Pagination().Page(page);

        Pageable pageable = PageRequest.of(pageNum, 20, Sort.Direction.ASC, "Id");
        Page<PositionsUrl> positionsUrls = PositionsUrls.findAll(pageable);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("totalPage", positionsUrls.getTotalPages());

        ArrayList<PositionsUrl> positionsUrlList = new ArrayList<>();
        for (PositionsUrl positionsUrl : positionsUrls) {
            positionsUrlList.add(positionsUrl);
        }
        map.put("positionsUrlList", positionsUrlList);

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", map);
    }
}

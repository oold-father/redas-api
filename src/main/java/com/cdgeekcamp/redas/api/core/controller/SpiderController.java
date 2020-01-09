package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.controller.json.SpiderJson;
import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.db.model.PositionUrl;
import com.cdgeekcamp.redas.db.model.Spider;
import com.cdgeekcamp.redas.db.model.SpiderRepository;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
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

@RestController
@RequestMapping(value = "/spider")
public class SpiderController {
    @Autowired
    private SpiderRepository spiders;

    @PostMapping(value = "add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addSpider(@RequestBody SpiderJson spiderJson) {
        String uuid = spiderJson.getUuid();
        String desc = spiderJson.getDescribe();
        String location = spiderJson.getLocation();

        Optional<Spider> spiderByUuid = spiders.findByUuid(uuid);

        if (spiderByUuid.isEmpty())
            spiders.save(new Spider(uuid, location, desc, true));
        else
            return new ApiResponse(ResponseCode.FAILED, "添加spider失败，spider已存在");

        return new ApiResponse(ResponseCode.SUCCESS, "添加spider成功");
    }

    @PostMapping(value = "delete")
    public ApiResponse deleteSpider(@RequestParam("id") Integer id) {
        Optional<Spider> spiderById = spiders.findById(id);

        if (spiderById.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "删除spider失败，spider不存在");

        spiders.delete(spiderById.get());
        return new ApiResponse(ResponseCode.SUCCESS, "删除spider成功");
    }

    @PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse updateSpider(@RequestBody SpiderJson spiderJson) {
        Integer id = spiderJson.getId();
        String desc = spiderJson.getDescribe();
        String location = spiderJson.getLocation();
        Boolean state = spiderJson.getState();

        Optional<Spider> spiderOpt = spiders.findById(id);

        if (spiderOpt.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "修改spider失败，spider不存在");

        Spider spider = spiderOpt.get();
        spider.setDescribe(desc);
        spider.setLocation(location);
        spider.setState(state);
        spiders.save(spider);
        return new ApiResponse(ResponseCode.SUCCESS, "修改spider成功");
    }

    @GetMapping(value = "get")
    public ApiResponse getSpider(@RequestParam("id") Integer id) {
        Optional<Spider> spider = spiders.findById(id);

        if (spider.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "获取spider失败");

        return new ApiResponseX<>(ResponseCode.SUCCESS, "获取spider成功", spider.get());
    }

    @GetMapping(value = "list")
    public ApiResponse getSpiderList(@PathParam("page") Integer page) {
        Integer pageNum = new Pagination().Page(page);

        Pageable pageable = PageRequest.of(pageNum, 20, Sort.Direction.ASC, "Id");
        Page<Spider> spider = spiders.findAll(pageable);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("totalPage", spider.getTotalPages());

        map.put("totalElements", spider.getTotalElements());

        ArrayList<Spider> spiderlist = new ArrayList<>();
        for (Spider item : spider) {
            spiderlist.add(item);
        }
        map.put("spiderList", spiderlist);

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", map);

    }
}

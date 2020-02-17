package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.controller.json.Spider;
import com.cdgeekcamp.redas.api.core.service.ModifyUrlStatusService;
import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.db.model.*;
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
import java.util.*;

@RestController
@RequestMapping(value = "/spider")
public class SpiderController {
    @Autowired
    private SpiderRepository spiders;

    @Autowired
    private ModifyUrlStatusService modifyUrlStatusService;

    /**
     * 添加爬虫
     * @param spiderJson spider
     * @return ApiResponse
     */
    @PostMapping(value = "add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addSpider(@RequestBody Spider spiderJson) {
        String uuid = spiderJson.getUuid();
        String desc = spiderJson.getDescribe();
        String location = spiderJson.getLocation();

        Optional<com.cdgeekcamp.redas.db.model.Spider> spiderByUuid = spiders.findByUuid(uuid);

        if (spiderByUuid.isEmpty())
            spiders.save(new com.cdgeekcamp.redas.db.model.Spider(uuid, location, desc, true));
        else
            return new ApiResponse(ResponseCode.FAILED, "添加spider失败，spider已存在");

        return new ApiResponse(ResponseCode.SUCCESS, "添加spider成功");
    }

    /**
     * 删除爬虫
     * @param id id
     * @return ApiResponse
     */
    @PostMapping(value = "delete")
    public ApiResponse deleteSpider(@RequestParam("id") Integer id) {
        Optional<com.cdgeekcamp.redas.db.model.Spider> spiderById = spiders.findById(id);

        if (spiderById.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "删除spider失败，spider不存在");

        spiders.delete(spiderById.get());
        return new ApiResponse(ResponseCode.SUCCESS, "删除spider成功");
    }

    /**
     * 修改爬虫
     * @param spiderJson spider
     * @return ApiResponse
     */
    @PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse updateSpider(@RequestBody Spider spiderJson) {
        Integer id = spiderJson.getId();
        String desc = spiderJson.getDescribe();
        String location = spiderJson.getLocation();
        Boolean state = spiderJson.getState();

        Optional<com.cdgeekcamp.redas.db.model.Spider> spiderOpt = spiders.findById(id);

        if (spiderOpt.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "修改spider失败，spider不存在");

        com.cdgeekcamp.redas.db.model.Spider spider = spiderOpt.get();
        spider.setDescribe(desc);
        spider.setLocation(location);
        spider.setState(state);
        spiders.save(spider);
        return new ApiResponse(ResponseCode.SUCCESS, "修改spider成功");
    }

    /**
     * 获取爬虫
     * @param id id
     * @return ApiResponse
     */
    @GetMapping(value = "get")
    public ApiResponse getSpider(@RequestParam("id") Integer id) {
        Optional<com.cdgeekcamp.redas.db.model.Spider> spider = spiders.findById(id);

        if (spider.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "获取spider失败");

        return new ApiResponseX<>(ResponseCode.SUCCESS, "获取spider成功", spider.get());
    }

    /**
     * 获取爬虫列表
     * @param page 页码
     * @return
     */
    @GetMapping(value = "list")
    public ApiResponse getSpiderList(@PathParam("page") Integer page) {
        Integer pageNum = new Pagination().Page(page);

        Pageable pageable = PageRequest.of(pageNum, 20, Sort.Direction.ASC, "Id");
        Page<com.cdgeekcamp.redas.db.model.Spider> spider = spiders.findAll(pageable);

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        ArrayList<com.cdgeekcamp.redas.db.model.Spider> spiderList = new ArrayList<>();

        for (com.cdgeekcamp.redas.db.model.Spider item : spider) {
            spiderList.add(item);
        }

        resultMap.put("spiderList", spiderList);
        resultMap.put("totalPage", spider.getTotalPages());
        resultMap.put("totalElements", spider.getTotalElements());

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", resultMap);

    }

    /**
     * 修改爬虫状态
     * @param spider spider
     */
    @GetMapping(value = "/modifyUrlStatus")
    public void modifyUrlStatus(@RequestParam("spider") String spider){
        if (spider.equals("list")){
            modifyUrlStatusService.modifyPositionsUrlStatus();
        }else if (spider.equals("detail")){
            modifyUrlStatusService.modifyPositionUrlStatus();
        }
    }
}

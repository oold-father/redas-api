package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.db.model.KeyWords;
import com.cdgeekcamp.redas.db.model.KeyWordsRepository;
import com.cdgeekcamp.redas.db.model.Nature;
import com.cdgeekcamp.redas.db.model.PositionUrl;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseMap;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.elasticsearch.common.collect.HppcMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import javax.websocket.server.PathParam;
import java.util.*;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/keyword")
public class KeywordsController {
    @Autowired
    private KeyWordsRepository keyWordsRepository;

    @GetMapping(value = "/get")
    public ApiResponse getKeywordsInfo(
            @RequestParam(name = "page", defaultValue= "0", required=false) Integer page,
            @RequestParam(name = "maxEle", defaultValue= "20", required=false) Integer maxEle) {

        if(page <= 0){
            page = 0;
        }else {
            page = page-1;
        }

        Pageable pageable = PageRequest.of(page, maxEle, Sort.Direction.ASC, "Id");
        Page<KeyWords> keyWords = keyWordsRepository.findAll(pageable);

//        Iterable<KeyWords> keyWords  = keyWordsRepository.findAll();
        ArrayList<Object> keyWords_list = new ArrayList<>();
        for (KeyWords item: keyWords){
            Map<Object, Object> newNode = new HashMap<>();
            newNode.put("id", item.getId());
            newNode.put("keyName", item.getKeyName());
            if (item.getParentId() == null){
                newNode.put("parent", "");
                newNode.put("parent_id", null);
            }else {
                Optional<KeyWords> parent = keyWordsRepository.findById(item.getParentId());
                newNode.put("parent", parent.get().getKeyName());
                newNode.put("parent_id", parent.get().getId());
            }
            keyWords_list.add(newNode);
        }

        ApiResponseMap<String, Object> responseMap = new ApiResponseMap<>(ResponseCode.SUCCESS, "查询成功");
        responseMap.addKeyAndValue("totalElements", keyWords.getTotalElements());
        responseMap.addKeyAndValue("data", keyWords_list);
        return responseMap;
    }
    @GetMapping(value = "/getByParent")
    public ApiResponse getKeywordsInfoByParent(
            @RequestParam(name = "parent_id", defaultValue= "0", required=false) Integer parent_id){

        if(parent_id <= 0){
            parent_id = null;
        }

        Iterable<KeyWords> keyWords = keyWordsRepository.findByParentId(parent_id);
        ArrayList<Object> keyWords_list = new ArrayList<>();
        for (KeyWords item: keyWords){

            Map<Object, Object> newNode = new HashMap<>();
            newNode.put("id", item.getId());
            newNode.put("keyName", item.getKeyName());

            Iterable<KeyWords> childKey  = keyWordsRepository.findByParentId(item.getId());
            newNode.put("leaf", 0 == StreamSupport.stream(childKey.spliterator(), false).count());

            keyWords_list.add(newNode);
        }
        ApiResponseList<Object> responseList = new ApiResponseList<>(ResponseCode.SUCCESS, "查询成功");
        responseList.setResult(keyWords_list);
        return  responseList;
    }

    @PostMapping(value = "/update")
    public ApiResponse updateKeywordsInfo(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "keyName") String name,
            @RequestParam(name = "parent_id", defaultValue= "0", required=false) Integer parent_id
            ) {

        if (id == parent_id){
            return new ApiResponse(ResponseCode.FAILED, "parent_id与id相同, 无法操作");
        }
        Optional<KeyWords> keyWords  = keyWordsRepository.findById(id);

        if (keyWords.isEmpty()){
            return new ApiResponse(ResponseCode.FAILED, "更新失败, id不存在");
        }
        KeyWords keyWord = keyWords.get();
        keyWord.setKeyName(name);

        if (parent_id == 0){
            parent_id = null;
        }
        keyWord.setParentId(parent_id);

        keyWordsRepository.save(keyWord);
        return new ApiResponse(ResponseCode.SUCCESS, "更新成功");
    }

    @PostMapping(value = "/add")
    public ApiResponse addKeywordsInfo(
            @RequestParam(name = "keyName") String name,
            @RequestParam(name = "parent_id", defaultValue= "0", required=false) Integer parent_id,
            @RequestParam(name = "level", defaultValue= "1", required=false) Integer level
    ) {
        if (parent_id <= 0){
            parent_id = null;
        }
        Optional<KeyWords> keyWords  = keyWordsRepository.findByKeyNameAndParentId(name, parent_id);

        if (!keyWords.isEmpty()){
            return new ApiResponse(ResponseCode.FAILED, "添加失败失败, 已存在该keyword");
        }

        KeyWords keyWord = new KeyWords(parent_id, name, level);
        keyWordsRepository.save(keyWord);
        return new ApiResponse(ResponseCode.SUCCESS, "更新成功");
    }

    @PostMapping(value = "/delete")
    public ApiResponse deleteKeywordsInfo(
            @RequestParam(name = "id") Integer id
    ) {
        Optional<KeyWords> keyWords  = keyWordsRepository.findById(id);

        if (keyWords.isEmpty()){
            return new ApiResponse(ResponseCode.FAILED, "删除失败, id不存在");
        }

        Iterable<KeyWords> childKey  = keyWordsRepository.findByParentId(id);

        if (0 != StreamSupport.stream(childKey.spliterator(), false).count()){
            return new ApiResponse(ResponseCode.FAILED, "删除失败, 该字段存在有子字段, 无法删除");
        }

        keyWordsRepository.deleteById(id);
        return new ApiResponse(ResponseCode.SUCCESS, "删除成功");
    }

}

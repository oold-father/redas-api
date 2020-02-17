package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.api.core.service.Pagination;
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
import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/keyword")
public class KeywordsController {
    @Autowired
    private KeyWordsRepository keyWordsRepository;

    /**
     * 获取keywords
     * @param page 页码
     * @param maxEle 本页数量
     * @return ApiResponse
     */
    @GetMapping(value = "/get")
    public ApiResponse getKeywordsInfo(
            @RequestParam(name = "page", defaultValue= "0", required=false) Integer page,
            @RequestParam(name = "maxEle", defaultValue= "20", required=false) Integer maxEle) {

        Integer pageNum = new Pagination().Page(page);

        Pageable pageable = PageRequest.of(pageNum, maxEle, Sort.Direction.ASC, "Id");
        Page<KeyWords> keyWords = keyWordsRepository.findAll(pageable);

        ArrayList<Object> keyWordsList = new ArrayList<>();
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
            keyWordsList.add(newNode);
        }

        ApiResponseMap<String, Object> responseMap = new ApiResponseMap<>(ResponseCode.SUCCESS, "查询成功");
        responseMap.addKeyAndValue("totalElements", keyWords.getTotalElements());
        responseMap.addKeyAndValue("data", keyWordsList);
        return responseMap;
    }

    /**
     * 依照parentId获取keywords
     * @param parentId 父节点id
     * @return ApiResponse
     */
    @GetMapping(value = "/getByParent")
    public ApiResponse getKeywordsInfoByParent(
            @RequestParam(name = "parent_id", defaultValue= "0", required=false) Integer parentId){

        if(parentId <= 0){
            parentId = null;
        }

        Iterable<KeyWords> keyWords = keyWordsRepository.findByParentId(parentId);
        ArrayList<Object> keyWordsList = new ArrayList<>();
        for (KeyWords item: keyWords){

            Map<Object, Object> newNode = new HashMap<>();
            newNode.put("id", item.getId());
            newNode.put("keyName", item.getKeyName());

            Iterable<KeyWords> childKey  = keyWordsRepository.findByParentId(item.getId());
            newNode.put("leaf", 0 == StreamSupport.stream(childKey.spliterator(), false).count());

            keyWordsList.add(newNode);
        }
        ApiResponseList<Object> responseList = new ApiResponseList<>(ResponseCode.SUCCESS, "查询成功");
        responseList.setResult(keyWordsList);
        return  responseList;
    }

    /**
     * 更新keyword信息
     * @param id  keyword的id
     * @param name keyword的keyName
     * @param parentId keyword的keyName
     * @return ApiResponse
     */
    @PostMapping(value = "/update")
    public ApiResponse updateKeywordsInfo(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "keyName") String name,
            @RequestParam(name = "parent_id", defaultValue= "0", required=false) Integer parentId
            ) {

        if (id == parentId){
            return new ApiResponse(ResponseCode.FAILED, "parent_id与id相同, 无法操作");
        }
        Optional<KeyWords> keyWords  = keyWordsRepository.findById(id);

        if (keyWords.isEmpty()){
            return new ApiResponse(ResponseCode.FAILED, "更新失败, id不存在");
        }
        KeyWords keyWord = keyWords.get();

        // 修改name
        keyWord.setKeyName(name);

        if (parentId == 0){
            parentId = null;
        }
        // 修改父节点id
        keyWord.setParentId(parentId);
        // 修改level
        Optional<KeyWords> keyWordsParent  = keyWordsRepository.findById(parentId);
        KeyWords keyWordParent = keyWordsParent.get();
        keyWord.setLevel(keyWordParent.getLevel() + 1);

        keyWordsRepository.save(keyWord);
        return new ApiResponse(ResponseCode.SUCCESS, "更新成功");
    }
    /**
     * 添加keyword信息
     * @param name keyword的keyName
     * @param parentId keyword的keyName
     * @param level  keyword的level
     * @return ApiResponse
     */
    @PostMapping(value = "/add")
    public ApiResponse addKeywordsInfo(
            @RequestParam(name = "keyName") String name,
            @RequestParam(name = "parent_id", defaultValue= "0", required=false) Integer parentId,
            @RequestParam(name = "level", defaultValue= "1", required=false) Integer level
    ) {
        if (parentId <= 0){
            parentId = null;
        }
        Optional<KeyWords> keyWords  = keyWordsRepository.findByKeyNameAndParentId(name, parentId);

        if (!keyWords.isEmpty()){
            return new ApiResponse(ResponseCode.FAILED, "添加失败失败, 已存在该keyword");
        }

        KeyWords keyWord = new KeyWords(parentId, name, level);
        keyWordsRepository.save(keyWord);
        return new ApiResponse(ResponseCode.SUCCESS, "更新成功");
    }

    /**
     * 删除keyword信息
     * @param id  keyword的id
     * @return ApiResponse
     */
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

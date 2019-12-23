package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.db.model.KeyWords;
import com.cdgeekcamp.redas.db.model.KeyWordsRepository;
import com.cdgeekcamp.redas.db.model.PositionRepository;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseMap;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@RestController
@RequestMapping(value = "/webChart")
@ResponseBody
public class WebChartController {
    @Autowired
    private KeyWordsRepository keyWordsRepository;

    @Autowired
    private PositionRepository positionRepository;

    @GetMapping(value = "/getTerm")
    public ApiResponseMap<String, List<String>> getTerm(){
        try{
            ArrayList<String> position = new ArrayList<>();
            ArrayList<String> edu = new ArrayList<>();
            ArrayList<String> exp = new ArrayList<>();
            List<String> city;

            List<KeyWords> keyWords = keyWordsRepository.findByLevel(4);
            for (KeyWords keyWord: keyWords){
                position.add(keyWord.getKeyName());
            }
            LinkedHashSet<String> set = new LinkedHashSet<String>(position.size());
            set.addAll(position);
            position.clear();
            position.addAll(set);

            edu.add("中专");
            edu.add("大专");
            edu.add("本科");
            edu.add("硕士");
            edu.add("博士");
            edu.add("不限");

            exp.add("应届");
            exp.add("1年");
            exp.add("2年");
            exp.add("3年");
            exp.add("4年");
            exp.add("5年");
            exp.add("不限");

            city = positionRepository.findGroupByCity();

            ApiResponseMap<String, List<String>> apiResponseMap = new ApiResponseMap<>(ResponseCode.SUCCESS, "成功");
            apiResponseMap.addKeyAndValue("position", position);
            apiResponseMap.addKeyAndValue("edu", edu);
            apiResponseMap.addKeyAndValue("exp", exp);
            apiResponseMap.addKeyAndValue("city", city);
            return apiResponseMap;
        }catch (Exception e){
            return new ApiResponseMap<>(ResponseCode.FAILED, "出错啦");
        }
    }
}

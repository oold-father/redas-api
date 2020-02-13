package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {
    @Autowired
    private EntityManagerFactoryToResult entityManagerFactoryToResult;

    public ApiResponse getAllOrUserSubList(String sql, Integer pagenum, Integer size, Integer page){
        List<Object[]> resultList = entityManagerFactoryToResult.sqlToResultPage(sql, pagenum, size);

        ArrayList<Map<String, String>> resList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            for (Object[] result : resultList) {
                Map<String, String> map = new HashMap<>();
                map.put("username", result[0].toString());
                map.put("keywords", result[1].toString());
                map.put("hash_key", result[2].toString());
                resList.add(map);
            }

            resultMap.put("totalElements", entityManagerFactoryToResult.sqlToResult(sql).size());
            resultMap.put("page", page);
            resultMap.put("SubList", resList);

            return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", resultMap);
        }catch (Exception e){
            return new ApiResponseX<>(ResponseCode.FAILED, "失败", new HashMap<>());
        }
    }
}

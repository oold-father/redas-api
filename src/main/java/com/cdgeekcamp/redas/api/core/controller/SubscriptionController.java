package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.EntityManagerFactoryToResult;
import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/subscribe")
@ResponseBody
public class SubscriptionController {
    @Autowired
    private EntityManagerFactoryToResult entityManagerFactoryToResult;

    @GetMapping(value = "/allSubList")
    public ApiResponseX allSubList(@RequestParam("search_type") String searchType,
                                   @RequestParam("search") String search,
                                   @RequestParam("page") Integer page,
                                   @RequestParam("size") Integer size){
        Integer pagenum = new Pagination().Page(page);
        String sql = "";
        if("".equals(search)){
            sql = "select a.hash_key,GROUP_CONCAT(a.key_name),user.name FROM " +
                    "(select s.id,s.hash_key,s.user_id,k.key_name from subscription as s inner JOIN keywords as k " +
                    "on s.keyword_id=k.id) as a inner JOIN `user` on a.user_id=user.id GROUP BY a.hash_key";
        }else {
            String sqlString = "select a.hash_key,GROUP_CONCAT(k.key_name),a.name from keywords as k inner join " +
                    "(select user.name,s.hash_key,s.keyword_id FROM user inner JOIN subscription as s on user.id=s.user_id) " +
                    "as a on a.keyword_id=k.id group by a.hash_key having name=\"%s\"";
            sql = String.format(sqlString, search);
        }

        List<Object[]> resultList = entityManagerFactoryToResult.sqlToResultPage(sql, pagenum, size);

        ArrayList<Map<String, String>> resList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            for (Object[] result : resultList) {
                Map<String, String> map = new HashMap<>();
                map.put("username", result[2].toString());
                map.put("keywords", result[1].toString());
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

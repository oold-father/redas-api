package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.EntityManagerFactoryToResult;
import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.db.model.User;
import com.cdgeekcamp.redas.db.model.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/allSubList")
    public ApiResponseX allSubList(@RequestParam("search_type") String searchType,
                                   @RequestParam("search") String search,
                                   @RequestParam("page") Integer page,
                                   @RequestParam("size") Integer size){
        Integer pagenum = new Pagination().Page(page);
        String sql = "";
        if("".equals(search)){
            sql = "select (select name from `user` where id=s.user_id) as username,GROUP_CONCAT(k.key_name) as keyname " +
                    "from subscription s left join keywords k on k.id = s.keyword_id GROUP by s.hash_key";
        }else {
            Optional<User> optionalUser = userRepository.findByName(search);
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                int user_id = user.getId();
                String sqlString = "select (select name from `user` where id=s.user_id) as username,GROUP_CONCAT(k.key_name) as keyname " +
                        "from subscription s left join keywords k on k.id = s.keyword_id where s.user_id=\"%d\" GROUP by s.hash_key";
                sql = String.format(sqlString, user_id);
            }else {
                return new ApiResponseX<>(ResponseCode.FAILED, "用户不存在", new HashMap<>());
            }
        }

        List<Object[]> resultList = entityManagerFactoryToResult.sqlToResultPage(sql, pagenum, size);

        ArrayList<Map<String, String>> resList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            for (Object[] result : resultList) {
                Map<String, String> map = new HashMap<>();
                map.put("username", result[0].toString());
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

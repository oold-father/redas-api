package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.EntityManagerFactoryToResult;
import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.db.model.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
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

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserOpenRepository userOpenRepository;

    @GetMapping(value = "/allSubList")
    public ApiResponse allSubList(@RequestParam("search_type") String searchType,
                                  @RequestParam("search") String search,
                                  @RequestParam("page") Integer page,
                                  @RequestParam("size") Integer size){
        Integer pagenum = new Pagination().Page(page);
        String sql = "";
        if("".equals(search)){
            sql = "select ANY_VALUE(u.name) as username,GROUP_CONCAT(k.key_name) as keyname,s.hash_key from subscription as s " +
                    "left join keywords as k on k.id = s.keyword_id left join `user` as u on u.id=s.user_id GROUP by s.hash_key";
        }else {
            Optional<User> optionalUser = userRepository.findByName(search);
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                int user_id = user.getId();
                String sqlString = "select ANY_VALUE(u.name) as username,GROUP_CONCAT(k.key_name) as keyname,s.hash_key " +
                        "from subscription as s left join keywords as k on k.id = s.keyword_id left join `user` as u " +
                        "on u.id=s.user_id where s.user_id=\"%d\" GROUP by s.hash_key";
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

    @GetMapping(value = "/userSubList")
    public ApiResponse userSubList(@RequestParam("search_type") String searchType,
                                  @RequestParam("open_id") String open_id,
                                  @RequestParam("page") Integer page,
                                  @RequestParam("size") Integer size){
        Integer pagenum = new Pagination().Page(page);

        Optional<UserOpen> userOpenOptional = userOpenRepository.findByOpenId(open_id);

        String sql = "";
        if (userOpenOptional.isPresent()){
            UserOpen userOpen = userOpenOptional.get();
            Optional<User> optionalUser = userRepository.findById(userOpen.getUserId());
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                int user_id = user.getId();
                String sqlString = "select ANY_VALUE(u.name) as username,GROUP_CONCAT(k.key_name) as keyname,s.hash_key " +
                        "from subscription as s left join keywords as k on k.id = s.keyword_id left join `user` as u " +
                        "on u.id=s.user_id where s.user_id=\"%d\" GROUP by s.hash_key";
                sql = String.format(sqlString, user_id);
            }else {
                return new ApiResponseX<>(ResponseCode.FAILED, "用户不存在", new HashMap<>());
            }
        }else {
            return new ApiResponseX<>(ResponseCode.FAILED, "用户不存在", new HashMap<>());
        }

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

    @GetMapping(value = "/deleteSub")
    public ApiResponse deleteSub(@RequestParam("open_id") String open_id,
                          @RequestParam("hash_key") String hash_key){
        Optional<UserOpen> userOpenOptional = userOpenRepository.findByOpenId(open_id);
        try{
            if (userOpenOptional.isPresent()){

                UserOpen userOpen = userOpenOptional.get();
                Optional<User> userOptional = userRepository.findById(userOpen.getUserId());
                List<Subscription> subscriptions = subscriptionRepository.findByHashKey(hash_key);

                if(userOptional.isPresent()){
                    User user = userOptional.get();
                    for (Subscription subscription: subscriptions){
                        if (user.getId().equals(subscription.getUserId())){
                            subscriptionRepository.deleteById(subscription.getId());
                        }
                    }
                }
            }else {
                return new ApiResponse(ResponseCode.FAILED, "删除失败");
            }
            return new ApiResponse(ResponseCode.SUCCESS, "删除成功");
        }catch (Exception e){
            return new ApiResponse(ResponseCode.FAILED, "删除失败");
        }
    }
}

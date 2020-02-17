package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.db.model.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubscriptionService {
    @Autowired
    private EntityManagerFactoryToResult entityManagerFactoryToResult;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOpenRepository userOpenRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

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

    private void deleteSubByUserId(Integer user_id, String hash_key) {
        Optional<User> userOptional = userRepository.findById(user_id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Subscription> subscriptions =
                    subscriptionRepository.findByUserIdAndHashKey(user.getId(), hash_key);

            for (Subscription subscription : subscriptions)
                subscriptionRepository.deleteById(subscription.getId());
        }
    }

    public void deleteSub(String open_id, String hash_key) {
        Optional<UserOpen> userOpenOptional = userOpenRepository.findByOpenId(open_id);

        // 根据OpenID查询的结果为空，表明用户没用订阅过，不需要处理
        if (userOpenOptional.isPresent()) {
            UserOpen userOpen = userOpenOptional.get();
            deleteSubByUserId(userOpen.getUserId(), hash_key);
        }
    }
}

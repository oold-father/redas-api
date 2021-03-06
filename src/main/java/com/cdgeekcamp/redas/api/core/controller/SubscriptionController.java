package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.EntityManagerFactoryToResult;
import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.api.core.service.SqlStatement;
import com.cdgeekcamp.redas.api.core.service.SubscriptionService;
import com.cdgeekcamp.redas.db.model.*;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private UserOpenRepository userOpenRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private KeyWordsRepository keyWordsRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * 获取所有的订阅
     *
     * @param searchType 查询类型
     * @param search     查询条件
     * @param page       页码
     * @param size       每页条数
     * @return ApiResponse
     */
    @GetMapping(value = "/allSubList")
    public ApiResponse allSubList(@RequestParam("search_type") String searchType,
                                  @RequestParam("search") String search,
                                  @RequestParam("page") Integer page,
                                  @RequestParam("size") Integer size) {
        Integer pagenum = new Pagination().Page(page);
        String sql = "";

        if ("".equals(search)) {
            // 查询条件不存在，查询所有订阅
            sql = new SqlStatement().getAllSubscriptSql();
        } else {
            // 查询条件存在，查询一个用户的订阅
            Optional<User> optionalUser = userRepository.findByName(search);

            // 判断用户存在
            if (optionalUser.isEmpty()) {
                return new ApiResponseX<>(ResponseCode.FAILED, "用户不存在", new HashMap<>());
            }

            sql = new SqlStatement().getUserSubscriptSql(optionalUser.get());
        }

        return subscriptionService.getAllOrUserSubList(sql, pagenum, size, page);
    }

    /**
     * 查询用户的订阅列表
     *
     * @param searchType 查询类型
     * @param open_id    open_id
     * @param page       页码
     * @param size       每页条数
     * @return ApiResponse
     */
    @GetMapping(value = "/userSubList")
    public ApiResponse userSubList(@RequestParam("search_type") String searchType,
                                   @RequestParam("open_id") String open_id,
                                   @RequestParam("page") Integer page,
                                   @RequestParam("size") Integer size) {
        Integer pagenum = new Pagination().Page(page);
        Optional<UserOpen> userOpenOptional = userOpenRepository.findByOpenId(open_id);
        String sql = "";

        // 判断useropen表中的user是否存在
        if (userOpenOptional.isEmpty()) {
            return new ApiResponseX<>(ResponseCode.FAILED, "用户不存在", new HashMap<>());
        }

        UserOpen userOpen = userOpenOptional.get();
        Optional<User> optionalUser = userRepository.findById(userOpen.getUserId());

        // 判断用户是否存在
        if (optionalUser.isEmpty()) {
            return new ApiResponseX<>(ResponseCode.FAILED, "用户不存在", new HashMap<>());
        }

        sql = new SqlStatement().getUserSubscriptSql(optionalUser.get());

        return subscriptionService.getAllOrUserSubList(sql, pagenum, size, page);
    }

    /**
     * 添加订阅
     *
     * @param openId  open_id
     * @param type    登陆平台类型
     * @param content 订阅
     * @return ApiResponse
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(value = "add")
    public ApiResponse addSub(@RequestParam(name = "openId") String openId,
                              @RequestParam(name = "type", defaultValue = "redas") String type,
                              @RequestBody Object content
    ) throws NoSuchAlgorithmException {
        // 根据openId判断用户是否存在， 不存在则创建用户
        Optional<UserOpen> userOpen = userOpenRepository.findByOpenId(openId);
        UserOpen newUserOpen = null;
        if (userOpen.isEmpty()) {
            //  生成一个16位随机字符串
            String userStr = RandomStringUtils.randomAlphanumeric(16);
            User newUser = userRepository.save(new User(type + "_" + userStr, userStr, userStr));
            newUserOpen = userOpenRepository.save(new UserOpen(newUser.getId(), openId, type));
        }

        if (newUserOpen == null) {
            newUserOpen = userOpen.get();
        }

        LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap) content;
        if (linkedHashMap.isEmpty()) {
            return new ApiResponse(ResponseCode.SUCCESS, "未选择任何主题");
        }
        String baseString = openId + type + content.toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(baseString.getBytes(StandardCharsets.UTF_8));
        String hashStr = RedasString.bytesToHex(hash);

        List<Subscription> isSubscription = subscriptionRepository.findByHashKey(hashStr);
        if (isSubscription.isEmpty()) {
            Integer userId = newUserOpen.getUserId();
            for (Map.Entry<String, Object> entry : linkedHashMap.entrySet()) {
                subscriptionRepository.save(new Subscription(userId, (Integer) entry.getValue(), hashStr));
            }
            return new ApiResponse(ResponseCode.SUCCESS, "订阅成功");
        } else {
            return new ApiResponse(ResponseCode.FAILED, "已经订阅过了");
        }

    }

    /**
     * 删除订阅
     *
     * @param open_id  open_id
     * @param hash_key hash_key
     * @return ApiResponse
     */
    @GetMapping(value = "/deleteSub")
    public ApiResponse deleteSub(@RequestParam("open_id") String open_id,
                                 @RequestParam("hash_key") String hash_key) {
        try {
            subscriptionService.deleteSub(open_id, hash_key);
        } catch (Exception e) {
            return new ApiResponse(ResponseCode.FAILED, "删除失败");
        }

        return new ApiResponse(ResponseCode.SUCCESS, "删除成功");
    }
}

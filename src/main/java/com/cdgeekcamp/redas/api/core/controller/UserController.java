package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.controller.json.User;
import com.cdgeekcamp.redas.db.model.UserRepository;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserRepository users;

    /**
     * 添加用户
     * @param userJson 用户信息
     * @return ApiResponse
     */
    @PostMapping(value = "add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse addUser(@RequestBody User userJson) {
        String name = userJson.getName();
        String pwd = userJson.getPwd();
        String phone = userJson.getPhone();

        Optional<com.cdgeekcamp.redas.db.model.User> userOpt = users.findByName(name);

        if (userOpt.isPresent())
            return new ApiResponse(ResponseCode.FAILED, "用户名已存在");

        userOpt = users.findByPhone(phone);

        if (userOpt.isPresent())
            return new ApiResponse(ResponseCode.FAILED, "电话号码已被使用");

        users.save(new com.cdgeekcamp.redas.db.model.User(name, pwd, phone));
        return new ApiResponse(ResponseCode.SUCCESS, "添加成功");
    }

    /**
     * 删除用户
     * @param id id
     * @return ApiResponse
     */
    @PostMapping(value = "delete")
    public ApiResponse deleteUser(@RequestParam("id") Integer id) {
        Optional<com.cdgeekcamp.redas.db.model.User> userOpt = users.findById(id);
        userOpt.ifPresent(user -> users.delete(user));

        return new ApiResponse(ResponseCode.SUCCESS, "删除成功");
    }

    /**
     * 修改用户信息
     * @param userJson 用户信息
     * @return ApiResponse
     */
    @PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse updateUser(@RequestBody User userJson) {
        int userId = userJson.getId();
        String pwd = userJson.getPwd();
        String phone = userJson.getPhone();

        Optional<com.cdgeekcamp.redas.db.model.User> userOpt = users.findById(userId);

        if (userOpt.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "修改失败，用户id不存在");

        com.cdgeekcamp.redas.db.model.User user = userOpt.get();
        user.setPwd(pwd);
        user.setPhone(phone);
        users.save(user);
        return new ApiResponse(ResponseCode.SUCCESS, "修改成功");
    }

    /**
     * 获取用户信息
     * @param id id
     * @return ApiResponse
     */
    @GetMapping(value = "get")
    public ApiResponse getUser(@RequestParam("id") int id) {
        Optional<com.cdgeekcamp.redas.db.model.User> userOpt = users.findById(id);

        if (userOpt.isEmpty())
            return new ApiResponse(ResponseCode.FAILED, "查询失败，用户id不存在");

        return new ApiResponseX<>(ResponseCode.SUCCESS, "查询成功", userOpt.get());
    }

    /**
     * 用户列表
     * @return ApiResponse
     */
    @GetMapping(value = "list")
    public ApiResponse getUserList() {
        ApiResponseList<com.cdgeekcamp.redas.db.model.User> apiResponseList =
                new ApiResponseList<>(ResponseCode.SUCCESS, "查询成功");

        for (com.cdgeekcamp.redas.db.model.User item : users.findAll())
            apiResponseList.addValue(item);

        return apiResponseList;
    }
}

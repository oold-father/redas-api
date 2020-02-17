package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
import com.cdgeekcamp.redas.lib.core.jsonObject.JsonObject;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController

@RequestMapping(value = "/script")
public class ScriptController {

    /**
     * 获取元数据的哈希值
     * @param recrPage 职位信息元数据
     * @return ApiResponse
     */
    @GetMapping(value = "/hashString")
    public ApiResponseX<String> getHashString(@RequestBody RecrPage recrPage) throws NoSuchAlgorithmException {

        JsonObject recrPageJson = new JsonObject();
        String content = recrPageJson.toJson(recrPage);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        String hash_str = RedasString.bytesToHex(hash);

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", hash_str);
    }
}

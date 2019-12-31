package com.cdgeekcamp.redas.api.core.controller;


import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
import com.cdgeekcamp.redas.lib.core.jsonObject.RecrPageJson;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController

@RequestMapping(value = "/script")
public class ScriptController {
//    @PostMapping(value = "/hashString")
    @GetMapping(value = "/hashString")
    public ApiResponseX getHashString(@RequestBody RecrPage recrPage) throws NoSuchAlgorithmException {

        RecrPageJson recrPageJson = new RecrPageJson();
        String content = recrPageJson.toJson(recrPage);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        String hash_str = RedasString.bytesToHex(hash);

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", hash_str);
    }
}

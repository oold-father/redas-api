package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.esConfig.EsApiCoreConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/es")
public class esController {
    @Autowired
    private EsApiCoreConfig esApiCoreConfig;

    @GetMapping(value = "/positions")
    public ApiResponse esGetPositions(@RequestParam String position,
                                      @RequestParam(defaultValue = "不限") String adress,
                                      @RequestParam(defaultValue = "不限") String exp,
                                      @RequestParam(defaultValue = "不限") String edu,
                                      @RequestParam(defaultValue = "不限") String stage,
                                      @RequestParam(defaultValue = "不限") String scale,
                                      @RequestParam(defaultValue = "不限") String nature
                                      ) throws IOException {
        RestClient restClient = RestClient.builder(
                new HttpHost(esApiCoreConfig.getHost(), esApiCoreConfig.getPort(), esApiCoreConfig.getScheme())).build();
        Request request = new Request(
                "GET",
                String.format("/%s/%s/_search", esApiCoreConfig.getIndex(), esApiCoreConfig.getType()));

//        HashMap<String, Object> requestMap = new HashMap<String, Object>();
//        requestMap.put();
        String key = "python";
        String query = String.format("{\n" +
                    "    \"query\": {\n" +
                    "        \"bool\": {\n" +
                    "            \"should\": [\n" +
                    "                {\n" +
                    "                    \"match\": {\n" +
                    "                        \"companyName\": \"%s\"\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"match\": {\n" +
                    "                        \"companyNature\": \"%s\"\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"match\": {\n" +
                    "                        \"posDesc\": \"%s\"\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"match\": {\n" +
                    "                        \"position\": \"%s\"\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"match\": {\n" +
                    "                        \"tagList\": \"%s\"\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", key, key, key, key, key);


        request.setEntity(new NStringEntity(
                query,
                ContentType.APPLICATION_JSON));

        Response response = restClient.performRequest(request);

        int statusCode = response.getStatusLine().getStatusCode();

        ApiResponseList<HashMap> result = new ApiResponseList<>(ResponseCode.SUCCESS, "查询成功");

        if (statusCode == 200){
            String responseBody = EntityUtils.toString(response.getEntity());

            Map res = new ObjectMapper().readValue(responseBody, HashMap.class);

            HashMap firstHits = (HashMap)res.get("hits");
            HashMap total = (HashMap)firstHits.get("total");
            Integer totalValue = (Integer)total.get("value");

            System.out.println(String.format("totalFind: %s", totalValue.toString()));

            ArrayList<HashMap> reHits = (ArrayList<HashMap>)firstHits.get("hits");
            for (HashMap eachHit: reHits){
                HashMap _source = (HashMap)eachHit.get("_source");
                result.addValue(_source);
            }
        }
        else {
            return new ApiResponse(ResponseCode.FAILED, "查询错误");
        }

        restClient.close();
        return result;
    }
}

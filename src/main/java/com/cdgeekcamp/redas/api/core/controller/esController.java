package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseMap;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.esConfig.EsApiCoreConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
    public ApiResponse esGetPositions(@RequestParam(name = "position", defaultValue = "不限") String position,
                                      @RequestParam(name = "address", defaultValue = "不限") String address,
                                      @RequestParam(name = "exp", defaultValue = "不限") String exp,
                                      @RequestParam(name = "edu", defaultValue = "不限") String edu,
                                      @RequestParam(name = "stage", defaultValue = "不限") String stage,
                                      @RequestParam(name = "scale", defaultValue = "不限") String scale,
                                      @RequestParam(name = "nature", defaultValue = "不限") String nature
                                      ) throws IOException {

//        low-level
//        RestClient restClient = RestClient.builder(
//                new HttpHost(esApiCoreConfig.getHost(), esApiCoreConfig.getPort(), esApiCoreConfig.getScheme())).build();
//        Request request = new Request(
//                "GET",
//                String.format("/%s/%s/_search", esApiCoreConfig.getIndex(), esApiCoreConfig.getType()));
//
////        HashMap<String, Object> requestMap = new HashMap<String, Object>();
////        requestMap.put();
//        String key = "python";
//        String query = String.format("{\n" +
//                    "    \"query\": {\n" +
//                    "        \"bool\": {\n" +
//                    "            \"should\": [\n" +
//                    "                {\n" +
//                    "                    \"match\": {\n" +
//                    "                        \"companyName\": \"%s\"\n" +
//                    "                    }\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"match\": {\n" +
//                    "                        \"companyNature\": \"%s\"\n" +
//                    "                    }\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"match\": {\n" +
//                    "                        \"posDesc\": \"%s\"\n" +
//                    "                    }\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"match\": {\n" +
//                    "                        \"position\": \"%s\"\n" +
//                    "                    }\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"match\": {\n" +
//                    "                        \"tagList\": \"%s\"\n" +
//                    "                    }\n" +
//                    "                }\n" +
//                    "            ]\n" +
//                    "        }\n" +
//                    "    }\n" +
//                    "}", key, key, key, key, key);
//
//
//        request.setEntity(new NStringEntity(
//                query,
//                ContentType.APPLICATION_JSON));
//
//        Response response = restClient.performRequest(request);
//
//        int statusCode = response.getStatusLine().getStatusCode();
//
//        ApiResponseList<HashMap> result = new ApiResponseList<>(ResponseCode.SUCCESS, "查询成功");
//
//        if (statusCode == 200){
//            String responseBody = EntityUtils.toString(response.getEntity());
//
//            Map res = new ObjectMapper().readValue(responseBody, HashMap.class);
//
//            HashMap firstHits = (HashMap)res.get("hits");
//            HashMap total = (HashMap)firstHits.get("total");
//            Integer totalValue = (Integer)total.get("value");
//
//            System.out.println(String.format("totalFind: %s", totalValue.toString()));
//
//            ArrayList<HashMap> reHits = (ArrayList<HashMap>)firstHits.get("hits");
//            for (HashMap eachHit: reHits){
//                HashMap _source = (HashMap)eachHit.get("_source");
//                result.addValue(_source);
//            }
//        }
//        else {
//            return new ApiResponse(ResponseCode.FAILED, "查询错误");
//        }
//
//        restClient.close();
//        high-level

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esApiCoreConfig.getHost(), esApiCoreConfig.getPort(), esApiCoreConfig.getScheme())));

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("position", position);
        paramMap.put("location", address);
//        paramMap.put("address", address);
        paramMap.put("exp", exp);
        paramMap.put("edu", edu);
        paramMap.put("stage", stage);
        paramMap.put("scale", scale);
        paramMap.put("companyNature", nature);


        SearchRequest searchRequest = new SearchRequest(esApiCoreConfig.getIndex());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        for (Map.Entry<String, String> param : paramMap.entrySet()){
            if(!param.getValue().equals("不限")){
                boolQueryBuilder.must(QueryBuilders.matchQuery(param.getKey(), param.getValue())
                                                   .operator(Operator.fromString("AND")));
            }
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        // 发送请求， 获取结果
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        // 获取结果中的Hits部分
        SearchHits hits = response.getHits();
        // Hits下的total部分
        TotalHits totalHits = hits.getTotalHits();
        // 查询到的数据总数
        long numHits = totalHits.value;
        // 查询到的数据列表前10个
        SearchHit[] searchHits = hits.getHits();

        ArrayList<Map> resultList = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            // do something with the SearchHit

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            resultList.add(sourceAsMap);
        }

        ApiResponseMap result = new ApiResponseMap<>(ResponseCode.SUCCESS, "搜索成功");
        result.addKeyAndValue("totalFind", numHits);
        result.addKeyAndValue("Hits", resultList);

        client.close();
        return result;
    }
}

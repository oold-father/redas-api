package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.esService;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseMap;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
import com.cdgeekcamp.redas.lib.core.esConfig.EsApiCoreConfig;
import com.cdgeekcamp.redas.lib.core.jsonObject.JsonObject;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/es")
public class esController {
    @Autowired
    private EsApiCoreConfig esApiCoreConfig;

    @Autowired
    private esService esService;

    @GetMapping(value = "/positions")
    public ApiResponse esGetPositions(@RequestParam(name = "position", defaultValue = "不限") String position,
                                      @RequestParam(name = "address", defaultValue = "不限") String address,
                                      @RequestParam(name = "exp", defaultValue = "不限") String exp,
                                      @RequestParam(name = "edu", defaultValue = "不限") String edu,
                                      @RequestParam(name = "stage", defaultValue = "不限") String stage,
                                      @RequestParam(name = "scale", defaultValue = "不限") String scale,
                                      @RequestParam(name = "nature", defaultValue = "不限") String nature,
                                      @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                      @RequestParam(name = "maxEle", defaultValue = "20", required = false) Integer maxEle
    ) throws IOException {

        RestHighLevelClient client = esService.getClient();

        if (page <= 0) {
            page = 0;
        } else {
            page = page - 1;
        }

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

        for (Map.Entry<String, String> param : paramMap.entrySet()) {
            if (!param.getValue().equals("不限")) {
                boolQueryBuilder.must(QueryBuilders.matchQuery(param.getKey(), param.getValue())
                        .operator(Operator.fromString("AND")));
            }
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.from(page * maxEle);
        searchSourceBuilder.size(maxEle);

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

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

    @PostMapping(value = "/positions/addDoc")
    public ApiResponse addPosition(
            @RequestBody RecrPage recrPage
            ) throws NoSuchAlgorithmException, IOException {
        RestHighLevelClient client = esService.getClient();


        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(recrPage.getSrcUrl().getBytes(StandardCharsets.UTF_8));
        String id = RedasString.bytesToHex(hash);

        String jsonString = new JsonObject<RecrPage>().toJson(recrPage);

        IndexRequest request = new IndexRequest(esApiCoreConfig.getIndex())
                .id(id)
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            System.out.println(String.format("文档不存在, 创建新的文档, 文档id:%s", id));
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            System.out.println(String.format("文档已存在, 更新文档, 文档id:%s", id));
        }

        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getFailed() > 0) {
            return new ApiResponse(ResponseCode.FAILED, "发生未知错误");
        }

        return new ApiResponse(ResponseCode.SUCCESS, "添加成功");
    }

    @GetMapping(value = "/statistics/eduAndTime")
    public ApiResponse statisticsEduAndTime(
            @RequestParam(name = "headDate") String headDate,
            @RequestParam(name = "endDate") String endDate,
            @RequestParam(name = "edu") String edu,
            @RequestParam(name = "unit", defaultValue = "week") String unit
    ) throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("publishTime").gte(headDate).lte(endDate))
                .must(QueryBuilders.matchQuery("edu", edu));

//        System.out.println(boolQueryBuilder.toString());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .aggregation(AggregationBuilders
                        .dateHistogram("count_position")
                        .field("publishTime")
                        .format("yyyy-MM-dd")
                        .calendarInterval(new DateHistogramInterval(unit))
                )
                .size(0);

        SearchRequest searchRequest = new SearchRequest(esApiCoreConfig.getIndex())
                .source(searchSourceBuilder);

        Aggregations aggs = esService.getClient()
                .search(searchRequest, RequestOptions.DEFAULT)
                .getAggregations();

        ParsedDateHistogram count_position = aggs.get("count_position");
        ApiResponseList<Map> responseList = new ApiResponseList<>(ResponseCode.SUCCESS, "获取成功");
        for (Histogram.Bucket groups: count_position.getBuckets()){
            Map<Object, Object> group = new HashMap();
            group.put("date", groups.getKeyAsString());
            group.put("count", groups.getDocCount());
            responseList.addValue(group);
        }

             return responseList;
    }
}

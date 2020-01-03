package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.api.core.service.esService;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseMap;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
import com.cdgeekcamp.redas.lib.core.esConfig.EsApiCoreConfig;
import com.cdgeekcamp.redas.lib.core.jsonObject.JsonObject;
import com.cdgeekcamp.redas.lib.core.util.EduLevelMap;
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
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public ApiResponse esGetPositions(
//            @RequestParam(name = "position", defaultValue = "不限") String position,
                                      @RequestAttribute(name = "position") String[] position,
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

        Integer pageNum = new Pagination().Page(page);
        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("position", position);
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
        BoolQueryBuilder positionBoolQueryBuilder = QueryBuilders.boolQuery();

        for (String item : position) {
            if (!item.equals("不限")) {
                positionBoolQueryBuilder.should(QueryBuilders.matchQuery("position", item)
                        .operator(Operator.fromString("AND")));
            }
        }
        boolQueryBuilder.must(positionBoolQueryBuilder);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .from(pageNum * maxEle)
                .size(maxEle)
                .timeout(new TimeValue(60, TimeUnit.SECONDS));

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
            @RequestParam(name = "city") String city,
            @RequestParam(name = "edu") String edu,
            @RequestAttribute(name = "position") String[] position
    ) throws IOException, ParseException {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("publishTime").gte(headDate).lte(endDate))
                .must(QueryBuilders.matchQuery("city", city).operator(Operator.fromString("AND")));

        BoolQueryBuilder positionBoolQueryBuilder = QueryBuilders.boolQuery();

        for (String item : position) {
            if (!item.equals("不限")) {
                positionBoolQueryBuilder.should(QueryBuilders.matchQuery("position", item)
                        .operator(Operator.fromString("AND")));
            }
        }

        boolQueryBuilder.must(positionBoolQueryBuilder);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .aggregation(AggregationBuilders
                        .terms("count_position")
                        .field("eduLevel")

                )
                .size(0);

        SearchRequest searchRequest = new SearchRequest(esApiCoreConfig.getIndex())
                .source(searchSourceBuilder);

        Aggregations aggs = esService.getClient()
                .search(searchRequest, RequestOptions.DEFAULT)
                .getAggregations();

        ParsedLongTerms count_position = aggs.get("count_position");
        ApiResponseList<Map> responseList = new ApiResponseList<>(ResponseCode.SUCCESS, "获取成功");
        for (Terms.Bucket groups : count_position.getBuckets()) {
            Number key = groups.getKeyAsNumber();
            Number value = groups.getDocCount();

            String keyAsString = EduLevelMap.getLevel(key.intValue());

            Map<Object, Object> resultMap = new HashMap<>();
            resultMap.put("name", keyAsString);
            resultMap.put("y", value.intValue());

            if (edu.equals(keyAsString)){
                resultMap.put("sliced", true);
            }

            responseList.addValue(resultMap);
        }
        return responseList;
    }

    @GetMapping(value = "/statistics/ExpAndTime")
    public ApiResponse statisticsExpAndTime(@RequestParam(name = "headDate") String headDate,
                                            @RequestParam(name = "endDate") String endDate,
                                            @RequestParam(name = "city") String city,
                                            @RequestParam(name = "exp") String exp,
                                            @RequestAttribute(name = "position") String[] position
    ) throws IOException, ParseException {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("publishTime").gte(headDate).lte(endDate))
                .must(QueryBuilders.matchQuery("city", city).operator(Operator.fromString("AND")));

        BoolQueryBuilder positionBoolQueryBuilder = QueryBuilders.boolQuery();

        for (String item : position) {
            if (!item.equals("不限")) {
                positionBoolQueryBuilder.should(QueryBuilders.matchQuery("position", item)
                        .operator(Operator.fromString("AND")));
            }
        }

        boolQueryBuilder.must(positionBoolQueryBuilder);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .aggregation(AggregationBuilders
                        .terms("count_position")
                        .field("expMin")
                )
                .size(0);

        SearchRequest searchRequest = new SearchRequest(esApiCoreConfig.getIndex())
                .source(searchSourceBuilder);

        Aggregations aggs = esService.getClient()
                .search(searchRequest, RequestOptions.DEFAULT)
                .getAggregations();

        ParsedLongTerms count_position = aggs.get("count_position");
        ApiResponseList<Map> responseList = new ApiResponseList<>(ResponseCode.SUCCESS, "获取成功");

        Map<Object, Object> moreThan5Map = new HashMap<>();
        moreThan5Map.put("name", "5年");
        moreThan5Map.put("y", 0);

        for (Terms.Bucket groups : count_position.getBuckets()) {
            Number key = groups.getKeyAsNumber();
            Number value = groups.getDocCount();

            Map<Object, Object> resultMap = new HashMap<>();
            if (key.intValue() == 0){
                resultMap.put("name", "应届");
                resultMap.put("y", value.intValue());
            }else if (key.intValue() >=5){
                moreThan5Map.put("y", (int)moreThan5Map.get("y") + value.intValue());
                continue;
            }else {
                resultMap.put("name", String.format("%s年", key.toString()));
                resultMap.put("y", value.intValue());
            }

            if (exp.equals(resultMap.get("name"))){
                resultMap.put("sliced", true);
            }

            responseList.addValue(resultMap);
        }

        responseList.addValue(moreThan5Map);

        return responseList;
    }
}

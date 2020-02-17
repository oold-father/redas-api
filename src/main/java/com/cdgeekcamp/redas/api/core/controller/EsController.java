package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.api.core.service.EsService;
import com.cdgeekcamp.redas.api.core.config.EsConfig;
import com.cdgeekcamp.redas.lib.core.api.ApiResponse;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseMap;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.api.receivedParameter.RecrPage;
import com.cdgeekcamp.redas.lib.core.jsonObject.JsonObject;
import com.cdgeekcamp.redas.lib.core.util.EduLevelMap;
import com.cdgeekcamp.redas.lib.core.util.RedasString;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/es")
public class EsController {
    @Autowired
    private EsConfig esConfig;

    @Autowired
    private EsService esService;

    private Logger log;

    {
        log = LoggerFactory.getLogger(EsController.class);
    }

    @GetMapping(value = "/positions")
    public ApiResponse esGetPositions(
                                      @RequestAttribute(name = "position") String position,
                                      @RequestParam(name = "address", defaultValue = "不限") String address,
                                      @RequestParam(name = "exp", defaultValue = "不限") String exp,
                                      @RequestParam(name = "edu", defaultValue = "不限") String edu,
                                      @RequestParam(name = "stage", defaultValue = "不限") String stage,
                                      @RequestParam(name = "scale", defaultValue = "不限") String scale,
                                      @RequestParam(name = "nature", defaultValue = "不限") String nature,
                                      @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                      @RequestParam(name = "maxEle", defaultValue = "20", required = false) Integer maxEle
    ) throws IOException {

        // 需要相同操作的参数放入Map, 便于遍历
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("address", address);
        paramMap.put("exp", exp);
        paramMap.put("edu", edu);
        paramMap.put("stage", stage);
        paramMap.put("scale", scale);
        paramMap.put("companyNature", nature);
        // 构造dsl的数据筛选条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, String> param : paramMap.entrySet()) {
            boolean paramIsLimited = !param.getValue().equals("不限");
            if (paramIsLimited) {
                boolQueryBuilder.must(QueryBuilders.matchQuery(param.getKey(), param.getValue())
                        .operator(Operator.fromString("AND")));
            }
        }
        BoolQueryBuilder positionBoolQueryBuilder = QueryBuilders.boolQuery();

        for (String item : position.split(",")) {
            boolean itemIsLimited = !item.equals("不限");
            if (itemIsLimited) {
                positionBoolQueryBuilder.should(QueryBuilders.matchQuery("position", item)
                        .operator(Operator.fromString("AND")));
            }
        }
        boolQueryBuilder.must(positionBoolQueryBuilder);
        // 构造dsl的参数条件
        Integer pageNum = new Pagination().Page(page);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .from(pageNum * maxEle)
                .size(maxEle)
                .timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 构造es客户端
        RestHighLevelClient client = esService.getClient();
        // 构造DSL请求
        SearchRequest searchRequest = new SearchRequest(esConfig.getIndex());
        searchRequest.source(searchSourceBuilder);
        // 发送请求， 获取结果
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        // 获取结果中的Hits部分
        SearchHits hits = response.getHits();
        // Hits下的total部分
        TotalHits totalHits = hits.getTotalHits();
        // 查询到的数据总数
        long numHits = totalHits.value;
        // 查询到的数据列表
        SearchHit[] searchHits = hits.getHits();

        ArrayList<Map<String, Object>> resultList = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            // 从查询结果中筛选信息
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Map<String, Object> positionInfo = new HashMap<>();
            String posDesc = sourceAsMap.get("posDesc").toString();

            positionInfo.put("address", sourceAsMap.get("address"));
            positionInfo.put("advantage", sourceAsMap.get("advantage"));
            positionInfo.put("city", sourceAsMap.get("city"));
            positionInfo.put("companyName", sourceAsMap.get("companyName"));
            positionInfo.put("companyNature", sourceAsMap.get("companyNature"));
            positionInfo.put("edu", sourceAsMap.get("edu"));
            positionInfo.put("exp", sourceAsMap.get("exp"));
            positionInfo.put("posDesc", posDesc.split("\n"));
            positionInfo.put("position", sourceAsMap.get("position"));
            positionInfo.put("publishTime", sourceAsMap.get("publishTime"));
            positionInfo.put("salary", sourceAsMap.get("salary"));
            positionInfo.put("scale", sourceAsMap.get("scale"));
            positionInfo.put("stage", sourceAsMap.get("stage"));
            positionInfo.put("tagList", sourceAsMap.get("tagList"));

            resultList.add(positionInfo);
        }

        ApiResponseMap<String, Object> result = new ApiResponseMap<>(ResponseCode.SUCCESS, "搜索成功");
        result.addKeyAndValue("totalFind", numHits);
        result.addKeyAndValue("Hits", resultList);

        client.close();
        return result;
    }

    @PostMapping(value = "/positions/addDoc")
    public ApiResponse addPosition(
            @RequestBody RecrPage recrPage
    ) throws NoSuchAlgorithmException, IOException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(recrPage.getSrcUrl().getBytes(StandardCharsets.UTF_8));
        String id = RedasString.bytesToHex(hash);

        String jsonString = new JsonObject().toJson(recrPage);

        IndexRequest request = new IndexRequest(esConfig.getIndex())
                .id(id)
                .source(jsonString, XContentType.JSON);
        RestHighLevelClient client = esService.getClient();
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            log.debug(String.format("文档不存在, 创建新的文档, 文档id:%s", id));
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            log.debug(String.format("文档已存在, 更新文档, 文档id:%s", id));
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
            @RequestAttribute(name = "position") String position
    ) throws IOException {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("publishTime").gte(headDate).lte(endDate))
                .must(QueryBuilders.matchQuery("city", city).operator(Operator.fromString("AND")));

        BoolQueryBuilder positionBoolQueryBuilder = QueryBuilders.boolQuery();

        for (String item : position.split(",")) {
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

        SearchRequest searchRequest = new SearchRequest(esConfig.getIndex())
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
                                            @RequestAttribute(name = "position") String position
    ) throws IOException, ParseException {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("publishTime").gte(headDate).lte(endDate))
                .must(QueryBuilders.matchQuery("city", city).operator(Operator.fromString("AND")));

        BoolQueryBuilder positionBoolQueryBuilder = QueryBuilders.boolQuery();

        for (String item : position.split(",")) {
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

        SearchRequest searchRequest = new SearchRequest(esConfig.getIndex())
                .source(searchSourceBuilder);

        Aggregations aggs = esService.getClient()
                .search(searchRequest, RequestOptions.DEFAULT)
                .getAggregations();

        ParsedLongTerms count_position = aggs.get("count_position");
        ApiResponseList<Map<Object, Object>> responseList = new ApiResponseList<>(ResponseCode.SUCCESS, "获取成功");

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

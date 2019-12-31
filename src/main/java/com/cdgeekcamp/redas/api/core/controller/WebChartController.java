package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.PositionSalaryArray;
import com.cdgeekcamp.redas.db.model.KeyWords;
import com.cdgeekcamp.redas.db.model.KeyWordsRepository;
import com.cdgeekcamp.redas.db.model.PositionRepository;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseList;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseMap;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import com.cdgeekcamp.redas.lib.core.esConfig.EsApiCoreConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.pipeline.DerivativePipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.ParsedDerivative;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/webChart")
@ResponseBody
public class WebChartController {
    @Autowired
    private KeyWordsRepository keyWordsRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EsApiCoreConfig esApiCoreConfig;


    @GetMapping(value = "/getTerm")
    public ApiResponseMap<String, List<String>> getTerm(){
        try{
            ArrayList<String> position = new ArrayList<>();
            ArrayList<String> edu = new ArrayList<>();
            ArrayList<String> exp = new ArrayList<>();
            List<String> city;

            List<KeyWords> keyWords = keyWordsRepository.findByLevel(4);
            for (KeyWords keyWord: keyWords){
                position.add(keyWord.getKeyName());
            }
            LinkedHashSet<String> set = new LinkedHashSet<String>(position.size());
            set.addAll(position);
            position.clear();
            position.addAll(set);

            edu.add("中专");
            edu.add("大专");
            edu.add("本科");
            edu.add("硕士");
            edu.add("博士");
            edu.add("不限");

            exp.add("应届");
            exp.add("1年");
            exp.add("2年");
            exp.add("3年");
            exp.add("4年");
            exp.add("5年");
            exp.add("不限");

            city = positionRepository.findGroupByCity();

            ApiResponseMap<String, List<String>> apiResponseMap = new ApiResponseMap<>(ResponseCode.SUCCESS, "成功");
            apiResponseMap.addKeyAndValue("position", position);
            apiResponseMap.addKeyAndValue("edu", edu);
            apiResponseMap.addKeyAndValue("exp", exp);
            apiResponseMap.addKeyAndValue("city", city);
            return apiResponseMap;
        }catch (Exception e){
            return new ApiResponseMap<>(ResponseCode.FAILED, "出错啦");
        }
    }

    // 净增长
    @GetMapping(value = "/jobTimeNetIncrement")
    public ApiResponseList<Map<String, Object>> jobTimeNetIncrement(@RequestParam("position") String position,
                                                                    @RequestParam("edu") String edu,
                                                                    @RequestParam("exp") String exp,
                                                                    @RequestParam("city") String city,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate) throws IOException {
        try{

            RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                            new HttpHost(esApiCoreConfig.getHost(), esApiCoreConfig.getPort(), esApiCoreConfig.getScheme())));

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("position", position);
            paramMap.put("city", city);

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must().add(QueryBuilders.rangeQuery("publishTime").gte(startDate).lte(endDate));
            for (Map.Entry<String, String> param : paramMap.entrySet()){
                boolQueryBuilder.must().add(QueryBuilders.matchQuery(param.getKey(), param.getValue()));
            }

            // 创建builder
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.aggregation(AggregationBuilders.dateHistogram("all_publishTime")
                    .field("publishTime")
                    .format("yyyy-MM-dd")
                    .calendarInterval(DateHistogramInterval.DAY));
            searchSourceBuilder.size(0);
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            // 创建SearchRequest请求
            SearchRequest searchRequest = new SearchRequest(esApiCoreConfig.getIndex());
            searchRequest.source(searchSourceBuilder);

            // 发送请求， 获取结果
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            ApiResponseList<Map<String, Object>> apiResponseList = new ApiResponseList<>(ResponseCode.SUCCESS, "成功");
            ParsedDateHistogram aggregations = searchResponse.getAggregations().get("all_publishTime");

            for (Histogram.Bucket entry : aggregations.getBuckets()) {
                // 统一时间戳
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                long time = simpleDateFormat.parse(entry.getKeyAsString()).getTime();

                // result
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("x", time);
                resultMap.put("y", entry.getDocCount());

                apiResponseList.addValue(resultMap);
            }

            return apiResponseList;
        }catch (IOException | ParseException e){
            return new ApiResponseList<>(ResponseCode.FAILED, "出错啦");
        }
    }

    // 环比增长
    @GetMapping(value = "/jobTimeMoMIncrement")
    public ApiResponseList<Map<String, Object>> jobTimeMoMIncrement(@RequestParam("position") String position,
                                                                    @RequestParam("edu") String edu,
                                                                    @RequestParam("exp") String exp,
                                                                    @RequestParam("city") String city,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate) throws IOException {
        try{

            RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                    new HttpHost(esApiCoreConfig.getHost(), esApiCoreConfig.getPort(), esApiCoreConfig.getScheme())));

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("position", position);
            paramMap.put("city", city);

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must().add(QueryBuilders.rangeQuery("publishTime").gte(startDate).lte(endDate));
            for (Map.Entry<String, String> param : paramMap.entrySet()){
                boolQueryBuilder.must().add(QueryBuilders.matchQuery(param.getKey(), param.getValue()));
            }

            // 创建builder
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQueryBuilder);

            searchSourceBuilder.aggregation(AggregationBuilders.dateHistogram("all_publishTime")
                    .field("publishTime")
                    .format("yyyy-MM-dd")
                    .calendarInterval(DateHistogramInterval.DAY)
                    .subAggregation(AggregationBuilders.count("position_count")
                            .script(new Script("doc['publishTime'].value")))
                    .subAggregation(new DerivativePipelineAggregationBuilder("derivative_count", "position_count")));
            searchSourceBuilder.size(0);
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            // 创建SearchRequest请求
            SearchRequest searchRequest = new SearchRequest(esApiCoreConfig.getIndex());
            searchRequest.source(searchSourceBuilder);

            // 发送请求， 获取结果
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            ApiResponseList<Map<String, Object>> apiResponseList = new ApiResponseList<>(ResponseCode.SUCCESS, "成功");
            ParsedDateHistogram aggregations = searchResponse.getAggregations().get("all_publishTime");

            List<? extends Histogram.Bucket> entry = aggregations.getBuckets();
            for (int i = 0;i<entry.size();i++) {
                // 统一时间戳
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                long time = simpleDateFormat.parse(entry.get(i).getKeyAsString()).getTime();

                // result
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("x", time);

                ParsedDerivative parsedDerivative = entry.get(i).getAggregations().get("derivative_count");
                if (parsedDerivative == null){
                    resultMap.put("y", 0);
                }else if(Double.isNaN(parsedDerivative.value())){
                    resultMap.put("y", entry.get(i).getDocCount()-entry.get(i-1).getDocCount());
                }else{
                    resultMap.put("y", parsedDerivative.value());
                }

                apiResponseList.addValue(resultMap);
            }

            return apiResponseList;
        }catch (IOException | ParseException e){
            return new ApiResponseList<>(ResponseCode.FAILED, "出错啦");
        }
    }

    @GetMapping(value = "/positionSalaryChart")
    public ApiResponseList<Map<String, Object>> positionSalaryChart(@RequestParam("position") String position,
                                                                    @RequestParam("edu") String edu,
                                                                    @RequestParam("exp") String exp,
                                                                    @RequestParam("city") String city,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate) throws IOException {
        try{

            RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                    new HttpHost(esApiCoreConfig.getHost(), esApiCoreConfig.getPort(), esApiCoreConfig.getScheme())));

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("position", position);
            paramMap.put("city", city);

            // 查询bool
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must().add(QueryBuilders.rangeQuery("publishTime").gte(startDate).lte(endDate));
            for (Map.Entry<String, String> param : paramMap.entrySet()){
                boolQueryBuilder.must().add(QueryBuilders.matchQuery(param.getKey(), param.getValue()));
            }

            // 去掉salaryMax,salaryMin都为0的数据
            BoolQueryBuilder boolQueryBuildermust = QueryBuilders.boolQuery();
            boolQueryBuildermust.must().add(QueryBuilders.matchQuery("salaryMax", 0));
            boolQueryBuildermust.must().add(QueryBuilders.matchQuery("salaryMin", 0));
            boolQueryBuilder.mustNot().add(boolQueryBuildermust);


            // 创建builder
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQueryBuilder);

            PositionSalaryArray positionSalaryArray = new PositionSalaryArray();
            //聚合bool
            // *k-*k
            for (int[] param : positionSalaryArray.getInterval()){
                BoolQueryBuilder boolQueryBuilderAggsInterval = QueryBuilders.boolQuery();
                BoolQueryBuilder boolQueryBuilderAggsShould = QueryBuilders.boolQuery();
                BoolQueryBuilder boolQueryBuilderAggsShouldBoolInterval = QueryBuilders.boolQuery();

                boolQueryBuilderAggsShouldBoolInterval.mustNot(QueryBuilders.matchQuery("salaryMax", 0));
                boolQueryBuilderAggsShouldBoolInterval.must(QueryBuilders.rangeQuery("salaryMax").lt(param[0]));

                boolQueryBuilderAggsShould.should(boolQueryBuilderAggsShouldBoolInterval);
                boolQueryBuilderAggsShould.should(QueryBuilders.rangeQuery("salaryMin").gt(param[1]));

                boolQueryBuilderAggsInterval.mustNot(boolQueryBuilderAggsShould);

                searchSourceBuilder.aggregation(AggregationBuilders.filter(positionSalaryArray.intervalString(param), boolQueryBuilderAggsInterval));
            }


            // *k以上
            for (int[] param : positionSalaryArray.getAbove()){
                BoolQueryBuilder boolQueryBuilderAggsAbove = QueryBuilders.boolQuery();
                BoolQueryBuilder boolQueryBuilderAggsShouldBoolAbove = QueryBuilders.boolQuery();

                boolQueryBuilderAggsShouldBoolAbove.mustNot(QueryBuilders.matchQuery("salaryMax", 0));
                boolQueryBuilderAggsShouldBoolAbove.must(QueryBuilders.rangeQuery("salaryMax").lt(param[0]));

                boolQueryBuilderAggsAbove.mustNot(QueryBuilders.boolQuery().should(boolQueryBuilderAggsShouldBoolAbove));

                searchSourceBuilder.aggregation(AggregationBuilders.filter(positionSalaryArray.aboveString(param), boolQueryBuilderAggsAbove));
            }

            searchSourceBuilder.size(0);
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            // 创建SearchRequest请求
            SearchRequest searchRequest = new SearchRequest(esApiCoreConfig.getIndex());
            searchRequest.source(searchSourceBuilder);

            // 发送请求， 获取结果
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            ApiResponseList<Map<String, Object>> apiResponseList = new ApiResponseList<>(ResponseCode.SUCCESS, "成功");
            for (int[] param : positionSalaryArray.getInterval()){
                ParsedFilter aggregations = searchResponse.getAggregations().get(positionSalaryArray.intervalString(param));

                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("x", positionSalaryArray.intervalString(param));
                resultMap.put("y", aggregations.getDocCount());

                apiResponseList.addValue(resultMap);
            }

            for (int[] param : positionSalaryArray.getAbove()){
                ParsedFilter aggregations = searchResponse.getAggregations().get(positionSalaryArray.aboveString(param));

                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("x", positionSalaryArray.aboveString(param));
                resultMap.put("y", aggregations.getDocCount());

                apiResponseList.addValue(resultMap);
            }

            return apiResponseList;
        }catch (IOException e){
            return new ApiResponseList<>(ResponseCode.FAILED, "出错啦");
        }
    }
}

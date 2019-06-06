package com.example.house.es;

import com.example.house.HouseApplicationTests;
import com.example.house.es.template.HouseIndexTemplate;
import com.example.house.es.template.Location;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class SearchServiceTest extends HouseApplicationTests {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SearchService searchService;

    @Test
    public void analyze() {
        AnalyzeRequestBuilder analyzeRequestBuilder = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient()
                , AnalyzeAction.INSTANCE, "house", "我是中国人","我是美国人","这个是什么东西");
        analyzeRequestBuilder.setAnalyzer("ik_smart");
        AnalyzeResponse analyzeTokens = analyzeRequestBuilder.execute().actionGet();
        List<AnalyzeResponse.AnalyzeToken> tokens = analyzeTokens.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            log.info("[分词结果]:{}",token.getTerm());
        }
        Assert.assertNotNull(tokens);
    }

    @Test
    public void updateSuggest() {


        HouseIndexTemplate houseIndex = HouseIndexTemplate.HouseIndexTemplateBuilder
                .aHouseIndexTemplate()
                .area(10)
                .cityEnName("广州")
                .createTime(LocalDate.now())
                .description("这个房自很棒")
                .direction(1)
                .distanceToSubWay(100)
                .district("龙洞街道")
                .layoutDesc("这个房间很大")
                .houseId("1000")
                .price(100)
                .location(new Location(10000, 100000))
                .regionEnName("天河区")
                .subWayName("地铁名称")
                .title("这个房自很棒")
                .traffic("交通状况")
                .roundService("附近的服务")
                .subwayStationName("地铁站的名称")
                .build();

        boolean b = searchService.updateSuggest(houseIndex);
        log.info("[更新提示字段后 indexTemplate.suggests的值]：{}", houseIndex.getSuggests().toString());
        HouseIndexTemplate houseIndex1 = HouseIndexTemplate.HouseIndexTemplateBuilder
                .aHouseIndexTemplate()
                .area(10)
                .cityEnName("广州")
                .createTime(LocalDate.now())
                .description("这个房自很棒")
                .direction(1)
                .distanceToSubWay(100)
                .district("龙洞街道")
                .layoutDesc("这个房间很大")
                .houseId("1000")
                .price(100)
                .location(new Location(10000, 100000))
                .regionEnName("天河区")
                .subWayName("地铁名称")
                .title("这个房自很棒")
                .traffic("交通状况")
                .roundService("附近的服务")
                .subwayStationName(null)
                .build();

        boolean b1 = searchService.updateSuggest(houseIndex1);
        log.info("[更新提示字段后 indexTemplate.suggests的值]：{}", houseIndex1.getSuggests().toString());
    }

    @Test
    public void suggest() {
        List<String> suggest = searchService.suggest("房");
        log.info("[searchService.suggest返回值为]：{}",suggest.toString());
    }
}
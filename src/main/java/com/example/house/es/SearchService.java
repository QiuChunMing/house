package com.example.house.es;

import com.example.house.domain.House;
import com.example.house.domain.HouseDetail;
import com.example.house.domain.HouseTag;
import com.example.house.domain.SupportAddress;
import com.example.house.enmu.HouseSort;
import com.example.house.es.repository.EsHouseRepository;
import com.example.house.es.template.HouseIndexMessage;
import com.example.house.es.template.HouseIndexTemplate;
import com.example.house.es.template.HouseSuggest;
import com.example.house.es.template.Location;
import com.example.house.exception.AddressException;
import com.example.house.form.MapQuery;
import com.example.house.form.SearchForm;
import com.example.house.repository.HouseDetailRepository;
import com.example.house.repository.HouseRepository;
import com.example.house.repository.HouseTagRepository;
import com.example.house.repository.SupportAddressRepository;
import com.example.house.service.AddressService;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService implements ISearchService {

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EsHouseRepository esHouseRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private AddressService addressService;

    public static final String INDEX_NAME = "house";

    /**
     * 根据message操作es
     * @param message
     */
    private void createOrUpdateIndex(HouseIndexMessage message) {
        String houseId = message.getHouseId();
        House house = houseRepository.findById(houseId).orElse(null);
        if (house == null) {
            return;
        }
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);

        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId);
        if (houseDetail == null) {
            return;
        }

        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(house.getCityEnName(), SupportAddress.Level.CITY.getValue()).orElse(null);
        SupportAddress region = supportAddressRepository.findByEnNameAndLevel(house.getRegionEnName(), SupportAddress.Level.REGION.getValue()).orElse(null);
        if (city == null || region == null) {
            throw new AddressException(String.format("[SearchSevice.createOrUpdate]：id :%s", message.getHouseId()));
        }
        String address = city.getCnName() + region.getCnName()
                + house.getStreet() + house.getDistrict() + houseDetail.getDetailAddress();

        //setLocation 用address换location
        Location location = addressService.getLocation(city.getCnName(), address);
        houseIndexTemplate.setLocation(location);

        List<HouseTag> tags = houseTagRepository.findAllByHouseId(house.getId());
        if (tags != null && !tags.isEmpty()) {
            List<String> newTags = new ArrayList<>();
            tags.forEach(tag -> {
                newTags.add(tag.getContent());
            });
            houseIndexTemplate.setTags(newTags);
        }
        createOrUpdate(houseIndexTemplate);
    }

    private void removeIndex(HouseIndexMessage message) {
        if (StringUtils.isEmpty(message.getHouseId())) {
            return;
        }
        delete(message.getHouseId());
    }

    public List<String> query(SearchForm searchForm) {
        Pageable pageable = PageRequest.of(searchForm.getPage(), searchForm.getSize());
        log.debug("[searchService.query] 查询关键词为:{}", searchForm.getKeyWord());
        log.debug("[searchService.query] 详细查询表单为：{}", searchForm.toString());
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(searchForm.getCityEnName())) {
            boolQuery.must(QueryBuilders.termQuery(HouseIndexKeyConstant.CITY_EN_NAME, searchForm.getCityEnName()));
        }
        if (!StringUtils.isEmpty(searchForm.getRegionEnName())) {
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexKeyConstant.REGION_EN_NAME, searchForm.getRegionEnName()));
        }
        //面积查询过滤
        RentValueBlock areaBlock = RentValueBlock.getAreaBlockFromString(searchForm.getAreaBlock());
        if (!RentValueBlock.withoutLimit.equals(areaBlock)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKeyConstant.AREA);
            if (areaBlock.getMin() != -1) {
                rangeQueryBuilder.gte(areaBlock.getMin());
            }
            if (areaBlock.getMax() != -1) {
                rangeQueryBuilder.lte(areaBlock.getMax());
            }
            boolQuery.filter(rangeQueryBuilder);
        }
        //价格查询过滤
        RentValueBlock priceBlock = RentValueBlock.getPriceBlockFromString(searchForm.getPriceBlock());
        if (!RentValueBlock.withoutLimit.equals(priceBlock)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKeyConstant.PRICE);
            if (priceBlock.getMax() != -1) {
                rangeQueryBuilder.gte(priceBlock.getMin());
            }

            if (priceBlock.getMax() != -1) {
                rangeQueryBuilder.lte(priceBlock.getMax());
            }

            boolQuery.filter(rangeQueryBuilder);
        }

        //房屋方向
        if (searchForm.getDirection() != null && searchForm.getDirection() > -1) {
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexKeyConstant.DIRECTION, searchForm.getDirection()));
        }
        //租住方式
        if (searchForm.getRentWay() != null && searchForm.getRentWay() > -1) {
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexKeyConstant.RENT_WAY, searchForm.getRentWay()));
        }

        boolQuery.must(QueryBuilders.multiMatchQuery(searchForm.getKeyWord()
                , HouseIndexKeyConstant.TITLE
                , HouseIndexKeyConstant.TRAFFIC
                , HouseIndexKeyConstant.DISTRICT
                , HouseIndexKeyConstant.ROUND_SERVICE
                , HouseIndexKeyConstant.SUBWAY_LINE
                , HouseIndexKeyConstant.SUBWAY_STATION
        ));
        Page<HouseIndexTemplate> searches = esHouseRepository.search(boolQuery, pageable);
        log.debug("es查询出{}个结果", searches.getTotalElements());
        List<String> ids = new ArrayList<>();
        searches.forEach(search -> {
            ids.add(search.getHouseId());
        });
        return ids;
    }

    private void createOrUpdate(HouseIndexTemplate template) {
        if (!updateSuggest(template)) {
            return;
        }
        esHouseRepository.save(template);
    }

    private void delete(String houseId) {
        esHouseRepository.deleteById(houseId);
    }

    @Override
    public void index(String houseId) {
        HouseIndexMessage message = new HouseIndexMessage();
        message.setHouseId(houseId);
        createOrUpdateIndex(message);
    }

    @Override
    public void remove(String houseId) {
        HouseIndexMessage houseIndexMessage = new HouseIndexMessage();
        houseIndexMessage.setHouseId(houseId);
        removeIndex(houseIndexMessage);
    }

    @Override
    public List<String> suggest(String prefix) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders
                .completionSuggestion(HouseIndexKeyConstant.SUGGEST)
                .prefix(prefix)
                .size(5);

        SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("my-suggest", suggestion);

        Suggest suggest = elasticsearchTemplate.suggest(suggestBuilder, HouseIndexTemplate.class).getSuggest();
        List<String> suggestList = new ArrayList<>();

        if (suggest != null && suggest.getSuggestion("my-suggest") != null && suggest.getSuggestion("my-suggest").getSize() > 0) {
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> completeSuggest
                    = suggest.getSuggestion("my-suggest");
            List<? extends Suggest.Suggestion.Entry.Option> options = completeSuggest.getEntries().get(0).getOptions();
            options.forEach(o -> {
                suggestList.add(o.getText().toString());
            });
        }
        return suggestList;
    }

    public boolean updateSuggest(HouseIndexTemplate template) {
        //健壮性判断,免去空指针异常
        String string = Joiner.on(",").skipNulls().join(
                template.getTitle(), template.getDescription()
                , template.getLayoutDesc(), template.getRoundService()
                , template.getSubWayName(), template.getSubwayStationName()
        );
        log.info("[template.getTitle()]:{}", template.getTitle());
        log.info("[template.getDescription()]:{}", template.getDescription());
        log.info("[template.getLayoutDesc()]:{}", template.getLayoutDesc());
        log.info("[template.getRoundService()]:{}", template.getRoundService());
        log.info("[template.getSubWayName()]:{}", template.getSubWayName());
        log.info("[template.getSubwayStationName()]:{}", template.getSubwayStationName());
        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient()
                , AnalyzeAction.INSTANCE, INDEX_NAME
                , template.getTitle(),
                string
        );
        requestBuilder.setAnalyzer("ik_smart");
        List<AnalyzeResponse.AnalyzeToken> tokens = requestBuilder.execute().actionGet().getTokens();

        if (tokens == null || tokens.isEmpty()) {
            return false;
        }
        List<HouseSuggest> houseSuggests = new ArrayList<>();
        tokens.forEach(token -> {
            log.info("[updateSuggest分词结果为]：{},类型为：{}", token.getTerm(), token.getType());
            if ("<NUM>".equals(token.getType())) {
                return;
            }
            HouseSuggest houseSuggest = new HouseSuggest();
            houseSuggest.setInput(token.getTerm());
            houseSuggests.add(houseSuggest);
        });


        HouseSuggest houseSuggest = new HouseSuggest();
        houseSuggest.setInput(template.getDistrict());
        houseSuggests.add(houseSuggest);

        template.setSuggests(houseSuggests);
        return true;
    }



    @Override
    public List<String> mapQuery(String cityName, String orderBy, String orderDirection, int page, int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(HouseIndexKeyConstant.CITY_EN_NAME, cityName));
        Sort sort = HouseSort.generateSort(orderBy, orderDirection);
        Pageable pageable = PageRequest.of(page, size, sort);
        Iterable<HouseIndexTemplate> houses = esHouseRepository.search(boolQuery, pageable);
        List<String> ids = new ArrayList<>();
        houses.forEach(house -> {
            ids.add(house.getHouseId());
        });

        return ids;
    }

    @Override
    public List<String> mapQuery(MapQuery mapQuery) {
        Sort sort = HouseSort.generateSort(mapQuery.getOrderBy(), mapQuery.getOrderDirection());
        Pageable pageable = PageRequest.of(mapQuery.getPage(), mapQuery.getSize(), sort);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKeyConstant.CITY_EN_NAME, mapQuery.getCityEnName()));
        boolQuery.filter(QueryBuilders.geoBoundingBoxQuery("location").setCorners(
                new GeoPoint(mapQuery.getLeftLatitude(), mapQuery.getLeftLongitude()),
                new GeoPoint(mapQuery.getRightLatitude(), mapQuery.getRightLongitude())
        ));
        Iterable<HouseIndexTemplate> houses = esHouseRepository.search(boolQuery, pageable);
        List<String> ids = new ArrayList<>();
        houses.forEach(
                house -> {
                    ids.add(house.getHouseId());
                }
        );
        return ids;
    }

}

package com.example.house.es.template;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;
@Data
@Document(indexName = "house",type = "house",replicas = 1)
public class HouseIndexTemplate {
    @Id
    private String id;

    private String houseId;
    private String title;
    private int price;
    private int area;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate updateTime;
    private String cityEnName;
    private String regionEnName;
    private int direction;
    private int distanceToSubWay;
    private String subWayName;
    private String subwayStationName;
    private String street;
    private String district;
    private String description;
    private String layoutDesc;
    private String traffic;
    private String roundService;
    private int rentWay;
    private List<String> tags;
    private List<HouseSuggest> suggests;
    private Location location;

    public static final class HouseIndexTemplateBuilder {
        private HouseIndexTemplate houseIndexTemplate;

        private HouseIndexTemplateBuilder() {
            houseIndexTemplate = new HouseIndexTemplate();
        }

        public static HouseIndexTemplateBuilder aHouseIndexTemplate() {
            return new HouseIndexTemplateBuilder();
        }

        public HouseIndexTemplateBuilder id(String id) {
            houseIndexTemplate.setId(id);
            return this;
        }

        public HouseIndexTemplateBuilder houseId(String houseId) {
            houseIndexTemplate.setHouseId(houseId);
            return this;
        }

        public HouseIndexTemplateBuilder title(String title) {
            houseIndexTemplate.setTitle(title);
            return this;
        }

        public HouseIndexTemplateBuilder price(int price) {
            houseIndexTemplate.setPrice(price);
            return this;
        }

        public HouseIndexTemplateBuilder area(int area) {
            houseIndexTemplate.setArea(area);
            return this;
        }

        public HouseIndexTemplateBuilder createTime(LocalDate createTime) {
            houseIndexTemplate.setCreateTime(createTime);
            return this;
        }

        public HouseIndexTemplateBuilder updateTime(LocalDate updateTime) {
            houseIndexTemplate.setUpdateTime(updateTime);
            return this;
        }

        public HouseIndexTemplateBuilder cityEnName(String cityEnName) {
            houseIndexTemplate.setCityEnName(cityEnName);
            return this;
        }

        public HouseIndexTemplateBuilder regionEnName(String regionEnName) {
            houseIndexTemplate.setRegionEnName(regionEnName);
            return this;
        }

        public HouseIndexTemplateBuilder direction(int direction) {
            houseIndexTemplate.setDirection(direction);
            return this;
        }

        public HouseIndexTemplateBuilder distanceToSubWay(int distanceToSubWay) {
            houseIndexTemplate.setDistanceToSubWay(distanceToSubWay);
            return this;
        }

        public HouseIndexTemplateBuilder subWayName(String subWayName) {
            houseIndexTemplate.setSubWayName(subWayName);
            return this;
        }

        public HouseIndexTemplateBuilder subwayStationName(String subwayStationName) {
            houseIndexTemplate.setSubwayStationName(subwayStationName);
            return this;
        }

        public HouseIndexTemplateBuilder street(String street) {
            houseIndexTemplate.setStreet(street);
            return this;
        }

        public HouseIndexTemplateBuilder district(String district) {
            houseIndexTemplate.setDistrict(district);
            return this;
        }

        public HouseIndexTemplateBuilder description(String description) {
            houseIndexTemplate.setDescription(description);
            return this;
        }

        public HouseIndexTemplateBuilder layoutDesc(String layoutDesc) {
            houseIndexTemplate.setLayoutDesc(layoutDesc);
            return this;
        }

        public HouseIndexTemplateBuilder traffic(String traffic) {
            houseIndexTemplate.setTraffic(traffic);
            return this;
        }

        public HouseIndexTemplateBuilder roundService(String roundService) {
            houseIndexTemplate.setRoundService(roundService);
            return this;
        }

        public HouseIndexTemplateBuilder rentWay(int rentWay) {
            houseIndexTemplate.setRentWay(rentWay);
            return this;
        }

        public HouseIndexTemplateBuilder tags(List<String> tags) {
            houseIndexTemplate.setTags(tags);
            return this;
        }

        public HouseIndexTemplateBuilder suggests(List<HouseSuggest> suggests) {
            houseIndexTemplate.setSuggests(suggests);
            return this;
        }

        public HouseIndexTemplateBuilder location(Location location) {
            houseIndexTemplate.setLocation(location);
            return this;
        }

        public HouseIndexTemplate build() {
            return houseIndexTemplate;
        }
    }
}

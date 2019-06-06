package com.example.house.form;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class HouseForm {
    private String id;

    private Integer bathroom;

    @NotNull(message = "标题不能为空")
    @Size(min = 1,max = 30,message = "长度在1~30之间")
    private String title;

    @NotNull(message = "城市不能为空")
    @Size(min = 1,message = "无效的城市")
    private String cityEnName;

    @NotNull(message = "地区不能为空")
    @Size(min = 1,message = "无效的地区")
    private String regionEnName;

    @NotNull
    @Size(min = 1,message = "非法的街道")
    private String street;

    private String district;

    @NotNull(message = "详细地址不能为空")
    @Size(min = 1,message = "无效的地址")
    private String detailAddress;

    @NotNull(message = "必须填入地址信息")
    @Min(value = 1,message = "非法的房屋数量")
    private Integer room;

    private Integer parlour;

    @NotNull(message = "必须填入所在楼层")
    private Integer floor;

    @NotNull(message = "必须填入总楼层")
    @Min(value = 1,message = "总楼层数非法")
    private Integer totalFloor;

    @NotNull(message = "必须填入房屋朝向")
    private Integer direction;

    @NotNull(message = "必须填入每月租金")
    @Min(value = 0,message = "非法的租金")
    private Integer price;

    @NotNull(message = "必须填入建筑初始时间")
    @Min(value = 1900,message = "非法的初始建筑时间")
    private Integer builderYear;

    @NotNull(message = "必须填入租赁方式")
    @Min(value = 0,message = "非法的租赁方式")
    @Max(value = 1,message = "非法的租赁方式")
    private Integer rentWay;

    @NotNull(message = "必须填入房屋面积")
    @Min(value = 1,message = "非法的房屋面积")
    private Integer area;

    private String subwayLineId;

    private String subwayStationId;

    private Integer distanceToSubway;

    private String layoutDesc;

    private String traffic;

    @Size(max = 255)
    private String description;

    private String cover;

    private List<String> tags;

    private List<PhotoForm> photos;

    private String roundService;

    public static final class HouseFormBuilder {
        private HouseForm houseForm;

        private HouseFormBuilder() {
            houseForm = new HouseForm();
        }

        public static HouseFormBuilder aHouseForm() {
            return new HouseFormBuilder();
        }

        public HouseFormBuilder id(String id) {
            houseForm.setId(id);
            return this;
        }

        public HouseFormBuilder title(String title) {
            houseForm.setTitle(title);
            return this;
        }

        public HouseFormBuilder cityEnName(String cityEnName) {
            houseForm.setCityEnName(cityEnName);
            return this;
        }

        public HouseFormBuilder regionEnName(String regionEnName) {
            houseForm.setRegionEnName(regionEnName);
            return this;
        }

        public HouseFormBuilder street(String street) {
            houseForm.setStreet(street);
            return this;
        }

        public HouseFormBuilder district(String district) {
            houseForm.setDistrict(district);
            return this;
        }

        public HouseFormBuilder detailAddress(String detailAddress) {
            houseForm.setDetailAddress(detailAddress);
            return this;
        }

        public HouseFormBuilder room(Integer room) {
            houseForm.setRoom(room);
            return this;
        }

        public HouseFormBuilder parlour(Integer parlour) {
            houseForm.setParlour(parlour);
            return this;
        }

        public HouseFormBuilder floor(Integer floor) {
            houseForm.setFloor(floor);
            return this;
        }

        public HouseFormBuilder totalFloor(Integer totalFloor) {
            houseForm.setTotalFloor(totalFloor);
            return this;
        }

        public HouseFormBuilder direction(Integer direction) {
            houseForm.setDirection(direction);
            return this;
        }

        public HouseFormBuilder price(Integer price) {
            houseForm.setPrice(price);
            return this;
        }

        public HouseFormBuilder builderYear(Integer builderYear) {
            houseForm.setBuilderYear(builderYear);
            return this;
        }

        public HouseFormBuilder rentWay(Integer rentWay) {
            houseForm.setRentWay(rentWay);
            return this;
        }

        public HouseFormBuilder area(Integer area) {
            houseForm.setArea(area);
            return this;
        }

        public HouseFormBuilder subwayLineId(String subwayLineId) {
            houseForm.setSubwayLineId(subwayLineId);
            return this;
        }

        public HouseFormBuilder subwayStationId(String subwayStationId) {
            houseForm.setSubwayStationId(subwayStationId);
            return this;
        }

        public HouseFormBuilder distanceToSubway(Integer distanceToSubway) {
            houseForm.setDistanceToSubway(distanceToSubway);
            return this;
        }

        public HouseFormBuilder layoutDesc(String layoutDesc) {
            houseForm.setLayoutDesc(layoutDesc);
            return this;
        }

        public HouseFormBuilder traffic(String traffic) {
            houseForm.setTraffic(traffic);
            return this;
        }

        public HouseFormBuilder description(String description) {
            houseForm.setDescription(description);
            return this;
        }

        public HouseFormBuilder cover(String cover) {
            houseForm.setCover(cover);
            return this;
        }

        public HouseFormBuilder tags(List<String> tags) {
            houseForm.setTags(tags);
            return this;
        }

        public HouseFormBuilder photos(List<PhotoForm> photos) {
            houseForm.setPhotos(photos);
            return this;
        }

        public HouseFormBuilder roundService(String roundService) {
            houseForm.setRoundService(roundService);
            return this;
        }

        public HouseForm build() {
            return houseForm;
        }
    }
}

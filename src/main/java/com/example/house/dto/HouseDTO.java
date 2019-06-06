package com.example.house.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class HouseDTO {
    private String id;
    private String title;
    private Integer price;
    private Integer direction;
    private Integer room;
    private Integer area;
    private Integer parlour;
    private Integer bathroom;
    private String userId;
    private String district;
    private Integer totalFloor;
    private Integer watchTimes;
    private Integer buildYear;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate LastUpdateTime;
    private String cityEnName;
    private String street;
    private String cover;
    private Integer distanceToSubway;
    private List<String> tags = new ArrayList<>();
    private List<HousePictureDTO> pictures;
    private HouseDetailDTO houseDetail;
    private Integer subscribeStatus;
}

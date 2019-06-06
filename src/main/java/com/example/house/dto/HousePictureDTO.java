package com.example.house.dto;

import lombok.Data;

@Data
public class HousePictureDTO {
    private String id;
    private String houseId;
    private String cdnPrefix;
    private String path;
    private Integer width;
    private Integer height;
}

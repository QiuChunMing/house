package com.example.house.form;

import lombok.Data;

@Data
public class MapQuery {
    private int level = 12;

    private String cityEnName;
    private String orderBy = "updateTime";
    private String orderDirection = "desc";

    private double leftLongitude;
    private double leftLatitude;

    private double rightLongitude;
    private double rightLatitude;

    int page = 0;
    int size = 5;
}

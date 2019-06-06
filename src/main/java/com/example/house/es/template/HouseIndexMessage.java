package com.example.house.es.template;

import lombok.Data;

@Data
public class HouseIndexMessage {
    public static final String INDEX = "index";
    public static final String REMOVE = "remove";

    private static final int MAX_RETRY = 3;

    private String houseId;
    private int reTry = 0;
    private String operation;
}

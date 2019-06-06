package com.example.house.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class SearchForm {
    private String cityEnName = "";
    private String regionEnName ="";
    private String priceBlock;
    private String areaBlock;
    private Integer room = -1;
    private Integer direction = -1;
    private String keyWord;
    private Integer rentWay = -1;
    private String orderBy = "UpdateTime";
    private String orderDirection = "desc";

    private int page = 0;
    private int size = 10;
}

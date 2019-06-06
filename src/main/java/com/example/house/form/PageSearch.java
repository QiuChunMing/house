package com.example.house.form;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class PageSearch {
    //每页数量
    private Integer size;
    //当前第几页
    private Integer page;
    private String direction;
    private String orderBy;
    private String city;
    private String title;
    private LocalDate createTimeMin;
    private LocalDate createTimeMax;
    private Integer status;

}

package com.example.house.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HouseSubscribeDTO {
    private String id;
    private String houseId;
    //预约用户id
    private String userId;
    //房主id，如果是房屋是交由中介出租的，ownnerid为管理员id
    private String ownerId;
    private Integer status;
    private LocalDate createTime;
    private LocalDate updateTime;
    private String phoneNumber;
    private LocalDate orderTime;
    private String content;
}

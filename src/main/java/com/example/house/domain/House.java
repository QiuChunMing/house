package com.example.house.domain;

import com.example.house.enmu.HouseStatus;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@Data
@Entity
public class House extends BaseDomain{

    private String title;

    private String ownerId;

    private Integer price;

    private Integer area;
    //房间
    private Integer room;
    //客厅
    private Integer parlour;

    private Integer bathroom;

    private Integer floor;

    private Integer totalFloor;

    private Integer watchTimes;

    private Integer buildYear;

    private Integer status = HouseStatus.NOT_AUDITED.getStatus();

    private String cityEnName;

    private String regionEnName;

    private String street;

    private String district;

    private String cover;

    private Integer direction;

    private Integer distanceToSubway;

    private String auditingUserId;

}

package com.example.house.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class HouseDetail {
    @Id
    @GeneratedValue(generator="system_uuid")
    @GenericGenerator(name="system_uuid",strategy="uuid")
    private String id;

    private String houseId;

    private String description;

    private String traffic;

    private String roundService;

    private String rentWay;

    private String detailAddress;

    private String subwayLineId;

    private String subwayStationId;

    private String subwayLineName;

    private String subwayStationName;

    private String layoutDesc;
}

package com.example.house.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class HousePicture {
    @Id
    @GeneratedValue(generator="system_uuid")

    @GenericGenerator(name="system_uuid",strategy="uuid")
    private String id;

    private String houseId;

    private String path;

    private String cdnPrefix;

    private Integer width;

    private Integer height;

    private String location;

}

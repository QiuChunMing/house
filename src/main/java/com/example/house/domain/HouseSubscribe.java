package com.example.house.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@Data
@Entity
public class HouseSubscribe extends BaseDomain{
    @Id
    @GeneratedValue(generator="system_uuid")
    @GenericGenerator(name="system_uuid",strategy="uuid")
    private String id;

    private String houseId;

    private String userId;

    private Integer status;

    private LocalDate orderTime;

    private String content;
    private String phoneNumber;
}

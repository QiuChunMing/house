package com.example.house.service;

import com.example.house.HouseApplicationTests;
import com.example.house.form.HouseForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class HouseServiceTest extends HouseApplicationTests {


    @Autowired
    private IHouseService houseService;

    @Test
    public void save() {
        HouseForm houseForm = HouseForm.HouseFormBuilder.aHouseForm()
                .area(1)
                .builderYear(1990)
                .cityEnName("广州市")
                .cover("")
                .description("")
                .build();
    }

    @Test
    public void total() {
        long total = houseService.total();
        log.info("[HouseService.total]:{}",total);
    }

    @Test
    public void update() {
    }

    @Test
    public void adminQuery() {
    }

    @Test
    public void findHouseWithDetail() {
    }

    @Test
    public void updateStatus() {
    }

    @Test
    public void addTag() {
    }

    @Test
    public void removeTag() {
    }

    @Test
    public void addSubscribeOrder() {
    }

    @Test
    public void subscribe() {
    }

    @Test
    public void cancelSubscribe() {
    }

    @Test
    public void finishSubscribe() {
    }

    @Test
    public void querySubscribeList() {
    }

    @Test
    public void query() {
    }
}
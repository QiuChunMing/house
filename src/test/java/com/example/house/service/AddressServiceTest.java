package com.example.house.service;

import com.example.house.HouseApplicationTests;
import com.example.house.es.template.Location;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class AddressServiceTest extends HouseApplicationTests {

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void getLocation() {
        Location location = addressService.getLocation("茂名市","茂名市信宜市人民北路365号");
        log.info("location is: {},{}", location.getLat(), location.getLng());
    }

    @Test
    public void buildUrl() {

    }
}
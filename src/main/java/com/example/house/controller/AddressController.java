package com.example.house.controller;

import com.example.house.base.ApiResponse;
import com.example.house.domain.SupportAddress;
import com.example.house.dto.SupportAddressDTO;
import com.example.house.service.IAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@Slf4j
public class AddressController {
    @Autowired
    private IAddressService addressService;

    @GetMapping("/api/address/cities")
    public ApiResponse getSupportCities() {
        List<SupportAddressDTO> cities = addressService.findAllCities();
        return ApiResponse.success(cities);

    }

    @GetMapping("/api/address/regions")
    public List<SupportAddressDTO> getSupportRegion(@RequestParam("cityName") String cityEnName) {
        return addressService.findAllRegionsByCityName(cityEnName);
    }

    @PostMapping("/api/address/city/add")
    public SupportAddress addCity(String cityName) {
       return addressService.addCity(cityName);
    }

    @PostMapping("/api/address/region/add")
    public void addCity(String cityName, String regionName) {
        log.info("cityName is {}, regionName is {}", cityName, regionName);
        addressService.addRegion(cityName, regionName);
    }


}

package com.example.house.controller;

import com.example.house.base.ApiResponse;
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
        if (cities != null) {
            return ApiResponse.success(cities);
        } else {
            return ApiResponse.fail("找不到支持的城市");
        }
    }

    @GetMapping("/api/address/regions")
    public ApiResponse getSupportRegion(@RequestParam("cityName") String cityEnName) {
        List<SupportAddressDTO> regions = addressService.findAllRegionsByCityName(cityEnName);
        return ApiResponse.success(regions);
    }

    @PostMapping("/api/address/city/add")
    public ApiResponse addCity(String cityName) {
        log.info("cityName is {}", cityName);
        addressService.addCity(cityName);
        return ApiResponse.success();
    }

    @PostMapping("/api/address/region/add")
    public ApiResponse addCity(String cityName, String regionName) {
        log.info("cityName is {}, regionName is {}", cityName, regionName);
        addressService.addRegion(cityName, regionName);
        return ApiResponse.success();
    }


}

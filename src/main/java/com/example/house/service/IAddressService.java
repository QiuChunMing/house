package com.example.house.service;

import com.example.house.domain.SupportAddress;
import com.example.house.dto.SubwayDTO;
import com.example.house.dto.SubwayStationDTO;
import com.example.house.dto.SupportAddressDTO;
import com.example.house.es.template.Location;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;

public interface IAddressService {
    List<SupportAddressDTO> findAllCities();

    Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    List<SupportAddressDTO> findAllRegionsByCityName(String cityName);

    List<SubwayDTO> findAllSubwaysByCity(String cityName);

    List<SubwayStationDTO> findAllStationBySubway(String subwayId);

    SubwayDTO findSubway(String subwayId);

    SubwayStationDTO findSubwayStation(String subwayStationId);

    SupportAddressDTO findCity(String cityName);

    Location getLocation(@Nullable String city, String address);

    void lbsUpload(Location location, String houseId, int price,
                   String area, String title, String address);

    boolean poiExist(String houseId);

    void poiRemove(String houseId);

    List<String> AddressSuggest(String region, String prefix);

    SupportAddress addCity(String cityName);

    void addRegion(String cityName, String regionName);

    void deleteCity(String cityName);
}

package com.example.house.repository;

import com.example.house.domain.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubwayStationRepository extends CrudRepository<SubwayStation,String> {
    List<SubwayStation> findAllBySubwayId(String subwayId);
}

package com.example.house.repository;

import com.example.house.domain.HouseDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface HouseDetailRepository extends CrudRepository<HouseDetail,String> {
    HouseDetail findByHouseId(String houseId);

    List<HouseDetail> findAllByHouseIdIn(List<String> houseId);
}

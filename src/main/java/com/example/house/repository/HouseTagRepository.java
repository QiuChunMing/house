package com.example.house.repository;

import com.example.house.domain.HouseTag;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface HouseTagRepository extends PagingAndSortingRepository<HouseTag,String> {
    List<HouseTag> findAllByHouseId(String houseId);

    HouseTag findByContentAndHouseId(String content,String houseId);

    List<HouseTag> findAllByHouseIdIsIn(List<String> houseId);
}

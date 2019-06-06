package com.example.house.repository;

import com.example.house.domain.HouseSubscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


import java.util.List;

public interface HouseSubscribeRepository extends PagingAndSortingRepository<HouseSubscribe,String> {
    HouseSubscribe findByHouseIdAndUserId(String houseId, String userId);

    List<HouseSubscribe> findAllByUserId(String userId);

    Page<HouseSubscribe> findAllByUserIdAndStatus(String userId, int status, Pageable pageable);

    HouseSubscribe findByHouseId(String houseId);
}

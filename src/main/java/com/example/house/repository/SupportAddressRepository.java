package com.example.house.repository;

import com.example.house.domain.SupportAddress;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface SupportAddressRepository extends PagingAndSortingRepository<SupportAddress,String> {
    List<SupportAddress> findAllByLevel(String level);


    Optional<SupportAddress> findByEnNameAndLevel(String cityEnName, String value);

    SupportAddress findByEnNameAndBelongTo(String regionEnName, String enName);

    List<SupportAddress> findAllByLevelAndBelongTo(String level, String belongTo);


}

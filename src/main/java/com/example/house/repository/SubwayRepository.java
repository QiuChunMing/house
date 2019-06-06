package com.example.house.repository;

import com.example.house.domain.Subway;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SubwayRepository extends PagingAndSortingRepository<Subway,String> {
    List<Subway> findAllByCityEnName(String cityEnName);

}

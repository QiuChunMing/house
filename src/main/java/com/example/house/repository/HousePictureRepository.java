package com.example.house.repository;

import com.example.house.domain.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HousePictureRepository extends CrudRepository<HousePicture,String> {
    List<HousePicture> findAllByHouseId(String houseId);
}

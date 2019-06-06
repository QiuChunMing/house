package com.example.house.service;

import com.example.house.dto.HouseDTO;
import com.example.house.dto.HouseSubscribeDTO;
import com.example.house.enmu.HouseSubscribeStatus;
import com.example.house.form.HouseForm;
import com.example.house.form.PageSearch;
import com.example.house.form.SearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface IHouseService {
    HouseDTO save(HouseForm houseForm);

    long total();

    void update(HouseForm houseForm);

    Page<HouseDTO> adminQuery(PageSearch pageSearch);

    HouseDTO findHouseWithDetail(String houseId);

    @Transactional
    void updateStatus(String houseId, Integer status);

    @Transactional
    void addTag(String houseId, String tag);

    void removeTag(String houseId, String tag);

    @Transactional
    void addSubscribeOrder(String houseId);

    void subscribe(String houseId, LocalDate orderTime, String phoneNumber, String desc);

    void cancelSubscribe(String houseId);

    @Transactional
    void finishSubscribe(String houseId);

    List<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(
            HouseSubscribeStatus status,
            int page,
            int size
    );

    List<HouseDTO> query(SearchForm searchForm);
}

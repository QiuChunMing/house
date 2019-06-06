package com.example.house.controller;

import com.example.house.base.ApiResponse;
import com.example.house.dto.HouseDTO;
import com.example.house.dto.HouseSubscribeDTO;
import com.example.house.enmu.HouseStatus;
import com.example.house.enmu.HouseSubscribeStatus;
import com.example.house.es.ISearchService;
import com.example.house.exception.BindResultException;
import com.example.house.form.HouseForm;
import com.example.house.form.PageSearch;
import com.example.house.form.SearchForm;
import com.example.house.service.IHouseService;
import com.example.house.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class HouseController {

    @Autowired
    private IHouseService houseService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ISearchService searchService;


    @PostMapping("/api/user/house/add")
    public HouseDTO addHouse(@RequestBody HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> list = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                list.add(error.getDefaultMessage());
            }
            throw new BindResultException(list);
        }
        return houseService.save(houseForm);
    }

    @GetMapping("/api/user/house/list")
    public List<HouseDTO> listHouse(SearchForm searchForm) {
        log.debug(searchForm.toString());
        return houseService.query(searchForm);
    }


    @GetMapping("/api/admin/house/list")
    public List<HouseDTO> listHouse(PageSearch pageSearch) {
        log.debug(pageSearch.toString());
        Page<HouseDTO> pages = houseService.adminQuery(pageSearch);
        List<HouseDTO> houseDTOS = new ArrayList<>();
        pages.forEach(page -> {
            houseDTOS.add(page);
        });
        return houseDTOS;
    }

    @GetMapping("/api/user/house/photo/token")
    public Map<String, String> getUploadToken() {
        Map map = new HashMap();
        map.put("uptoken", imageService.getUploadToken());
        return map;
    }

    @PostMapping("/api/houses/auditing/{houseId}")
    public void auditingHouse(@PathVariable("houseId") String houseId) {
        log.debug(houseId);
        if (houseId != null) {
            houseService.updateStatus(houseId, HouseStatus.PASSED.getStatus());
        }
    }

    @GetMapping("/api/house/{houseId}")
    public HouseDTO getDetail(@PathVariable String houseId) {
        return houseService.findHouseWithDetail(houseId);
    }

    @PostMapping(value = "api/user/house/subscribe")
    @ResponseBody
    public void subscribeHouse(@RequestParam(value = "houseId") String houseId) {
        houseService.addSubscribeOrder(houseId);
    }

    @PostMapping(value = "api/user/house/subscribe/delete")
    public void cancelSubscribe(@RequestParam(value = "houseId") String houseId) {
        log.info("删除订阅id:{}", houseId);
        houseService.cancelSubscribe(houseId);
    }

    @GetMapping(value = "api/user/house/subscribe/list")
    public List<Pair<HouseDTO, HouseSubscribeDTO>> listSubscribe() {
        return houseService.querySubscribeList(HouseSubscribeStatus.NO_SUBSCRIBE, 0, 20);
    }

    @GetMapping(value = "/api/admin/subscribe/list")
    public List<Pair<HouseDTO, HouseSubscribeDTO>> allListSubscribe() {
        return houseService.querySubscribeList(HouseSubscribeStatus.NO_SUBSCRIBE, 0, 20);
    }

    @PostMapping(value = "/api/admin/subscribe/finish")
    public void finishSub(@RequestParam("houseId") String houseId) {
        houseService.finishSubscribe(houseId);
    }

    @GetMapping(value = "/api/house/suggest")
    public List<String> suggest(@RequestParam("prefix") String prefix) {
       return searchService.suggest(prefix);
    }
}

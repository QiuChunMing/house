package com.example.house.controller;

import com.example.house.base.ApiResponse;
import com.example.house.dto.HouseDTO;
import com.example.house.dto.HouseSubscribeDTO;
import com.example.house.enmu.HouseStatus;
import com.example.house.enmu.HouseSubscribeStatus;
import com.example.house.es.ISearchService;
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
    public ApiResponse addHouse(@RequestBody HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> list = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                list.add(error.getDefaultMessage());
            }
            return ApiResponse.fail(list.toString());
        }
        HouseDTO houseDTO = houseService.save(houseForm);
        return ApiResponse.success(houseDTO);
    }

    @GetMapping("/api/user/house/list")
    public ApiResponse listHouse(SearchForm searchForm) {
        log.debug(searchForm.toString());
        List<HouseDTO> dtos = houseService.query(searchForm);
        return ApiResponse.success(dtos);
    }


    @GetMapping("/api/admin/house/list")
    public ApiResponse listHouse(PageSearch pageSearch) {

        log.debug(pageSearch.toString());
        Page<HouseDTO> pages = houseService.adminQuery(pageSearch);
        List<HouseDTO> houseDTOS = new ArrayList<>();
        pages.forEach(page -> {
            houseDTOS.add(page);
        });
        ApiResponse apiResponse = ApiResponse.success(houseDTOS);
        apiResponse.setTotal((int) pages.getTotalElements());
        return apiResponse;
    }

    @GetMapping("/api/user/house/photo/token")
    public Map<String, String> getUploadToken() {

        Map map = new HashMap();
        map.put("uptoken", imageService.getUploadToken());
        return map;
    }

    @PostMapping("/api/houses/auditing/{houseId}")
    public ApiResponse auditingHouse(@PathVariable("houseId") String houseId) {
        log.debug(houseId);
        if (houseId != null) {
            houseService.updateStatus(houseId, HouseStatus.PASSED.getStatus());
            return ApiResponse.success();
        }
        return ApiResponse.fail("houseId不能为空");
    }

    @GetMapping("/api/house/{houseId}")
    public ApiResponse getDetail(@PathVariable String houseId) {
        HouseDTO houseDTO = houseService.findHouseWithDetail(houseId);
        return ApiResponse.success(houseDTO);
    }

    @PostMapping(value = "api/user/house/subscribe")
    @ResponseBody
    public ApiResponse subscribeHouse(@RequestParam(value = "houseId") String houseId) {
        houseService.addSubscribeOrder(houseId);
        return ApiResponse.success();
    }

    @PostMapping(value = "api/user/house/subscribe/delete")
    public ApiResponse cancelSubscribe(@RequestParam(value = "houseId") String houseId) {
        log.info("删除订阅id:{}", houseId);
        houseService.cancelSubscribe(houseId);
        return ApiResponse.success();
    }

    @GetMapping(value = "api/user/house/subscribe/list")
    public ApiResponse listSubscribe() {
        List<Pair<HouseDTO, HouseSubscribeDTO>> pairs = houseService.querySubscribeList(HouseSubscribeStatus.NO_SUBSCRIBE, 0, 20);
        return ApiResponse.success(pairs);
    }

    @GetMapping(value = "/api/admin/subscribe/list")
    public ApiResponse allListSubscribe() {
        List<Pair<HouseDTO, HouseSubscribeDTO>> pairs = houseService.querySubscribeList(HouseSubscribeStatus.NO_SUBSCRIBE, 0, 20);
        return ApiResponse.success(pairs);
    }

    @PostMapping(value = "/api/admin/subscribe/finish")
    public ApiResponse finishSub(@RequestParam("houseId") String houseId) {
        houseService.finishSubscribe(houseId);
        return ApiResponse.success(houseId);
    }

    @GetMapping(value = "/api/house/suggest")
    public ApiResponse suggest(@RequestParam("prefix") String prefix) {
        List<String> suggests = searchService.suggest(prefix);
        return ApiResponse.success(suggests);
    }
}

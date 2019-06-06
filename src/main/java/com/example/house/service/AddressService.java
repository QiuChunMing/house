package com.example.house.service;

import com.example.house.base.CommonUtils;
import com.example.house.config.BaiduMapProperties;
import com.example.house.domain.Subway;
import com.example.house.domain.SubwayStation;
import com.example.house.domain.SupportAddress;
import com.example.house.dto.SubwayDTO;
import com.example.house.dto.SubwayStationDTO;
import com.example.house.dto.SupportAddressDTO;
import com.example.house.es.template.Location;
import com.example.house.exception.AddressException;
import com.example.house.repository.SubwayRepository;
import com.example.house.repository.SubwayStationRepository;
import com.example.house.repository.SupportAddressRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Slf4j
public class AddressService implements IAddressService {
    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BaiduMapProperties baiduMapProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    private static final String getLocationUrl = "http://api.map.baidu.com/geocoder/v2/";

    private static final String poiCreateUrl = "http://api.map.baidu.com/geodata/v3/poi/create";

    private static final String poiListUrl = "http://api.map.baidu.com/geodata/v3/poi/list";

    private static final String poiRemoveUrl = "http://api.map.baidu.com/geodata/v3/poi/delete";

    private static final String geoTableId = "200775";

    @Override
    public List<SupportAddressDTO> findAllCities() {
        List<SupportAddress> supportAddressList = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDTO> supportAddressDTOList = new ArrayList<>();
        supportAddressList.forEach(city -> {
            supportAddressDTOList.add(modelMapper.map(city, SupportAddressDTO.class));
        });
        return supportAddressDTOList;
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDTO> result = new HashMap<>();

        Optional<SupportAddress> city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY
                .getValue());
        Optional<SupportAddress> region = null;
        if (city.isPresent()) {
            region = supportAddressRepository.findByEnNameAndLevel(regionEnName, SupportAddress.Level.REGION.getValue());
            result.put(SupportAddress.Level.CITY, modelMapper.map(city.get(), SupportAddressDTO.class));
        }
        region.ifPresent(supportAddress -> result.put(SupportAddress.Level.REGION, modelMapper.map(supportAddress, SupportAddressDTO.class)));
        return result;
    }

    /**
     * @param cityName
     * @return
     */
    @Override
    public List<SupportAddressDTO> findAllRegionsByCityName(String cityName) {
        if (StringUtils.isEmpty(cityName)) {
            throw new IllegalArgumentException();
        }
        List<SupportAddressDTO> result = new ArrayList<>();

        List<SupportAddress> regions = supportAddressRepository.findAllByLevelAndBelongTo(
                SupportAddress.Level.REGION.getValue(), cityName);
        regions.forEach(region -> {
            result.add(modelMapper.map(region, SupportAddressDTO.class));
        });
        return result;
    }

    @Override
    public List<SubwayDTO> findAllSubwaysByCity(String cityName) {
        List<SubwayDTO> result = new ArrayList<>();
        List<Subway> subways = subwayRepository.findAllByCityEnName(cityName);

        subways.forEach(subway -> {
            result.add(modelMapper.map(subway, SubwayDTO.class));
        });
        return result;
    }

    @Override
    public List<SubwayStationDTO> findAllStationBySubway(String subwayId) {
        List<SubwayStationDTO> result = new ArrayList<>();
        List<SubwayStation> stations = subwayStationRepository.findAllBySubwayId(subwayId);
        stations.forEach(station -> {
            result.add(modelMapper.map(station, SubwayStationDTO.class));
        });
        return result;
    }

    @Override
    public SubwayDTO findSubway(String subwayId) {
        Optional<Subway> subway = subwayRepository.findById(subwayId);
        return subway.map(subway1 -> modelMapper.map(subway1, SubwayDTO.class)).orElse(null);
    }

    @Override
    public SubwayStationDTO findSubwayStation(String subwayStationId) {
        if (StringUtils.isEmpty(subwayStationId)) {
            throw new IllegalArgumentException();
        }
        Optional<SubwayStation> station = subwayStationRepository.findById(subwayStationId);
        return station.map(station1 -> modelMapper.map(station1, SubwayStationDTO.class)).orElse(null);
    }

    @Override
    public SupportAddressDTO findCity(String cityName) {
        if (StringUtils.isEmpty(cityName)) {
            throw new IllegalArgumentException();
        }
        Optional<SupportAddress> city = supportAddressRepository.findByEnNameAndLevel(
                cityName, SupportAddress.Level.CITY.getValue());

        return city.map(city1 -> modelMapper.map(city1, SupportAddressDTO.class)).orElse(null);
    }

    /**
     * 根据地址返回Location
     *
     * @param city    限定城市
     * @param address 详细地址
     * @return
     */
    @Override
    public Location getLocation(@Nullable String city, String address) {
        if (StringUtils.isEmpty(address)) {
            log.warn("[getLocation] 参数不能为空");
            throw new IllegalArgumentException("[getLocation] 参数不能为空");
        }
        checkNotNull(city, "[addressService.getLocation]：city can not be null");
        String accessKey = baiduMapProperties.getAccessKey();
        //编码后address长度将会大于84个字节，百度地图api会调用异常
        Map<String, String> params = Maps.newHashMap();
        params.put("output", "json");
        params.put("city", city);
        params.put("address", address);
        params.put("ak", accessKey);
        String url = String.format("%s?%s", getLocationUrl, CommonUtils.getUrlParamsByMap(params));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        log.info("[AddressService.getLocation]: request uri is {}", url);
        try {
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new AddressException();
            }
            JsonNode root = objectMapper.readTree(responseEntity.getBody());
            if (root.get("status").asInt() == 0) {
                JsonNode locationNode = root.get("result").get("location");
                return new Location(locationNode.get("lat").asDouble(), locationNode.get("lng").asDouble());
            } else {
                throw new AddressException();
            }
        } catch (Exception e) {
            log.error("解析位置数据异常:{}", responseEntity.toString());
            throw new AddressException("无法获取地址经纬度数据");
        }
    }

    @Override
    public void lbsUpload(Location location, String houseId, int price,
                          String area, String title, String address) {
        //组装请求体
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        params.add("latitude", String.valueOf(location.getLat()));
        params.add("longitude", String.valueOf(location.getLng()));
        params.add("coord_type", "3");
        params.add("geotable_id", geoTableId);
        params.add("ak", baiduMapProperties.getAccessKey());
        params.add("area", area);
        params.add("price", String.valueOf(price));
        params.add("houseId", String.valueOf(price));

        //将请求头设为表单提交

        HttpEntity<MultiValueMap<String, String>> request = getFormDataRequestHeader(params);
        ResponseEntity<String> entity = restTemplate.postForEntity(poiCreateUrl, request, String.class);
        try {
            if (entity.getStatusCode().is2xxSuccessful()) {
                JsonNode root = null;
                root = objectMapper.readTree(entity.getBody());
                if (root.get("status").asInt() != 0) {
                    throw new AddressException("百度服务器返回结果异常");
                }

            } else {
                throw new AddressException("连接百度服务器异常");
            }
        } catch (Exception e) {
            throw new AddressException();
        }
    }

    @Override
    public boolean poiExist(String houseId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("coord_type", "3");
        params.add("ak", baiduMapProperties.getAccessKey());
        params.add("geotable_id", geoTableId);
        params.add("houseId", houseId);

        ResponseEntity<String> entity = restTemplate.getForEntity(poiListUrl, String.class, params);

        try {
            if (entity.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(entity.getBody());
                if (root.get("status").asInt() == 0 && root.get("size").asInt() > 0) {
                    return true;
                } else {
                    throw new AddressException();
                }
            } else {
                throw new AddressException();
            }
        } catch (Exception e) {
            throw new AddressException();
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getFormDataRequestHeader(MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "multipart/form-data");
        return new HttpEntity<>(params, headers);
    }

    @Override
    public void poiRemove(String houseId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("ak", baiduMapProperties.getAccessKey());
        params.add("geotable_id", geoTableId);
        params.add("houseId", houseId);

        ResponseEntity<String> entity = restTemplate.postForEntity(poiRemoveUrl, getFormDataRequestHeader(params), String.class);

        try {
            if (entity.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(entity.getBody());
                if (root.get("status").asInt() != 0) {
                    throw new AddressException();
                }
            } else {
                throw new AddressException();
            }
        } catch (Exception e) {
            throw new AddressException();
        }
    }

    //http://api.map.baidu.com/place/v2/suggestion?query=天安门&region=北京&city_limit=true&output=json&ak=你的ak //GET请求
    private static final String addressSuggestUrl = "http://api.map.baidu.com/place/v2/suggestion";

    @Override
    public List<String> AddressSuggest(String region, String prefix) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("region", region);
        params.add("query", prefix);
        params.add("output", "json");
        params.add("ak", baiduMapProperties.getAccessKey());

        ResponseEntity<String> entity = restTemplate.getForEntity(addressSuggestUrl, String.class, params);
        try {
            if (entity.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(entity.getBody());
                if (root.get("status").asInt() == 0) {
                    List<String> suggests = new ArrayList<>();
                    JsonNode result = root.get("result");
                    for (int i = 0; i < result.size(); i++) {
                        JsonNode jsonNode = result.get(i);
                        suggests.add(jsonNode.get("name").asText());
                    }
                    return suggests;
                } else {
                    throw new AddressException();
                }
            } else {
                throw new AddressException();
            }
        } catch (Exception e) {
            throw new AddressException();
        }
    }

    @Override
    public SupportAddress addCity(String cityName) {
        Location location = this.getLocation(cityName, null);
        SupportAddress supportAddress = new SupportAddress();
        supportAddress.setCnName(cityName);
        supportAddress.setEnName(cityName);
        supportAddress.setLevel(SupportAddress.Level.CITY.getValue());
        supportAddress.setLatitude(String.valueOf(location.getLat()));
        supportAddress.setLongitude(String.valueOf(location.getLng()));
        try {
            log.debug(objectMapper.writeValueAsString(supportAddress));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        supportAddressRepository.save(supportAddress);
        return supportAddress;
    }

    @Override
    public void addRegion(String cityName, String regionName) {
        Optional<SupportAddress> cityOptional = supportAddressRepository.findByEnNameAndLevel(cityName, SupportAddress.Level.CITY.getValue());
        SupportAddress city;
        city = cityOptional.orElseGet(() -> addCity(cityName));
        Location location = this.getLocation(cityName, regionName);
        Optional<SupportAddress> level = supportAddressRepository.findByEnNameAndLevel(regionName, SupportAddress.Level.REGION.getValue());
        SupportAddress supportAddress = new SupportAddress();
        level.ifPresent(supportAddress1 -> supportAddress.setId(supportAddress1.getId()));

        supportAddress.setCnName(regionName);
        supportAddress.setEnName(regionName);
        supportAddress.setLevel(SupportAddress.Level.REGION.getValue());
        supportAddress.setBelongTo(city.getCnName());
        supportAddress.setLatitude(String.valueOf(location.getLat()));
        supportAddress.setLongitude(String.valueOf(location.getLng()));
        supportAddressRepository.save(supportAddress);
    }

    @Override
    public void deleteCity(String cityName) {
    }
}

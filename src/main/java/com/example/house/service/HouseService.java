package com.example.house.service;

import com.example.house.config.QiniuProperties;
import com.example.house.domain.*;
import com.example.house.dto.HouseDTO;
import com.example.house.dto.HouseDetailDTO;
import com.example.house.dto.HousePictureDTO;
import com.example.house.dto.HouseSubscribeDTO;
import com.example.house.enmu.HouseSort;
import com.example.house.enmu.HouseStatus;
import com.example.house.enmu.HouseSubscribeStatus;
import com.example.house.es.SearchService;
import com.example.house.exception.*;
import com.example.house.form.HouseForm;
import com.example.house.form.PageSearch;
import com.example.house.form.PhotoForm;
import com.example.house.form.SearchForm;
import com.example.house.repository.*;
import com.example.house.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class HouseService implements IHouseService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private HouseSubscribeRepository houseSubscribeRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private QiniuProperties qiniuProperties;


    /**
     * 保存房源信息
     *
     * @param houseForm
     * @return
     */
    @Override
    public HouseDTO save(HouseForm houseForm) {
        HouseDetail houseDetail = new HouseDetail();
        wrapperDetailInfo(houseDetail, houseForm);
        House house = new House();
        modelMapper.map(houseForm, house);

        LocalDateTime date = LocalDateTime.now();
        house.setCreateTime(date);
        house.setUpdateTime(date);
        house.setOwnerId(UserUtils.getUserId());
        house = houseRepository.save(house);

        //保存详细信息
        houseDetail.setHouseId(house.getId());
        houseDetail = houseDetailRepository.save(houseDetail);
        //保存图片信息
        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        Iterable<HousePicture> housePictures = housePictureRepository.saveAll(pictures);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);

        //将图片信息包装后返回
        List<HousePictureDTO> housePictureDTOS = new ArrayList<>();
        housePictures.forEach(housePicture -> housePictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(housePictureDTOS);
        //TODO 设置图片前缀
        houseDTO.setCover(house.getCover());

        //设置标签
        List<String> tags = houseForm.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            houseTagRepository.saveAll(houseTags);
            houseDTO.setTags(tags);
        }
        searchService.index(house.getId());
        return houseDTO;
    }


    /**
     * 将dto中photoForm转为housePicture
     *
     * @param houseForm
     * @param houseId
     * @return
     */
    private List<HousePicture> generatePictures(HouseForm houseForm, String houseId) {
        List<PhotoForm> photoForms = houseForm.getPhotos();
        List<HousePicture> housePictures = new ArrayList<>();
        if (photoForms == null || photoForms.isEmpty()) {
            return housePictures;
        }
        photoForms.forEach(photoForm -> {
            HousePicture housePicture = new HousePicture();
            housePicture.setHouseId(houseId);
            housePicture.setCdnPrefix(qiniuProperties.getDomain());
            housePicture.setPath(photoForm.getPath());
            housePicture.setWidth(photoForm.getWidth());
            housePicture.setHeight(photoForm.getHeight());
            housePictures.add(housePicture);
        });
        return housePictures;
    }

    /**
     * 统计房源数
     *
     * @return
     */
    @Override
    public long total() {
        return houseRepository.count();
    }

    private void wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {

        //包装地铁相关信息
        if (houseForm.getSubwayLineId() != null || houseForm.getSubwayStationId() != null) {
            Optional<Subway> subwayLineOptional = subwayRepository.findById(houseForm.getSubwayLineId());
            Optional<SubwayStation> subwayStationOptional = subwayStationRepository.findById(houseForm.getSubwayStationId());

            if (!subwayLineOptional.isPresent()) {
                throw new SubwayNotFoundException();
            }
            if (!subwayStationOptional.isPresent()) {
                throw new SubwayStationNotFoundException();
            }

            Subway subway = subwayLineOptional.get();
            houseDetail.setSubwayLineId(subway.getId());
            houseDetail.setSubwayLineName(subway.getName());
            SubwayStation subwayStation = subwayStationOptional.get();
            houseDetail.setSubwayStationId(subwayStation.getId());
            houseDetail.setSubwayStationName(subwayStation.getName());
        }

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
    }

    @Override
    public void update(HouseForm houseForm) {
        Optional<House> houseOptional = houseRepository.findById(houseForm.getId());
        if (!houseOptional.isPresent()) {
            throw new HouseNotFoundException();
        }
        House house = houseOptional.get();
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(house.getId());
        if (houseDetail == null) {
            throw new HouseDetailNotFoundException();
        }
        wrapperDetailInfo(houseDetail, houseForm);
        houseDetail = houseDetailRepository.save(houseDetail);

        List<HousePicture> housePictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.saveAll(housePictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }

        modelMapper.map(houseForm, house);
        house.setUpdateTime(LocalDateTime.now());
        houseRepository.save(house);

        if (house.getStatus() == HouseStatus.PASSED.getStatus()) {//将上架
            //将数据更新到es中
            searchService.index(house.getId());
        }
    }

    /**
     * 处理管理员表格头部排列条件,不查询详细信息
     *
     * @param pageSearch
     * @return
     */
    @Override
    public Page<HouseDTO> adminQuery(PageSearch pageSearch) {
        List<HouseDTO> houseDTOS = new ArrayList<>();
        Sort sort = HouseSort.generateSort(pageSearch.getOrderBy(), pageSearch.getDirection());
        Pageable pageable = PageRequest.of(pageSearch.getPage(), pageSearch.getSize(), sort);
        Page houses = houseRepository.findAll(
                (Specification<House>) (root, criteriaQuery, criteriaBuilder) -> {
//            去除删除的项目
                    Predicate predicate =
                            criteriaBuilder.and(criteriaBuilder.notEqual(root.get("status"), HouseStatus.DELETED.getStatus()));
//            城市
                    if (!StringUtils.isEmpty(pageSearch.getCity())) {
                        log.debug(pageSearch.getCity());
                        predicate = criteriaBuilder.and(criteriaBuilder.equal(root.get("cityEnName"), pageSearch.getCity()));
                    }
                    if (!StringUtils.isEmpty(pageSearch.getStatus())) {
                        log.debug(pageSearch.getStatus().toString());
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), pageSearch.getStatus()));
                    }
                    if (!StringUtils.isEmpty(pageSearch.getCreateTimeMin())) {
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), pageSearch.getCreateTimeMin()));
                    }
                    if (!StringUtils.isEmpty(pageSearch.getCreateTimeMax())) {
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), pageSearch.getCreateTimeMax()));
                    }
                    if (!StringUtils.isEmpty(pageSearch.getTitle())) {
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("title"), "%" + pageSearch.getTitle() + "%"));
                    }
                    return predicate;
                },
                pageable);

        houses.forEach(house -> {
            houseDTOS.add(modelMapper.map(house, HouseDTO.class));
        });
        Page<HouseDTO> houseDTOPage = new PageImpl<HouseDTO>(houseDTOS, pageable, houses.getTotalElements());
        return houseDTOPage;
    }

    @Override
    public HouseDTO findHouseWithDetail(String houseId) {
        Optional<House> houseOptional = houseRepository.findById(houseId);
        if (!houseOptional.isPresent()) {
            throw new HouseNotFoundException();
        }
        House house = houseOptional.get();
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(house.getId());
        List<HousePicture> pictures = housePictureRepository.findAllByHouseId(house.getId());

        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        for (HousePicture picture : pictures) {
            HousePictureDTO pictureDTO = modelMapper.map(picture, HousePictureDTO.class);
            pictureDTOS.add(pictureDTO);
        }

        List<HouseTag> houseTags = houseTagRepository.findAllByHouseId(house.getId());
        List<String> tags = new ArrayList<>();
        for (HouseTag houseTag : houseTags) {
            tags.add(houseTag.getContent());
        }

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setTags(tags);

        if (UserUtils.getUserId() != null) {
            HouseSubscribe houseSubscribe = houseSubscribeRepository.findByHouseIdAndUserId(house.getId(), UserUtils.getUserId());
            if (houseSubscribe != null) {
                houseDTO.setSubscribeStatus(houseSubscribe.getStatus());
            }
        }
        return houseDTO;
    }

    @Transactional
    @Override
    public void updateStatus(String houseId, Integer status) {
        Optional<House> houseOptional = houseRepository.findById(houseId);
        if (!houseOptional.isPresent()) {
            throw new HouseNotFoundException();
        }
        House house = houseOptional.get();
        HouseStatus houseStatusEnum = HouseStatus.getEnumType(status);
        if (houseStatusEnum == null) {
            throw new IllegalArgumentException("非法房屋状态");
        }
        house.setStatus(houseStatusEnum.getStatus());
        houseRepository.save(house);
        //TODO 更新es索引
        searchService.index(houseId);
    }

    /**
     * 添加标签
     *
     * @param houseId
     * @param tag
     */
    @Override
    public void addTag(String houseId, String tag) {
        Optional<House> houseOptional = houseRepository.findById(houseId);
        if (!houseOptional.isPresent()) {
            throw new HouseNotFoundException();
        }
        HouseTag houseTag = houseTagRepository.findByContentAndHouseId(tag, houseId);
        if (houseTag != null) {
            throw new AddHouseTagException("标签已存在");
        }
        houseTagRepository.save(new HouseTag(houseId, tag));
    }


    /**
     * 删除标签
     *
     * @param houseId
     * @param tag
     */
    @Override
    public void removeTag(String houseId, String tag) {
        Optional<House> houseOptional = houseRepository.findById(houseId);
        if (!houseOptional.isPresent()) {
            throw new HouseNotFoundException();
        }
        HouseTag houseTag = houseTagRepository.findByContentAndHouseId(tag, houseId);
        if (houseTag == null) {
            throw new AddHouseTagException("标签不存在");
        }
        houseTagRepository.delete(houseTag);
    }

    @Transactional
    @Override
    public void addSubscribeOrder(String houseId) {
        HouseSubscribe houseSubscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, UserUtils.getUserId());
        if (houseSubscribe != null) {
            throw new SubScribeException("已添加进预约");
        }
        Optional<House> houseOptional = houseRepository.findById(houseId);
        if (!houseOptional.isPresent()) {
            throw new HouseNotFoundException();
        }
        HouseSubscribe subscribe = new HouseSubscribe();
        LocalDateTime date = LocalDateTime.now();
        subscribe.setCreateTime(date);
        subscribe.setUpdateTime(date);
        subscribe.setHouseId(houseId);
        subscribe.setUserId(UserUtils.getUserId());
        subscribe.setStatus(HouseStatus.NOT_AUDITED.getStatus());
        houseSubscribeRepository.save(subscribe);
    }

    /**
     * 预约时间，电话等
     *
     * @param houseId
     * @param orderTime
     * @param phoneNumber
     * @param desc
     */
    @Override
    public void subscribe(String houseId, LocalDate orderTime, String phoneNumber, String desc) {
        HouseSubscribe houseSubscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, UserUtils.getUserId());
        if (houseSubscribe != null) {
            throw new SubScribeException("已添加进预约");
        }

        if (houseSubscribe.getStatus() != HouseSubscribeStatus.IN_ORDER_LIST.getStatus()) {
            throw new SubScribeException("未到预约时间，电话阶段");
        }

        HouseSubscribe subscribe = new HouseSubscribe();
        LocalDateTime date = LocalDateTime.now();
        subscribe.setUpdateTime(date);
        subscribe.setPhoneNumber(phoneNumber);
        subscribe.setUserId(houseId);
        subscribe.setOrderTime(orderTime);
        subscribe.setContent(desc);
        subscribe.setStatus(HouseSubscribeStatus.IN_ORDER_TIME.getStatus());
        houseSubscribeRepository.save(subscribe);
    }

    /**
     * 删除预约
     *
     * @param houseId
     */
    @Override
    public void cancelSubscribe(String houseId) {
        String userId = UserUtils.getUserId();
        HouseSubscribe houseSubscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (houseSubscribe == null) {
            throw new SubScribeException("无此条预约记录");
        }
        houseSubscribeRepository.delete(houseSubscribe);
    }

    @Transactional
    @Override
    public void finishSubscribe(String houseId) {
        //TODO 更新查看次数
        HouseSubscribe houseSubscribe = houseSubscribeRepository.findByHouseId(houseId);
        if (houseSubscribe == null) {
            throw new SubScribeException("找不到预约记录");
        }
        houseSubscribe.setStatus(HouseSubscribeStatus.FINISH.getStatus());
        houseRepository.updateWatchTime(houseId);
    }

    /**
     * 查询预约列表
     *
     * @param status
     * @param page
     * @param size
     * @return
     */
    @Override
    public List<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(
            HouseSubscribeStatus status,
            int page,
            int size
    ) {
        String userId = UserUtils.getUserId();
        log.info("查询预约列表时用户id为{}", userId);
        Pageable pageable = PageRequest.of(page, size);
        log.info("查询预约列表是状态是{}", status.getStatus());

        Page<HouseSubscribe> subscribePage = houseSubscribeRepository.findAllByUserIdAndStatus(userId, 0, pageable);

        List<Pair<HouseDTO, HouseSubscribeDTO>> pairs = new ArrayList<>();
        if (subscribePage.getTotalElements() <= 0) {
            return pairs;
        }

        List<HouseSubscribeDTO> subscribeDTOS = new ArrayList<>();
        List<String> houseIds = new ArrayList<>();
        subscribePage.forEach(houseSubscribe -> {
            subscribeDTOS.add(modelMapper.map(houseSubscribe, HouseSubscribeDTO.class));
            houseIds.add(houseSubscribe.getHouseId());
        });

        Map<String, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<House> houses = houseRepository.findAllById(houseIds);

        houses.forEach(house -> {
            idToHouseMap.put(house.getId(), modelMapper.map(house, HouseDTO.class));
        });

        for (HouseSubscribeDTO subscribeDTO : subscribeDTOS) {
            Pair<HouseDTO, HouseSubscribeDTO> pair = Pair.of(idToHouseMap.get(subscribeDTO.getHouseId()), subscribeDTO);
            pairs.add(pair);
        }
        return pairs;
    }

    /**
     * 通用查询
     *
     * @param searchForm
     * @return
     */
    @Override
    public List<HouseDTO> query(SearchForm searchForm) {
        if (!StringUtils.isEmpty(searchForm.getKeyWord())) {
            List<String> ids = searchService.query(searchForm);
            List<HouseDTO> houseDTOS = wrapHouseList(ids);
            return houseDTOS;
        }
        return simpleQuery(searchForm);
    }


    /**
     * 查询不带关键字的详细房源信息时使用
     *
     * @param searchForm
     * @return
     */
    private List<HouseDTO> simpleQuery(SearchForm searchForm) {
        Sort sort = HouseSort.generateSort(searchForm.getOrderBy(), searchForm.getOrderDirection());
        Pageable pageable = PageRequest.of(searchForm.getPage(), searchForm.getSize(), sort);

        Page<House> houses = houseRepository.findAll((Specification<House>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), HouseStatus.PASSED.getStatus());
            if (!StringUtils.isEmpty(searchForm.getCityEnName())) {
                predicate = criteriaBuilder.and(criteriaBuilder.equal(root.get("cityEnName"), searchForm.getCityEnName()));
            }
            //按照距离地铁距离排序时，排除掉距离为-1的信息
            if (HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(searchForm.getOrderBy())) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY), -1));
            }
            return predicate;
        }, pageable);

        List<String> houseIds = new ArrayList<>();
        Map<String, HouseDTO> idToHouseDTOMap = new HashMap<>();
        List<HouseDTO> houseDTOS = new ArrayList<>();

        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(qiniuProperties.getDomain() + house.getCover());
            houseDTOS.add(houseDTO);

            houseIds.add(house.getId());
            idToHouseDTOMap.put(house.getId(), houseDTO);
        });

        wrapperHouseListDetail(houseIds, idToHouseDTOMap);
        return houseDTOS;
    }

    private void wrapperHouseListDetail(List<String> houseIds, Map<String, HouseDTO> idToHouseDTOMap) {
        List<HouseDetail> houseDetails = houseDetailRepository.findAllByHouseIdIn(houseIds);
        houseDetails.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseDTOMap.get(houseDetail.getHouseId());
            houseDTO.setHouseDetail(modelMapper.map(houseDetail, HouseDetailDTO.class));
        });
        List<HouseTag> houseTags = houseTagRepository.findAllByHouseIdIsIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDTO houseDTO = idToHouseDTOMap.get(houseTag.getHouseId());
            houseDTO.getTags().add(houseTag.getContent());
        });
    }


    private List<HouseDTO> wrapHouseList(List<String> houseIds) {
        Iterable<House> houses = houseRepository.findAllById(houseIds);

        List<String> ids = new ArrayList<>();
        Map<String, HouseDTO> idToHouseDTOMap = new HashMap<>();
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            ids.add(house.getId());
            idToHouseDTOMap.put(house.getId(), houseDTO);
        });

        wrapperHouseListDetail(ids, idToHouseDTOMap);

        List<HouseDTO> dtos = new ArrayList<>();
        for (String houseId : houseIds) {
            dtos.add(idToHouseDTOMap.get(houseId));
        }
        return dtos;
    }

}

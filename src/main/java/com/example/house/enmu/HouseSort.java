package com.example.house.enmu;

import org.elasticsearch.common.util.set.Sets;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.Set;

public class HouseSort {
    public static final String DEFAULT_SORT_KEY = "updateTime";
    public static final String DISTANCE_TO_SUBWAY_KEY = "distanceToSubway";

    private static final Set<String> keys = Sets.newHashSet(
            DEFAULT_SORT_KEY,
            "createTime",
            "price",
            "area",
            DISTANCE_TO_SUBWAY_KEY
    );

    public static Sort generateSort(String key, String directionKey) {
        key = getKey(key);
        Optional<Sort.Direction> directionOptional = Sort.Direction.fromOptionalString(directionKey);
        Sort.Direction direction = directionOptional.orElse(Sort.Direction.DESC);
        return new Sort(direction, key);
    }

    private static String getKey(String key) {
        if (keys.contains(key)) {
            return key;
        }
        return DEFAULT_SORT_KEY;
    }

}

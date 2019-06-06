package com.example.house.exception;

import com.example.house.base.APIException;

public class SubwayStationNotFoundException extends APIException {
    public SubwayStationNotFoundException() {
        super(StatusCode.INVALID_PARAM);
    }

    public SubwayStationNotFoundException(String message) {
        super(StatusCode.INVALID_PARAM, message);
    }
}

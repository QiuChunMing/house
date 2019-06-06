package com.example.house.exception;

import com.example.house.base.APIException;

public class HouseDetailNotFoundException extends APIException {
    public HouseDetailNotFoundException() {
        super(StatusCode.INVALID_PARAM);
    }

    public HouseDetailNotFoundException(String message) {
        super(StatusCode.INVALID_PARAM, message);
    }
}

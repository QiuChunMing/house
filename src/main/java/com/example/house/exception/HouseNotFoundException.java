package com.example.house.exception;

import com.example.house.base.APIException;

public class HouseNotFoundException extends APIException {
    public HouseNotFoundException() {
        super(StatusCode.INVALID_PARAM);
    }

    public HouseNotFoundException(String message) {
        super(StatusCode.INVALID_PARAM, message);
    }
}

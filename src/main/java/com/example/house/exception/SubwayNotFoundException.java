package com.example.house.exception;

import com.example.house.base.APIException;

public class SubwayNotFoundException extends APIException {
    public SubwayNotFoundException() {
        super(StatusCode.INVALID_PARAM);
    }

    public SubwayNotFoundException(String message) {
        super(StatusCode.INVALID_PARAM, message);
    }
}

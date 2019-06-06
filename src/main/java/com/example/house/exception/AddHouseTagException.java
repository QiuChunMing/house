package com.example.house.exception;

import com.example.house.base.APIException;

public class AddHouseTagException extends APIException {
    public AddHouseTagException() {
        super(StatusCode.INVALID_PARAM);
    }

    public AddHouseTagException(String message) {
        super(StatusCode.INVALID_PARAM, message);
    }
}

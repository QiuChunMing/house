package com.example.house.exception;

import com.example.house.base.APIException;

public class AddressException extends APIException {
    public AddressException() {
        super(StatusCode.INVALID_PARAM);
    }

    public AddressException(String message) {
        super(StatusCode.INVALID_PARAM, message);
    }
}

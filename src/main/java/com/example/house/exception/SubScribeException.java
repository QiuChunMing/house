package com.example.house.exception;

import com.example.house.base.APIException;

public class SubScribeException extends APIException {
    public SubScribeException(String message) {
        super(StatusCode.INVALID_PARAM,message);
    }
}

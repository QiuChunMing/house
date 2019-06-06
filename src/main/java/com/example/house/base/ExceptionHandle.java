package com.example.house.base;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(Exception.class)
    public ErrorResult handleException(Exception e) {
        ErrorResult result = new ErrorResult();
        result.setCode(ResultCode.FAIL.code());
        result.setMsg(ResultCode.FAIL.message());
        result.setErrors(e);
        return result;
    }
}

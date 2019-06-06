package com.example.house.base;

import lombok.Data;

@Data
public class ErrorBody {
    private Integer code;
    private String msg;
    private Object errors;

    public ErrorBody(Object errors) {
        this.errors = errors;
    }

    public ErrorBody(Integer code) {
        this.code = code;
    }
}

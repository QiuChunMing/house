package com.example.house.base;

import lombok.Data;

@Data
public class ErrorResult {
    private Integer code;
    private String msg;
    private Object errors;
}

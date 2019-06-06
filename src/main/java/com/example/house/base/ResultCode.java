package com.example.house.base;

public enum ResultCode {
    SUCCESS(0,"success"),
    FAIL(1,"fail");

    private Integer code;
    private String message;

    ResultCode(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer code(){
        return this.code;
    }

    public String message(){
        return this.message;
    }
}

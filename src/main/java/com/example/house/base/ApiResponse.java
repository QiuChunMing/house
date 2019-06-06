package com.example.house.base;

import lombok.Data;

@Data
public class ApiResponse {
    private int status;
    private String msg;
    private Object data;


    public static ApiResponse success() {
        return success(null);
    }
    public static ApiResponse success(Object o) {
        ApiResponse response = new ApiResponse();
        response.setStatus(ResultCode.SUCCESS.code());
        response.setMsg(ResultCode.SUCCESS.message());
        response.setData(o);
        return response;
    }

    public static ApiResponse fail(ResultCode resultCode) {
        return fail(resultCode, null);
    }

    public static ApiResponse fail(ResultCode resultCode,Object o) {
        return fail(resultCode.code(), resultCode.message(), o);
    }
    public static ApiResponse fail(Integer code,String msg,Object o) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(code);
        apiResponse.setMsg(msg);
        apiResponse.setData(o);
        return apiResponse;
    }

}

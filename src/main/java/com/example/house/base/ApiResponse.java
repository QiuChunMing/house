package com.example.house.base;

import lombok.Data;

@Data
public class ApiResponse {
    private int status;
    private String msg;
    private Object data;

    public static ApiResponse success(Object o) {
        return success("success", o);
    }

    public static ApiResponse success(String msg, Object o) {
        ApiResponse response = new ApiResponse();
        response.setStatus(0);
        response.setMsg(msg);
        response.setData(o);
        return response;
    }

    public static ApiResponse success() {
        return success(null);
    }


}

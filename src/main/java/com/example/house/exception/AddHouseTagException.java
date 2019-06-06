package com.example.house.exception;

import lombok.Data;

@Data
public class AddHouseTagException extends RuntimeException {
    public AddHouseTagException(String message) {
        super(message);
    }

}

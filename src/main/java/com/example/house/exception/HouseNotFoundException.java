package com.example.house.exception;

public class HouseNotFoundException extends RuntimeException {
    public HouseNotFoundException() {
    }

    public HouseNotFoundException(String message) {
        super(message);
    }
}

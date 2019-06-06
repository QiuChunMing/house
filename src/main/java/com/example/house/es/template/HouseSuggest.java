package com.example.house.es.template;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HouseSuggest {
    private String input;
    private int weight;

    public HouseSuggest(String input, int weight) {
        this.input = input;
        this.weight = weight;
    }

    public HouseSuggest() {
    }
}

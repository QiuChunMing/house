package com.example.house.es;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

@Data
public class RentValueBlock {
    private int min;
    private int max;
    public static final RentValueBlock withoutLimit = new RentValueBlock(-1, -1);


    public RentValueBlock(int min, int max) {
        this.min = min;
        this.max = max;
    }

    private static ImmutableMap<String,RentValueBlock> areaBlock;
    private static ImmutableMap<String,RentValueBlock> priceBlock;
    static {
        areaBlock = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-10",new RentValueBlock(-1,10))
                .put("10-15",new RentValueBlock(10,15))
                .put("15-20",new RentValueBlock(15,20))
                .put("20-*",new RentValueBlock(20,-1))
                .build();
        priceBlock = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-1000",new RentValueBlock(-1,1000))
                .put("1000-1500",new RentValueBlock(1000,1500))
                .put("1500-2000",new RentValueBlock(1500,2000))
                .put("2000-*",new RentValueBlock(2000,-1))
                .build();
    }

    public static RentValueBlock getAreaBlockFromString(String value) {
        RentValueBlock rentValueBlock = areaBlock.get(value);
        if (rentValueBlock == null) {
            return withoutLimit;
        }
        return rentValueBlock;
    }

    public static RentValueBlock getPriceBlockFromString(String value) {
        RentValueBlock rentValueBlock = priceBlock.get(value);
        if (rentValueBlock == null) {
            return withoutLimit;
        }
        return rentValueBlock;
    }
}

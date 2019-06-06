package com.example.house.enmu;

public enum HouseStatus {
    NOT_AUDITED(0),//未审核
    PASSED(1),//审核通过
    RENTED(2),//已出租
    DELETED(3);//删除

    private Integer status;

    HouseStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
    public static HouseStatus getEnumType (int val) {
        for (HouseStatus type : HouseStatus .values()) {
            if (type.getStatus() == val) {
                return type;
            }
        }
        return null;
    }
}

package com.cniupay.pay.enums;

/**
 * Created by zhuzhuodong on 2018/7/17.
 */

public enum PayStatusEnum {

    FAILURE(0),
    SUCCESS(1);

    private Integer code;

    PayStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

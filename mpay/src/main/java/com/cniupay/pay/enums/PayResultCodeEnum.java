package com.cniupay.pay.enums;

/**
 * Created by zhuzhuodong on 2018/7/17.
 */

public enum PayResultCodeEnum {

    SUCCESS(0),
    FAILURE(1);

    private Integer code;

    PayResultCodeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

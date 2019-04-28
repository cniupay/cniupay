package com.cniupay.pay.enums;

/**
 * Created by zhuzhuodong on 2018/7/17.
 */

public enum PayTypeEnum {

    ZHIFUBAO(0),
    WEIXIN(1);

    private Integer code;

    PayTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

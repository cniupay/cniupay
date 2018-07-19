package com.zhuzhuodong.tool.android.mmpay.enums;

/**
 * Created by zhuzhuodong on 2018/7/17.
 */

public enum MmPayResultCodeEnum {

    SUCCESS(0),
    FAILURE(1);

    private Integer code;

    MmPayResultCodeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

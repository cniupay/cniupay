package com.cniupay.pay.util;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public class FieldUtil {

    public static String ifNullReturnEmpty(String source) {
        if (null == source) {
            return "";
        }
        return source;
    }

}

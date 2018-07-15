package com.zhuzhuodong.tool.android.mmpay.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public class DateUtil {

    private static String DEFAULT_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    private static SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat(DEFAULT_FORMAT_STR);

    public static String getNowDefaultFormat() {
        return DEFAULT_FORMAT.format(new Date());
    }

}

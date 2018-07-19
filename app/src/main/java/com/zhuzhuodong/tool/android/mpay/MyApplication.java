package com.zhuzhuodong.tool.android.mpay;

import android.app.Application;

import com.zhuzhuodong.tool.android.mmpay.MmPay;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MmPay.getInstance(getApplicationContext()).init("test");
    }
}

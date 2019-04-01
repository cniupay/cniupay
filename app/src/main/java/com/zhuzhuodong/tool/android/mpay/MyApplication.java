package com.zhuzhuodong.tool.android.mpay;

import android.app.Application;

import com.cniupay.pay.CNiuPay;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CNiuPay.getInstance(getApplicationContext()).init("bf5a7bc6c9c0bffc0d3d55cf0991183b116a0fa15d654101b388e688060b85e6");
    }
}

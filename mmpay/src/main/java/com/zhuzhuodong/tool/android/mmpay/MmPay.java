package com.zhuzhuodong.tool.android.mmpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.zhuzhuodong.tool.android.mmpay.activity.MPayActivity;
import com.zhuzhuodong.tool.android.mmpay.common.CommonResp;
import com.zhuzhuodong.tool.android.mmpay.common.CommonUrl;
import com.zhuzhuodong.tool.android.mmpay.enums.MmPayResultCodeEnum;
import com.zhuzhuodong.tool.android.mmpay.listener.PayResultListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public class MmPay {

    private static final String TAG = "MmPay";

    private static MmPay INSTANCE = null;

    private static Context context;
    private static String appId;
    private static PayResultListener listener;

    private MmPay(Context context) {
        this.context = context;
    }

    public static MmPay getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (MmPay.class) {
                INSTANCE = new MmPay(context);
            }
        }
        INSTANCE.context = context;
        return INSTANCE;
    }

    public void init(String appId) {
        MmPay.appId = appId;
    }

    public void pay(long amount, String tradeName, String outTradeNo, PayResultListener listener) {
        if (null == listener) {
            Toast.makeText(context, "listener为空", Toast.LENGTH_SHORT).show();
            return;
        }
        MmPay.listener = listener;
        Intent intent = new Intent(context, MPayActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("title", tradeName);
        intent.putExtra("outTradeNo", outTradeNo);
        context.startActivity(intent);
        return;

    }

    public static String getAppId() {
        return appId;
    }

    public static void doFinished(MmPayResultCodeEnum resultCode, String resultMsg, long amount) {
        if (null != listener) {
            listener.onPayFinished(resultCode, resultMsg, amount);
        }
    }

}

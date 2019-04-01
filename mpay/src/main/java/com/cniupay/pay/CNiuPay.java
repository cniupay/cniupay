package com.cniupay.pay;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.cniupay.pay.activity.MPayActivity;
import com.cniupay.pay.common.CommonUrl;
import com.cniupay.pay.enums.PayResultCodeEnum;
import com.cniupay.pay.listener.PayResultListener;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public class CNiuPay {

    private static final String TAG = "CNiuPay";

    private static CNiuPay INSTANCE = null;

    private static Context context;
    private static String appKey;
    private static PayResultListener listener;

    private CNiuPay(Context context) {
        this.context = context;
    }

    public static CNiuPay getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (CNiuPay.class) {
                INSTANCE = new CNiuPay(context);
            }
        }
        INSTANCE.context = context;
        return INSTANCE;
    }

    private static void report() {
        if (null == context || null == appKey) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(CommonUrl.REPORT_DEVICE);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.connect();
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    StringBuilder builder = new StringBuilder();
                    builder.append("appKey=").append(URLEncoder.encode(appKey, "utf-8")).append("&");
                    builder.append("appId=").append(URLEncoder.encode(context.getPackageName(),
                            "utf-8"));
                    out.writeBytes(builder.toString());
                    out.flush();
                    out.close();
                    int temp=connection.getResponseCode();
                    int bbb=temp;
                } catch (Exception e) {
                    Log.e(TAG, "report: ", e);
                } finally {
                    if (null != connection) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public void init(String appKey) {
        CNiuPay.appKey = appKey;
        report();
    }

    public void pay(long amount, String tradeName, String outTradeNo, PayResultListener listener) {
        if (null == listener) {
            Toast.makeText(context, "listener为空", Toast.LENGTH_SHORT).show();
            return;
        }
        CNiuPay.listener = listener;
        Intent intent = new Intent(context, MPayActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("title", tradeName);
        intent.putExtra("outTradeNo", outTradeNo);
        context.startActivity(intent);
        return;

    }

    public static String getAppKey() {
        return appKey;
    }

    public static void doFinished(PayResultCodeEnum resultCode, String resultMsg, long amount) {
        if (null != listener) {
            listener.onPayFinished(context, resultCode, resultMsg, amount);
        }
    }

}

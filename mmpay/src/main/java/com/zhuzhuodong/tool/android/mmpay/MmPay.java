package com.zhuzhuodong.tool.android.mmpay;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.zhuzhuodong.tool.android.mmpay.common.CommonUrl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public class MmPay {

    private static final String TAG = "MmPay";

    private static MmPay INSTANCE = null;

    private Context context;
    private String appId;

    private MmPay(Context context) {
        this.context = context;
    }

    public static MmPay getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (MmPay.class) {
                INSTANCE = new MmPay(context);
            }
        }
        return INSTANCE;
    }

    public void init(String appId) {
        this.appId = appId;
    }

    public void pay(Activity activity, int amount, String title, String desc) {
        PayTask payTask = new PayTask(activity);
        String orderInfo = getOrderInfo(amount, title, desc);
        System.out.println(orderInfo);
    }

    private String getOrderInfo(int amount, String title, String desc) {
        String outTradeNo = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(CommonUrl.PREPAY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            StringBuilder builder = new StringBuilder();
            builder.append("appId=").append(appId).append("&");
            builder.append("amount=").append(amount).append("&");
            if (null != title) {
                builder.append("title=").append(title);
            }
            if (null != desc) {
                builder.append("desc=").append(desc);
            }
            out.writeBytes(builder.toString());
            out.flush();
            out.close();
            if (HttpURLConnection.HTTP_OK != connection.getResponseCode()) {
                return null;
            }
            StringBuilder response = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = responseReader.readLine()) != null) {
                response.append(line).append("\n");
            }
            responseReader.close();
            Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "getOutTradeNo: ", e);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
        return outTradeNo;
    }

}

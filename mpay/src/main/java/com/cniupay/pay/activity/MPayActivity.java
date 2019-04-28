package com.cniupay.pay.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.cniupay.pay.CNiuPay;
import com.cniupay.pay.common.CommonResp;
import com.cniupay.pay.common.CommonUrl;
import com.cniupay.pay.common.PayResult;
import com.cniupay.pay.common.PrepayResult;
import com.cniupay.pay.dialog.MmPayConfirmDialog;
import com.cniupay.pay.dialog.MmPayLoadingDialog;
import com.cniupay.pay.enums.PayResultCodeEnum;
import com.cniupay.pay.enums.PayTypeEnum;
import com.cniupay.pay.util.GsonUtil;
import com.google.gson.JsonObject;
import com.zhuzhuodong.tool.android.mmpay.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MPayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MPayActivity";

    private static DecimalFormat AMOUNT_FORMAT = new DecimalFormat("0.00");

    private Context context;
    private Activity activity;
    private RadioButton alipayRadioBtn;
    private RadioButton wechatRadioBtn;
    private TextView mpayAmountShow;
    private TextView mpaySubject;
    private long amount;
    private String title;
    private String outTradeNo;
    private RelativeLayout mmpayConfirmLayout;
    private String tradeNo;
    private MmPayLoadingDialog dialog;

    private Toolbar cniuToolbar;

    private Handler mmpayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    activity.finish();
                    CNiuPay.doFinished(PayResultCodeEnum.SUCCESS, "Success", (PayResult) msg.obj);
                    break;
                case 1:
                    activity.finish();
                    CNiuPay.doFinished(PayResultCodeEnum.FAILURE, "Failure", null);
                    break;
                case 2:
                    Toast.makeText(context, null != msg.obj ? msg.obj.toString() : "系统异常，请重试",
                            Toast.LENGTH_SHORT).show();
                    mmpayConfirmLayout.setEnabled(true);
                    break;
                case 3:
                    doQueryPay();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amount = getIntent().getLongExtra("amount", 0);
        title = getIntent().getStringExtra("title");
        outTradeNo = getIntent().getStringExtra("outTradeNo");
        if (null == title) {
            title = "默认商品";
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mpay);
        cniuToolbar = findViewById(R.id.cniuToolbar);
        setSupportActionBar(cniuToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        activity = this;
        initComponents();
        setTitle("收银台");
    }

    private void setTransparentStatusBar() {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = this.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            this.getWindow().setStatusBarColor(Color.parseColor("#ff6666"));
            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = this.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    private void initComponents() {
        alipayRadioBtn = (RadioButton) findViewById(R.id.mmpay_wechat_radio);
        wechatRadioBtn = (RadioButton) findViewById(R.id.mmpay_wechat_radio);
        mpayAmountShow = (TextView) findViewById(R.id.mpayAmountShow);
        mpayAmountShow.setText("￥" + AMOUNT_FORMAT.format((double) amount / 100));
        mpaySubject = (TextView) findViewById(R.id.mpaySubject);
        mpaySubject.setText(title);
        mmpayConfirmLayout = (RelativeLayout) findViewById(R.id.mmpayConfirmLayout);
        mmpayConfirmLayout.setOnClickListener(this);
        dialog = new MmPayLoadingDialog(context);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mmpayConfirmLayout) {
            dialog.show();
            if (amount <= 0) {
                Toast.makeText(context, "金额非法，请联系开发者处理", Toast.LENGTH_SHORT).show();
                return;
            }
            mmpayConfirmLayout.setEnabled(false);
            doPay();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doPay() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PayTask payTask = new PayTask((Activity) context);
                CommonResp resp = getOrderInfo(amount, title, CNiuPay.getSecretAppKey());
                dialog.dismiss();
                if (null == resp || !resp.getCode().equals(0)) {
                    Message message = new Message();
                    message.what = 2;
                    if (null != resp) {
                        message.obj = resp.getMsg();
                    }
                    mmpayHandler.sendMessage(message);
                    return;
                }
                PrepayResult prepayResult = GsonUtil.GSON.fromJson(resp.getData(),
                        PrepayResult.class);
                tradeNo = prepayResult.getTradeNo();
                Map<String, String> result = payTask.payV2(prepayResult.getOrderInfo(), true);
                Log.i(TAG, "pay result: " + result);
                Integer resultStatus = Integer.valueOf(result.get("resultStatus"));
                if (null != resultStatus && resultStatus.equals(9000)) {
                    JsonObject resultTemp = GsonUtil.GSON.fromJson(result.get("result"), JsonObject.class);
                    JsonObject resultData = resultTemp.getAsJsonObject("alipay_trade_app_pay_response");
                    PayResult resultVo = new PayResult();
                    resultVo.setTradeNo(tradeNo);
                    resultVo.setAmount((long) (resultData.get("total_amount").getAsDouble() * 100));
                    resultVo.setRealTradeNo(resultData.get("trade_no").getAsString());
                    resultVo.setStatus(1);
                    resultVo.setPayType(PayTypeEnum.ZHIFUBAO.getCode());
                    sendSuccess(resultVo);
                } else {
                    Message message = new Message();
                    message.what = 3;
                    mmpayHandler.sendMessage(message);
                }
            }
        };
        new Thread(runnable).start();
    }

    private void doQueryPay() {
        final MmPayConfirmDialog confirmDialog = new MmPayConfirmDialog(context,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendFailre();
                    }
                }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        queryPay();
                    }
                };
                new Thread(runnable).start();
            }
        });
        confirmDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = confirmDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.9); //设置宽度
        confirmDialog.getWindow().setAttributes(lp);
    }

    private void queryPay() {
        CommonResp resp = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(CommonUrl.QUERY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            StringBuilder builder = new StringBuilder();
            builder.append("appKey=").append(URLEncoder.encode(CNiuPay.getSecretAppKey(), "utf-8")).append("&");
            builder.append("tradeNo=").append(URLEncoder.encode(tradeNo, "utf-8"));
            out.writeBytes(builder.toString());
            out.flush();
            out.close();
            if (HttpURLConnection.HTTP_OK != connection.getResponseCode()) {
                Message message = new Message();
                message.what = 1;
                mmpayHandler.sendMessage(message);
            }
            StringBuilder response = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = responseReader.readLine()) != null) {
                response.append(line).append("\n");
            }
            responseReader.close();
            resp = GsonUtil.GSON.fromJson(response.toString(), CommonResp.class);
        } catch (Exception e) {
            Log.e(TAG, "queryPay: ", e);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
        if (null != resp && 0 == resp.getCode()) {
            if (null == resp.getData()) {
                sendFailre();
                return;
            }
            try {
                PayResult result = GsonUtil.GSON.fromJson(resp.getData(), PayResult.class);
                if (result.getStatus() == 1) {
                    sendSuccess(result);
                } else {
                    sendFailre();
                }
            } catch (Exception e) {
                Log.e(TAG, "queryPay: ", e);
                sendFailre();
            }
        } else {
            sendFailre();
        }

    }

    private void sendSuccess(PayResult result) {
        Message message = new Message();
        message.what = 0;
        message.obj = result;
        mmpayHandler.sendMessage(message);
    }

    private void sendFailre() {
        Message message = new Message();
        message.what = 1;
        mmpayHandler.sendMessage(message);
    }

    private CommonResp getOrderInfo(long amount, String title, String desc) {
        CommonResp resp = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(CommonUrl.PREPAY_ALIPAY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            StringBuilder builder = new StringBuilder();
            builder.append("appKey=").append(URLEncoder.encode(CNiuPay.getSecretAppKey(), "utf-8")).append("&");
            builder.append("amount=").append(URLEncoder.encode(String.valueOf(amount),
                    "utf-8")).append("&");
            if (null != title) {
                builder.append("title=").append(URLEncoder.encode(title, "utf-8")).append("&");
            }
            if (null != desc) {
                builder.append("desc=").append(URLEncoder.encode(desc, "utf-8")).append("&");
            }
            if (null != outTradeNo) {
                builder.append("outTradeNo=").append(URLEncoder.encode(outTradeNo, "utf-8"));
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
            resp = GsonUtil.GSON.fromJson(response.toString(), CommonResp.class);
        } catch (Exception e) {
            Log.e(TAG, "getOutTradeNo: ", e);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
        return resp;
    }

}

package com.zhuzhuodong.tool.android.mmpay.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.zhuzhuodong.tool.android.mmpay.MmPay;
import com.zhuzhuodong.tool.android.mmpay.R;
import com.zhuzhuodong.tool.android.mmpay.common.CommonResp;
import com.zhuzhuodong.tool.android.mmpay.common.CommonUrl;
import com.zhuzhuodong.tool.android.mmpay.common.PrepayResult;
import com.zhuzhuodong.tool.android.mmpay.dialog.MmPayConfirmDialog;
import com.zhuzhuodong.tool.android.mmpay.dialog.MmPayLoadingDialog;
import com.zhuzhuodong.tool.android.mmpay.enums.MmPayResultCodeEnum;
import com.zhuzhuodong.tool.android.mmpay.util.GsonUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Map;

public class MPayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MPayActivity";

    private static DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#.##");

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

    private Handler mmpayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    activity.finish();
                    MmPay.doFinished(MmPayResultCodeEnum.SUCCESS, "Success", amount);
                    break;
                case 1:
                    activity.finish();
                    MmPay.doFinished(MmPayResultCodeEnum.FAILURE, "Failure", amount);
                    break;
                case 2:
                    Toast.makeText(context, "系统异常，请检查网络或稍后重试",
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
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6666")));
        setTransparentStatusBar();
        setContentView(R.layout.activity_mpay);
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
                CommonResp resp = getOrderInfo(amount, title, MmPay.getAppId());
                dialog.dismiss();
                if (null == resp || !resp.getCode().equals(0)) {
                    Message message = new Message();
                    message.what = 2;
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
                    sendSuccess();
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
            builder.append("appId=").append(URLEncoder.encode(MmPay.getAppId(), "utf-8")).append("&");
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
        if (null != resp && "0".equals(resp.getData())) {
            sendSuccess();
        } else {
            sendFailre();
        }

    }

    private void sendSuccess() {
        Message message = new Message();
        message.what = 0;
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
            builder.append("appId=").append(URLEncoder.encode(MmPay.getAppId(), "utf-8")).append("&");
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

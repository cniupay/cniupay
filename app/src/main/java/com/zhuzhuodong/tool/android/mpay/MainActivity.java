package com.zhuzhuodong.tool.android.mpay;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cniupay.pay.CNiuPay;
import com.cniupay.pay.enums.PayResultCodeEnum;
import com.cniupay.pay.listener.PayResultListener;

public class MainActivity extends AppCompatActivity {

    private Button testPay;
    private Context context;
    private Activity activity;

    private EditText subject;
    private EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;
        testPay = (Button) findViewById(R.id.testPay);
        subject = (EditText) findViewById(R.id.subject);
        amount = (EditText) findViewById(R.id.amount);
        testPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectStr = subject.getText().toString();
                if (subjectStr.equals("")) {
                    Toast.makeText(context, "标题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String amountStr = amount.getText().toString();
                if (amountStr.equals("")) {
                    Toast.makeText(context, "金额不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer amountValue = Integer.valueOf(amountStr);
                if (amountValue <= 0) {
                    Toast.makeText(context, "金额必须大于0", Toast.LENGTH_SHORT).show();
                    return;
                }
                CNiuPay.getInstance(context).pay(amountValue, subjectStr, null, new PayResultListener() {
                    @Override
                    public void onPayFinished(Context context, PayResultCodeEnum payResult,
                                              String resultMsg, long amount) {
                        Toast.makeText(context, "Final:" + payResult.getCode(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


}

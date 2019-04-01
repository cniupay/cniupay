package com.cniupay.pay.listener;

import android.content.Context;

import com.cniupay.pay.enums.PayResultCodeEnum;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public interface PayResultListener {

    /**
     * 支付完成
     *
     * @param context   传入上下文
     * @param payResult 支付结果
     * @param resultMsg 结果描述
     * @param amount    支付金额
     */
    void onPayFinished(Context context, PayResultCodeEnum payResult, String resultMsg, long amount);

}

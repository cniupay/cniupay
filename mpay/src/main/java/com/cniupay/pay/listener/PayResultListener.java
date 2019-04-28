package com.cniupay.pay.listener;

import android.content.Context;

import com.cniupay.pay.common.PayResult;
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
     * @param data      结果数据（payResult=SUCCESS才有数据）
     */
    void onPayFinished(Context context, PayResultCodeEnum payResult, String resultMsg, PayResult data);

}

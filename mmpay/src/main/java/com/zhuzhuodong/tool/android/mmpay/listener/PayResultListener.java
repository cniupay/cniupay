package com.zhuzhuodong.tool.android.mmpay.listener;

import android.content.Context;

import com.zhuzhuodong.tool.android.mmpay.enums.MmPayResultCodeEnum;

/**
 * Created by zhuzhuodong on 2018/7/15.
 */

public interface PayResultListener {

    /**
     * 支付完成
     *
     * @param payResult
     * @param resultMsg 失败时返回错误信息
     */
    void onPayFinished(MmPayResultCodeEnum payResult, String resultMsg, long amount);

}

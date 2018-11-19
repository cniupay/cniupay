package com.zhuzhuodong.tool.android.mmpay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.wang.avi.AVLoadingIndicatorView;
import com.zhuzhuodong.tool.android.mmpay.R;

/**
 * Created by zhuzhuodong on 2018/7/19.
 */

public class MmPayLoadingDialog extends Dialog {

    private Context context;

    private AVLoadingIndicatorView mmpay_loading_view;

    public MmPayLoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_mmpay_loading, null);
        setContentView(view);
        mmpay_loading_view = (AVLoadingIndicatorView) view.findViewById(R.id.mmpay_loading_view);
        mmpay_loading_view.show();
    }
}

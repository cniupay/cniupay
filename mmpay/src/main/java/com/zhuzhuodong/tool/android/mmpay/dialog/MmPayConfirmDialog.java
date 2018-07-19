package com.zhuzhuodong.tool.android.mmpay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.zhuzhuodong.tool.android.mmpay.R;

/**
 * Created by zhuzhuodong on 2018/7/17.
 */

public class MmPayConfirmDialog extends Dialog {


    private Context context;
    private LinearLayout mmpayUnpayLayout;
    private LinearLayout mmpayPayLayout;
    private DialogInterface dialogInterface;

    private DialogInterface.OnClickListener failure;
    private DialogInterface.OnClickListener success;

    public MmPayConfirmDialog(@NonNull Context context, DialogInterface.OnClickListener failure,
                              DialogInterface.OnClickListener success) {
        super(context);
        this.context = context;
        this.failure = failure;
        this.success = success;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_mmpay_confirm, null);
        setContentView(view);
        dialogInterface = this;
        mmpayUnpayLayout = (LinearLayout) view.findViewById(R.id.mmpayUnpayLayout);
        mmpayUnpayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failure.onClick(dialogInterface, 1);
            }
        });
        mmpayPayLayout = (LinearLayout) view.findViewById(R.id.mmpayPayLayout);
        mmpayPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                success.onClick(dialogInterface, 0);
            }
        });
    }
}

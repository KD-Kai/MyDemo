package com.desaysv.dsvdialogdemo.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.desaysv.dsvdialogdemo.R;

public class DialogContent extends BaseDialogView{

    @Override
    protected int onBindLayoutId(int style) {
        return R.layout.view_dialog_content;
    }

    @Override
    protected void onViewCreated(View view) {

    }
}

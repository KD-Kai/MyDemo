package com.desaysv.dsvdialogdemo.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseDialogView {
    protected String TAG = this.getClass().getSimpleName();

    protected Context mContext;
    protected ViewGroup mRoot;
    protected View mView;
    protected int mDialogStyle;

    public void createView(Context context, ViewGroup root, int style) {
        mContext = context;
        mRoot = root;
        mDialogStyle = style;
        mView = LayoutInflater.from(context).inflate(onBindLayoutId(style), root, false);
        onViewCreated(mView);
    }

    protected abstract int onBindLayoutId(int style);

    protected abstract void onViewCreated(View view);

    public View getView() {
        return mView;
    }
}

package com.desaysv.dsvdialogdemo.dialog;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desaysv.dsvdialogdemo.DsvDialog;
import com.desaysv.dsvdialogdemo.R;

public class DialogButton extends BaseDialogView{

    private LinearLayout mButtonView;
    private TextView mLeftButton;
    private TextView mRightButton;

    public int mButtonWidth = DialogParam.BUTTON_SIZE_M_WIDTH;
    public int mButtonHeight = DialogParam.BUTTON_SIZE_M_HEIGHT;
    public int mButtonMargin = DialogParam.BUTTON_SIZE_M_MARGIN;

    public int mLeftVisible = View.VISIBLE;
    public int mRightVisible = View.VISIBLE;

    private String mLeftText = "确定";
    private String mRightText = "取消";

    @Override
    protected int onBindLayoutId(int style) {
        if ((mDialogStyle & DsvDialog.STYLE_DIALOG_BUTTON_DOUBLE) != 0) {
            switch (mDialogStyle &0x0F) {
                case DsvDialog.STYLE_DIALOG_SIZE_S:
                    mButtonWidth = DialogParam.BUTTON_SIZE_S_WIDTH;
                    mButtonHeight = DialogParam.BUTTON_SIZE_S_HEIGHT;
                    mButtonMargin = DialogParam.BUTTON_SIZE_S_MARGIN;
                    break;
                case DsvDialog.STYLE_DIALOG_SIZE_M:
                    mButtonWidth = DialogParam.BUTTON_SIZE_M_WIDTH;
                    mButtonHeight = DialogParam.BUTTON_SIZE_M_HEIGHT;
                    mButtonMargin = DialogParam.BUTTON_SIZE_M_MARGIN;
                    break;
                case DsvDialog.STYLE_DIALOG_SIZE_L:
                    mButtonWidth = DialogParam.BUTTON_SIZE_L_WIDTH;
                    mButtonHeight = DialogParam.BUTTON_SIZE_L_HEIGHT;
                    mButtonMargin = DialogParam.BUTTON_SIZE_L_MARGIN;
                    break;
                case DsvDialog.STYLE_DIALOG_SIZE_XL:
                    mButtonWidth = DialogParam.BUTTON_SIZE_XL_WIDTH;
                    mButtonHeight = DialogParam.BUTTON_SIZE_XL_HEIGHT;
                    mButtonMargin = DialogParam.BUTTON_SIZE_XL_MARGIN;
                    break;
            }
            mLeftVisible = View.VISIBLE;
            mRightVisible = View.VISIBLE;
        } else if ((mDialogStyle & DsvDialog.STYLE_DIALOG_BUTTON_SINGLE) != 0) {
            mLeftVisible = View.VISIBLE;
            mRightVisible = View.GONE;
        } else if ((mDialogStyle & DsvDialog.STYLE_DIALOG_BUTTON_NULL) != 0) {
            mLeftVisible = View.GONE;
            mRightVisible = View.GONE;
        }
        return R.layout.view_dialog_button;
    }

    @Override
    protected void onViewCreated(View view) {
        mButtonView = view.findViewById(R.id.dialog_button_view);
        mLeftButton = view.findViewById(R.id.dialog_button_left);
        mRightButton = view.findViewById(R.id.dialog_button_right);
    }

    public void show() {
        setButtonParam();
        setButtonText();
        setButtonVisible();
    }

    private void setButtonParam() {
        LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) mLeftButton.getLayoutParams();
        leftParams.width = mButtonWidth;
        leftParams.height = mButtonHeight;
        mLeftButton.setLayoutParams(leftParams);

        LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) mRightButton.getLayoutParams();
        rightParams.width = mButtonWidth;
        rightParams.height = mButtonHeight;
        rightParams.setMarginStart(mButtonMargin);
        mRightButton.setLayoutParams(rightParams);
    }

    private void setButtonText() {
        mLeftButton.setText(mLeftText);
        mRightButton.setText(mRightText);
    }

    private void setButtonVisible() {
        mLeftButton.setVisibility(mLeftVisible);
        mRightButton.setVisibility(mRightVisible);
        if (mLeftVisible == View.GONE && mRightVisible == View.GONE) {
            mButtonView.setVisibility(View.GONE);
        } else {
            mButtonView.setVisibility(View.VISIBLE);
        }
    }

    public void setLeftClickListener(View.OnClickListener mLeftClickListener) {
        if (mLeftButton != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        }
    }

    public void setRightClickListener(View.OnClickListener mRightClickListener) {
        if (mRightButton != null) {
            mRightButton.setOnClickListener(mRightClickListener);
        }
    }

    public void setButtonWidth(int mButtonWidth) {
        this.mButtonWidth = mButtonWidth;
    }

    public void setButtonHeight(int mButtonHeight) {
        this.mButtonHeight = mButtonHeight;
    }

    public void setButtonMargin(int mButtonMargin) {
        this.mButtonMargin = mButtonMargin;
    }

    public void setLeftText(String mLeftText) {
        this.mLeftText = mLeftText;
    }

    public void setRightText(String mRightText) {
        this.mRightText = mRightText;
    }

    public void setLeftVisible(int mLeftVisible) {
        this.mLeftVisible = mLeftVisible;
    }

    public void setRightVisible(int mRightVisible) {
        this.mRightVisible = mRightVisible;
    }
}
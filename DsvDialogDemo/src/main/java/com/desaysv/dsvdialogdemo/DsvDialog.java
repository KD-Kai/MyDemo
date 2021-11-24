package com.desaysv.dsvdialogdemo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.desaysv.dsvdialogdemo.dialog.DialogButton;
import com.desaysv.dsvdialogdemo.dialog.DialogContent;
import com.desaysv.dsvdialogdemo.dialog.DialogParam;


public class DsvDialog extends Dialog {
    private static final String TAG = "DsvDialog";

    public static final int STYLE_DIALOG_SIZE_S = 0x1;
    public static final int STYLE_DIALOG_SIZE_M = 0x2;
    public static final int STYLE_DIALOG_SIZE_L = 0x4;
    public static final int STYLE_DIALOG_SIZE_XL = 0x8;

    public static final int STYLE_DIALOG_BUTTON_NULL = 0x10;
    public static final int STYLE_DIALOG_BUTTON_SINGLE = 0x20;
    public static final int STYLE_DIALOG_BUTTON_DOUBLE = 0x40;
    public static final int STYLE_DIALOG_BUTTON_SPECIAL = 0x80;

    private final Context mContext;
    private final DialogButton mDialogButton;
    private final DialogContent mDialogContent;

    private int mDialogStyle = STYLE_DIALOG_SIZE_S | STYLE_DIALOG_BUTTON_DOUBLE;
    private int mDialogWidth = DialogParam.DIALOG_SIZE_S_WIDTH;
    private int mDialogHeight = DialogParam.DIALOG_SIZE_S_HEIGHT;

    public DsvDialog(@NonNull Context context) {
        super(context, R.style.Theme_DsvDialog);
        mContext = context;
        mDialogButton = new DialogButton();
        mDialogContent = new DialogContent();
    }

    @Override
    public void show() {
        Log.d(TAG, "show: width = " + mDialogWidth + "; height = " + mDialogHeight);
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_dialog_main, null);
        ImageView imageView = view.findViewById(R.id.dialog_background);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = mDialogWidth;
        layoutParams.height = mDialogHeight;
        imageView.setLayoutParams(layoutParams);

        mDialogButton.createView(mContext, view, mDialogStyle);
        mDialogButton.show();
        mDialogContent.createView(mContext, view, mDialogStyle);

        view.addView(mDialogButton.getView());
        view.addView(mDialogContent.getView());
        setContentView(view);
        super.show();
    }

    public void setDialogStyle(int style) {
        mDialogStyle = style;
        getDialogSize(style);
    }

    private void getDialogSize(int style) {
        Log.d(TAG, String.format("getDialogWidth: style = %x", style));
        switch (mDialogStyle&0x0F) {
            case STYLE_DIALOG_SIZE_S:
                mDialogWidth = DialogParam.DIALOG_SIZE_S_WIDTH;
                mDialogHeight = DialogParam.DIALOG_SIZE_S_HEIGHT;
                break;
            case STYLE_DIALOG_SIZE_M:
                mDialogWidth = DialogParam.DIALOG_SIZE_M_WIDTH;
                mDialogHeight = DialogParam.DIALOG_SIZE_M_HEIGHT;
                break;
            case STYLE_DIALOG_SIZE_L:
                mDialogWidth = DialogParam.DIALOG_SIZE_L_WIDTH;
                mDialogHeight = DialogParam.DIALOG_SIZE_L_HEIGHT;
                break;
            case STYLE_DIALOG_SIZE_XL:
                mDialogWidth = DialogParam.DIALOG_SIZE_XL_WIDTH;
                mDialogHeight = DialogParam.DIALOG_SIZE_XL_HEIGHT;
                break;
        }
    }

    public void setSystemDialog(boolean isSystemDialog) {
        if (isSystemDialog) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void setDialogSize(int width, int height) {
        mDialogWidth = width;
        mDialogHeight = height;
    }

    public void setButtonSize(int width, int height) {
        if (mDialogButton != null) {
            mDialogButton.setButtonWidth(width);
            mDialogButton.setButtonHeight(height);
        }
    }

    public void setButtonMargin(int margin) {
        if (mDialogButton != null) {
            mDialogButton.setButtonMargin(margin);
        }
    }

    public void setLeftButtonText(String text) {
        if (mDialogButton != null) {
            mDialogButton.setLeftText(text);
        }
    }

    public void setRightButtonText(String text) {
        if (mDialogButton != null) {
            mDialogButton.setRightText(text);
        }
    }

    public void setLeftButtonVisible(int visible) {
        if (mDialogButton != null) {
            mDialogButton.setLeftVisible(visible);
        }
    }

    public void setRightButtonVisible(int visible) {
        if (mDialogButton != null) {
            mDialogButton.setRightVisible(visible);
        }
    }

    public void setLeftButtonClickListener(View.OnClickListener listener) {
        if (mDialogButton != null) {
            mDialogButton.setLeftClickListener(listener);
        }
    }

    public void setRightButtonClickListener(View.OnClickListener listener) {
        if (mDialogButton != null) {
            mDialogButton.setRightClickListener(listener);
        }
    }
}

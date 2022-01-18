package com.desaysv.dsvlightdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DsvLightDemo";
    private EditText mEditText;
    private Button mConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.input_brightness);
        mEditText.setText(String.valueOf(getWindow().getAttributes().screenBrightness));
        mConfirmBtn = findViewById(R.id.confirm_brightness);
        mEditText.setOnEditorActionListener(mOnEditorActionListener);
        mConfirmBtn.setOnClickListener(mOnClickListener);
    }

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            float input = getEditInput();
            Log.d(TAG, "mOnEditorActionListener: input = " + input);
            return true;  //回车禁止换行;
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float input = getEditInput();
            Log.d(TAG, "mOnClickListener: input = " + input);
            setWindowBrightness(input);
        }
    };

    private float getEditInput() {
        float result = 0;
        String input = null;
        Editable editable = mEditText.getText();
        if (editable != null) {
            input = editable.toString();
        }
        try {
            if (input != null) {
                result = Float.parseFloat(input);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setBrightness(int brightness) {
        Log.d(TAG, "setBrightness: start --> " + System.currentTimeMillis());
        try {
            IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
            if (power != null) {
                power.setTemporaryScreenBrightnessSettingOverride(brightness);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setBrightness: done --> " + System.currentTimeMillis());
    }

    private void setWindowBrightness(float brightness) {
        Log.d(TAG, "setWindowBrightness: start --> " + System.currentTimeMillis());
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.screenBrightness = brightness;
            window.setAttributes(lp);
        }
        Log.d(TAG, "setWindowBrightness: start --> " + System.currentTimeMillis());
    }
}
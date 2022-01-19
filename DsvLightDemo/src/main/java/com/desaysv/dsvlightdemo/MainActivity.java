package com.desaysv.dsvlightdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DsvLightDemo";
    private SeekBar mSeekbar;
    private Button mReturn;
    private Button mNextTest;
    private int mBrightness = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSeekbar = findViewById(R.id.brightness_seek_bar);
        mSeekbar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mNextTest = findViewById(R.id.activity_next);
        mNextTest.setOnClickListener(mOnClickListener);

        mReturn = findViewById(R.id.activity_return);
        mReturn.setOnClickListener(mOnClickListener);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mBrightness != progress/100) {
                mBrightness = progress/100;
                BrightnessProxy.setLocalBrightness(MainActivity.this, mBrightness);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mReturn) {
                finish();
            } else if (v == mNextTest) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    };
}
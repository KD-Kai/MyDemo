package com.desaysv.dsvlightdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class LocalActivity extends AppCompatActivity {

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
                BrightnessProxy.setLocalBrightness(LocalActivity.this, mBrightness);
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
                Intent intent = new Intent(LocalActivity.this, LocalActivity.class);
                startActivity(intent);
            }
        }
    };
}
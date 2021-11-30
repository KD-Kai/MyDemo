package com.desaysv.dsvrecorderdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.desaysv.dsvrecorderdemo.demo.MediaPlayerDemo;
import com.desaysv.dsvrecorderdemo.demo.MediaRecorderDemo;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DsvRecorder";

    private MediaRecorderDemo mMediaRecorderDemo;
    private MediaPlayerDemo mMediaPlayerDemo;

    private Button mRecordBtn;
    private Button mPlayerBtn;
    private TextView mHintText;

    private boolean mIsRecording = false;
    private boolean mIsPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        mMediaRecorderDemo = new MediaRecorderDemo();
        mMediaRecorderDemo.setOnRecorderStatusListener(onRecorderStatusListener);
        mMediaPlayerDemo = new MediaPlayerDemo(this);
        mMediaPlayerDemo.setOnPlayerStatusListener(onPlayerStatusListener);

        mRecordBtn = findViewById(R.id.media_record_btn);
        mPlayerBtn = findViewById(R.id.media_player_btn);
        mRecordBtn.setOnClickListener(onClickListener);
        mPlayerBtn.setOnClickListener(onClickListener);

        mHintText = findViewById(R.id.media_hint_tv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaRecorderDemo.destroy();
        mMediaPlayerDemo.destroy();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mRecordBtn) {
                if (mRecordBtn.getText().toString().contains("START")) {
                    if (mIsPlaying) {
                        updateHintText("请先结束播放");
                        return;
                    }
                    mMediaRecorderDemo.startRecord();
                }else {
                    mMediaRecorderDemo.stopRecord();
                }

            }else if (v == mPlayerBtn) {
                if (mPlayerBtn.getText().toString().contains("START")) {
                    if (mIsRecording) {
                        updateHintText("请先结束录音");
                        return;
                    }
                    mMediaPlayerDemo.startPlay();
                }else {
                    mMediaPlayerDemo.stopPlay();
                }
            }
        }
    };

    private MediaRecorderDemo.OnRecorderStatusListener onRecorderStatusListener = new MediaRecorderDemo.OnRecorderStatusListener() {
        @Override
        public void isRecording(boolean isRecording) {
            Log.d(TAG, "onRecorderStatusListener: isRecording = " + isRecording);
            mIsRecording = isRecording;
            mRecordBtn.post(new Runnable() {
                @Override
                public void run() {
                    if (isRecording) {
                        updateHintText("正在录音");
                        mRecordBtn.setText("STOP RECORD");
                    }else {
                        updateHintText("已将录音保存至：/sdcard/test.mp3");
                        mRecordBtn.setText("START RECORD");
                    }
                }
            });
        }
    };

    private MediaPlayerDemo.OnPlayerStatusListener onPlayerStatusListener = new MediaPlayerDemo.OnPlayerStatusListener() {
        @Override
        public void isPlaying(boolean isPlaying) {
            Log.d(TAG, "onPlayerStatusListener: isPlaying = " + isPlaying);
            mIsPlaying = isPlaying;
            mPlayerBtn.post(new Runnable() {
                @Override
                public void run() {
                    if (isPlaying) {
                        updateHintText("正在播放：/sdcard/test.mp3");
                        mPlayerBtn.setText("STOP PLAY");
                    }else {
                        updateHintText("结束播放：/sdcard/test.mp3");
                        mPlayerBtn.setText("START PLAY");
                    }
                }
            });
        }
    };

    private void updateHintText(String text) {
        mHintText.post(new Runnable() {
            @Override
            public void run() {
                mHintText.setText(text);
            }
        });
    }
    /**
     * 权限申请
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
    }
}
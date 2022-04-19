package com.desaysv.dsvvolumedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.car.Car;
import android.car.media.CarAudioManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Car mCar = Car.createCar(this);
        CarAudioManager mCarAudioManager = (CarAudioManager) mCar.getCarManager(Car.AUDIO_SERVICE);
        Log.d(TAG, "MainActivity.onCreate");

        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Log.d(TAG, "MainActivity.onCreate: getStreamVolume = " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        Log.d(TAG, "MainActivity.onCreate: getStreamVolume = " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

//        mCarAudioManager.setCurGroupVolume(30);
//        Log.d(TAG, "MainActivity.onCreate: getStreamVolume = " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }
}
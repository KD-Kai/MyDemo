package com.desaysv.dsvvolumedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.car.Car;
import android.car.media.CarAudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "VolumeDemo";

    private CarAudioManager mCarAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!querySoundEffectsEnabled()) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SOUND_EFFECTS_ENABLED, 1);
        }

        init();

    }

    private void init() {
        Car.createCar(this, /* handler= */ null, Car.CAR_WAIT_TIMEOUT_DO_NOT_WAIT, (car, ready) -> {
            if (ready) {
                mCarAudioManager = (CarAudioManager)car.getCarManager(Car.AUDIO_SERVICE);
                mCarAudioManager.registerCarVolumeCallback(mVolumeChangeCallback);
            }
        });
    }

    private final CarAudioManager.CarVolumeCallback mVolumeChangeCallback = new CarAudioManager.CarVolumeCallback() {
        @Override
        public void onGroupVolumeChanged(int zoneId, int groupId, int flags) {
            Log.d(TAG, "onGroupVolumeChanged: groupId = " + groupId);
            if (mCarAudioManager != null) {
                int volume = mCarAudioManager.getGroupVolume(groupId);
                Log.d(TAG, "onGroupVolumeChanged volume =" + volume);
            }
        }

        @Override
        public void onMasterMuteChanged(int zoneId, int flags) {
            if (mCarAudioManager != null) {
                boolean mute = mCarAudioManager.isGroupMute(0);
                Log.d(TAG, "onMasterMuteChanged mute =" + mute);
            }
        }
    };

    private boolean querySoundEffectsEnabled() {
        try {
            return Settings.System.getInt(getContentResolver(),
                    Settings.System.SOUND_EFFECTS_ENABLED) != 0;
        } catch (Settings.SettingNotFoundException e) {

        }
        return false;
    }
}
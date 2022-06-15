package com.desaysv.dsvaudiodemo;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.desaysv.dsvaudiodemo.fragment.MediaDemoFragment;
import com.desaysv.dsvaudiodemo.fragment.NaviDemoFragment;
import com.desaysv.dsvaudiodemo.fragment.PhoneDemoFragment;
import com.desaysv.dsvaudiodemo.fragment.RingDemoFragment;
import com.desaysv.dsvaudiodemo.fragment.VoiceDemoFragment;
import com.desaysv.ivi.platformadapter.app.audio.SvCarAudioManager;

public class MainActivity extends AppCompatActivity {

    private String[] mFragmentName = {
            MediaDemoFragment.class.getName(),
            NaviDemoFragment.class.getName(),
            PhoneDemoFragment.class.getName(),
            VoiceDemoFragment.class.getName(),
            RingDemoFragment.class.getName()
    };

    private static final int DEMO_MEDIA = 0;
    private static final int DEMO_NAVI = 1;
    private static final int DEMO_PHONE = 2;
    private static final int DEMO_VOICE = 3;
    private static final int DEMO_RING = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragment(mFragmentName[DEMO_MEDIA], R.id.fragment_audio_media);
        setFragment(mFragmentName[DEMO_NAVI], R.id.fragment_audio_navi);
        setFragment(mFragmentName[DEMO_PHONE], R.id.fragment_audio_phone);
        setFragment(mFragmentName[DEMO_VOICE], R.id.fragment_audio_voice);
        setFragment(mFragmentName[DEMO_RING], R.id.fragment_audio_ring);
        getPermission();

        SvCarAudioManager.get(this).registerSourceListener(new SvCarAudioManager.OnSourceChangeListener() {
            @Override
            public void onSourceChange(String s) {
                Log.d("AudioDemo", "onSourceChange: source = " + s);
            }
        });
    }

    private void setFragment(String fragmentName, @IdRes int resId) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = Fragment.instantiate(this, fragmentName, null);
        transaction.replace(resId, fragment, fragmentName).commitAllowingStateLoss();
    }

    private void getPermission(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        }
    }
}
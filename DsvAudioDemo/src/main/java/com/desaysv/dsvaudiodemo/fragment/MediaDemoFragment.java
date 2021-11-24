package com.desaysv.dsvaudiodemo.fragment;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.desaysv.dsvaudiodemo.util.SourceUtil;
//import com.desaysv.ivi.platformadapter.app.audio.SvCarAudioManager;


import java.util.HashMap;

import desaysv.adapter.app.audio.SvCarAudioManager;

public class MediaDemoFragment extends BaseFragment {

    private AudioManager mAudioManager;
    //private AudioAttributes mAudioAttributes;
    private int mTargetFocus;

    private static final String ACTIVITY_NAME = "NA";
    private static final String SERVICE_NAME = "NA";

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        initAudioStatus();
        initAudioAttr();
    }
    private void initAudioStatus() {
        mTargetFocus = AudioManager.AUDIOFOCUS_GAIN;
        mAudioFocus = AudioManager.AUDIOFOCUS_NONE;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    private void initAudioAttr() {
        Log.d(TAG, "initAudioAttr");
        HashMap<String, Object> hashMap = new HashMap<>();
        if (mAudioAttributes != null) {
            return;
        }
        hashMap.put(SvCarAudioManager.KEY_CAR_AUDIO_TYPE, SvCarAudioManager.CAR_AUDIO_TYPE_USB_0);
        hashMap.put(SvCarAudioManager.KEY_CLASS_NAME_SERVICE, SERVICE_NAME);
        hashMap.put(SvCarAudioManager.KEY_CLASS_NAME_ACTIVITY, ACTIVITY_NAME);
        hashMap.put(SvCarAudioManager.KEY_SUPPORT_SV_EXTEND_FOCUS_STATE, true);
        hashMap.put(SvCarAudioManager.KEY_BOOT_RESUME, 1);//设置是否恢复音源
        hashMap.put(SvCarAudioManager.KEY_BOOT_RESUME_TIME_OUT, 30000);//设置音源恢复超时设置
        AudioAttributes attributes = (new AudioAttributes.Builder()).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build();

        mAudioAttributes = SvCarAudioManager.setCarAttr(attributes, hashMap);
    }

    public boolean requestFocus() {
        Log.d(TAG, "requestFocus: mAudioFocus = " + mAudioFocus);
        if (mAudioFocus == AudioManager.AUDIOFOCUS_NONE) {
            int requestResult = SourceUtil.doRequestFocus(mAudioManager, onAudioFocusChangeListener,
                    mAudioAttributes, mTargetFocus, true);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == requestResult) {
                mAudioFocus = mTargetFocus;
                return true;
            }
        }
        return mTargetFocus == mAudioFocus;
    }

    public boolean abandonFocus() {
        if (mAudioFocus != AudioManager.AUDIOFOCUS_NONE) {
            int requestResult = SourceUtil.doAbandonAudioFocus(mAudioManager, onAudioFocusChangeListener,
                    mAudioAttributes, mTargetFocus);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == requestResult) {
                mAudioFocus = AudioManager.AUDIOFOCUS_NONE;
                return true;
            }
        }
        return AudioManager.AUDIOFOCUS_NONE == mAudioFocus;
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "onAudioFocusChange: focusChange = " + focusChange);
            mAudioFocus = focusChange;
        }
    };

    @Override
    protected boolean onRequestFocus() {
        return requestFocus();
    }

    @Override
    protected boolean onAbandonFocus() {
        return abandonFocus();
    }
}
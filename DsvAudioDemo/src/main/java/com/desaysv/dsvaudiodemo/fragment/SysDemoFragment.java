package com.desaysv.dsvaudiodemo.fragment;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.desaysv.dsvaudiodemo.util.SourceUtil;
import com.desaysv.ivi.platformadapter.app.audio.SvCarAudioManager;

import java.util.HashMap;

public class SysDemoFragment extends BaseFragment {

    private AudioManager mAudioManager;
    //private AudioAttributes mAudioAttributes;
    private final int mTargetFocus =  AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;

    private static final String ACTIVITY_NAME = "NA";
    private static final String SERVICE_NAME = "NA";

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container) {
        mAudioFocus = AudioManager.AUDIOFOCUS_NONE;
        initAudioAttr();
        initAudioStatus();
        super.init(inflater, container);
    }

    private void initAudioAttr() {
        Log.d(TAG, "initAudioAttr");
        HashMap<String, Object> hashMap = new HashMap<>();
        if (mAudioAttributes != null) {
            return;
        }
        AudioAttributes attributes = (new AudioAttributes.Builder())
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        mAudioAttributes = SvCarAudioManager.setCarAttr(attributes, hashMap);
    }

    private void initAudioStatus() {
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean requestFocus() {
        Log.d(TAG, "requestFocus: mAudioFocus = " + mAudioFocus);
        if (mAudioFocus == AudioManager.AUDIOFOCUS_NONE) {
            int requestResult = SourceUtil.doRequestFocus(mAudioManager, onAudioFocusChangeListener,
                    mAudioAttributes, mTargetFocus);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == requestResult) {
                mAudioFocus = mTargetFocus;
                return true;
            }
        }
        return mTargetFocus == mAudioFocus;
    }

    public boolean abandonFocus() {
        Log.d(TAG, "abandonFocus: mAudioFocus = " + mAudioFocus);
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
        return true;
    }

    @Override
    protected boolean onAbandonFocus() {
        return true;
    }
}
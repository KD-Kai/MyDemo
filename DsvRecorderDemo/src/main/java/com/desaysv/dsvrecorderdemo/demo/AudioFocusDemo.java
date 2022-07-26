package com.desaysv.dsvrecorderdemo.demo;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.util.Log;

import com.desaysv.ivi.platformadapter.app.audio.SvCarAudioManager;

import java.util.HashMap;


public class AudioFocusDemo {

    private static final String TAG = "AudioFocusDemo";

    private Context mContext;

    private AudioManager mAudioManager;
    private AudioAttributes mAudioAttributes;

    /**
     * 记录当前音频焦点状态的变化；
     */
    private int mAudioFocus;

    /**
     * 配置需要请求的音频焦点状态；
     */
    private final int mTargetFocus = AudioManager.AUDIOFOCUS_GAIN;

    /**
     * 配置音源所在的 Activity && Service
     * 1. ACTIVITY_NAME: 用于前台拉起音源界面（如：切源 or 开机恢复）
     * 2. SERVICE_NAME: 用于后台拉起音源（如：切源 or 开机恢复）
     */
    private static final String ACTIVITY_NAME = "NA";
    private static final String SERVICE_NAME = "NA";

    public AudioFocusDemo(Context context) {
        mContext = context;
        initAudioStatus();
        initAudioAttr();
    }

    private void initAudioStatus() {
        mAudioFocus = AudioManager.AUDIOFOCUS_NONE;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private void initAudioAttr() {
        Log.d(TAG, "initAudioAttr");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(SvCarAudioManager.KEY_CAR_AUDIO_TYPE, SvCarAudioManager.CAR_AUDIO_TYPE_USB_0);//配置音频焦点的Stream类型
        hashMap.put(SvCarAudioManager.KEY_SUPPORT_SV_EXTEND_FOCUS_STATE, true);

        hashMap.put(SvCarAudioManager.KEY_CLASS_NAME_ACTIVITY, ACTIVITY_NAME);//配置可拉起音源的Activity
        hashMap.put(SvCarAudioManager.KEY_CLASS_NAME_SERVICE, SERVICE_NAME);//配置可拉起音源的Service

        hashMap.put(SvCarAudioManager.KEY_BOOT_RESUME, 1);//根据需求设置是否需要恢复音源: 0-default，1-enable，2-disable
        hashMap.put(SvCarAudioManager.KEY_BOOT_RESUME_TIME_OUT, 30000);//设置音源恢复Timeout时间

        AudioAttributes attributes = (new AudioAttributes.Builder()).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build();

        mAudioAttributes = SvCarAudioManager.setCarAttr(attributes, hashMap);
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

    public static class SourceUtil {

        public static int doRequestFocus(
                AudioManager audioManager,
                AudioManager.OnAudioFocusChangeListener listener,
                int streamType,
                int androidFocus) {

            AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
            attributesBuilder.setLegacyStreamType(streamType);
            return doRequestFocus(audioManager, listener, attributesBuilder.build(), androidFocus);
        }

        public static int doRequestFocus(
                AudioManager audioManager,
                AudioManager.OnAudioFocusChangeListener listener,
                AudioAttributes attributes,
                int androidFocus) {

            return doRequestFocus(audioManager, listener, attributes, androidFocus, false);
        }

        public static int doRequestFocus(
                AudioManager audioManager,
                AudioManager.OnAudioFocusChangeListener listener,
                AudioAttributes attributes,
                int androidFocus,
                boolean acceptsDelayedFocus) {

            AudioFocusRequest.Builder focusBuilder = new AudioFocusRequest.Builder(androidFocus);
            focusBuilder.setOnAudioFocusChangeListener(listener).setAcceptsDelayedFocusGain(
                    acceptsDelayedFocus);
            focusBuilder.setAudioAttributes(attributes);
            return audioManager.requestAudioFocus(focusBuilder.build());
        }

        public static int doAbandonAudioFocus(
                AudioManager audioManager,
                AudioManager.OnAudioFocusChangeListener listener,
                AudioAttributes attributes,
                int androidFocus) {

            AudioFocusRequest.Builder focusBuilder = new AudioFocusRequest.Builder(androidFocus);
            focusBuilder.setOnAudioFocusChangeListener(listener);
            focusBuilder.setAudioAttributes(attributes);
            return audioManager.abandonAudioFocusRequest(focusBuilder.build());
        }
    }
}

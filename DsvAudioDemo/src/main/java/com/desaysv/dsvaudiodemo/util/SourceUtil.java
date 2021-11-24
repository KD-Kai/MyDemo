package com.desaysv.dsvaudiodemo.util;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;

public class SourceUtil {

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

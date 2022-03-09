package com.desaysv.dsvaudiodemo.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class MediaPlayerUtil {
    private final String TAG = "DsvAudioDemo" + this.getClass().getSimpleName();

    private static final String mFileName= "dsv_audio_demo_44.1.mp3";

    private static final int MSG_INIT_MP = 0;
    private static final int MSG_RELEASE_MP = 1;
    private static final int MSG_PLAY = 2;
    private static final int MSG_PAUSE = 3;
    private static final int MSG_GET_POSITION = 7;

    private HandlerThread mChildThread = null;
    private Handler mChildHandler = null;
    private Context mContext;
    private AudioAttributes mAudioAttributes;

    private final Object mLock = new Object();
    private MediaPlayer mediaPlayer = null;
    private boolean isPlayerPrepared = false;

    public void init(Context context, AudioAttributes audioAttributes) {
        mContext = context;
        initChildThread();
        initMediaPlayer(audioAttributes);
    }

    public void destroy() {
        releaseMediaPlayer();
        destroyChildThread();
    }

    public void start() {
        sendChildThreadMsg(MSG_PLAY);
    }

    public void pause() {
        sendChildThreadMsg(MSG_PAUSE);
    }

    public void initMediaPlayer(AudioAttributes audioAttributes) {
        mAudioAttributes = audioAttributes;
        sendChildThreadMsg(MSG_INIT_MP);
    }

    public void releaseMediaPlayer() {
        sendChildThreadMsg(MSG_RELEASE_MP);
    }

    public interface MediaPlayerCallBack {

        void onPositionChange(int position);

        void onDurationChange(int duration);

        void onError(String err);
    }

    public void setMediaPlayerListener (MediaPlayerCallBack listener){
        mListener = listener;
    }

    private MediaPlayerCallBack mListener;

    private void notifyPosition(int position) {
        if (mListener != null) {
            mListener.onPositionChange(position);
        }
    }

    private void notifyDuration(int duration) {
        if (mListener != null) {
            mListener.onDurationChange(duration);
        }
    }

    private void notifyError(String err) {
        if (mListener != null) {
            mListener.onError(err);
        }
    }

    private void initChildThread() {
        Log.d(TAG, "initChildThread");
        if (mChildThread == null && mChildHandler == null) {
            mChildThread = new HandlerThread(TAG);
            mChildThread.start();
            mChildHandler = new Handler(mChildThread.getLooper(), new ChildHandlerThread());
        }
    }

    private void destroyChildThread() {
        Log.d(TAG, "destroyChildThread");
        if (mChildHandler != null) {
            mChildHandler.removeCallbacksAndMessages(null);
            mChildHandler = null;
        }
        if (mChildThread != null) {
            mChildThread.quitSafely();
            mChildThread = null;
        }
    }

    private void sendChildThreadMsg(int what) {
        if (mChildHandler != null) {
            mChildHandler.removeMessages(what);
            mChildHandler.sendEmptyMessage(what);
        }
    }

    private class ChildHandlerThread implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_MP:
                    initMediaPlayer();
                    break;
                case MSG_RELEASE_MP:
                    releaseMediaPlayer();
                    break;
                case MSG_PLAY:
                    onPlay();
                    break;
                case MSG_PAUSE:
                    onPause();
                    break;
                case MSG_GET_POSITION:
                    getPosition();
                    break;

            }
            return false;
        }

        private void initMediaPlayer() {
            Log.d(TAG, "initMediaPlayer");
            if (mediaPlayer == null) {
                synchronized (mLock) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        if (mAudioAttributes != null) {
                            mediaPlayer.setAudioAttributes(mAudioAttributes);
                        }
                        mediaPlayer.setOnPreparedListener(onPreparedListener);
                        mediaPlayer.setOnErrorListener(onErrorListener);
                    }
                }
            }
        }

        private void releaseMediaPlayer() {
            Log.d(TAG, "releaseMediaPlayer");
            if (mediaPlayer != null) {
                synchronized (mLock) {
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                }
            }
        }

        private void onPlay() {
            Log.d(TAG, "onPlay: isPlayerPrepared = " + isPlayerPrepared);
            if (mediaPlayer != null) {
                if (isPlayerPrepared) {
                    mediaPlayer.start();
                } else {
                    try {
                        AssetFileDescriptor afd = mContext.getAssets().openFd(mFileName);
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mediaPlayer.prepareAsync();
                    }catch (Exception e) {
                        notifyError("onPlay: Exception = " + e);
                        e.printStackTrace();
                    }
                }
            }
        }

        private void onPause() {
            Log.d(TAG, "onPause: isPlayerPrepared = " + isPlayerPrepared);
            if (mediaPlayer != null && isPlayerPrepared) {
                mediaPlayer.pause();
            }
        }

        private void getPosition() {
            if (mediaPlayer != null && isPlayerPrepared) {
                final int position = mediaPlayer.getCurrentPosition();
                notifyPosition(position);
            }
            if (mChildHandler != null) {
                mChildHandler.sendEmptyMessageDelayed(MSG_GET_POSITION, 500);
            }
        }

        private void getDuration() {
            if (mediaPlayer != null && isPlayerPrepared) {
                final int duration = mediaPlayer.getDuration();
                notifyDuration(duration);
            }
        }

        private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPreparedListener: onPrepared");
                isPlayerPrepared = true;
                mp.start();
                getPosition();
                getDuration();
            }
        };

        private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                notifyError("MediaPlayer.OnErrorListener: what = " + what);
                return false;
            }
        };
    }
}

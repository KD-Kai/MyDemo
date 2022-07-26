package com.desaysv.dsvrecorderdemo.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;

public class MediaPlayerDemo {

    private static final String TAG = "PlayerDemo";

    private static final int MSG_START_PLAYER = 1;
    private static final int MSG_STOP_PLAYER = 2;

    private HandlerThread mPlayerThread;
    private Handler mHandler;

    private AudioFocusDemo audioFocusDemo;

    private FileDescriptor fd = new FileDescriptor();

    public MediaPlayerDemo(Context context) {
        initPlayerThread();
        audioFocusDemo = new AudioFocusDemo(context);
    }

    public void destroy() {
        destroyPlayerThread();
    }

    public void startPlay() {
        Log.d(TAG, "startPlay");
        sendMsg(MSG_START_PLAYER);
    }

    public void stopPlay() {
        Log.d(TAG, "stopPlay");
        sendMsg(MSG_STOP_PLAYER);
    }

    private void initPlayerThread() {
        mPlayerThread = new HandlerThread("mPlayerThread");
        mPlayerThread.start();
        mHandler = new Handler(mPlayerThread.getLooper(), new PlayerHandler());
    }

    private void destroyPlayerThread() {
        mHandler.removeCallbacksAndMessages(null);
        mPlayerThread.quitSafely();
    }

    private class PlayerHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_START_PLAYER:
                    synchronized (mLock) {
                        startMediaPlayer();
                    }
                    break;
                case MSG_STOP_PLAYER:
                    synchronized (mLock) {
                        stopMediaPlayer();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }

        private final Object mLock = new Object();
        private MediaPlayer mediaPlayer;

        private void startMediaPlayer() {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnErrorListener(onErrorListener);
                    mediaPlayer.setOnCompletionListener(onCompletionListener);
                }
                if (new File(MediaRecorderDemo.RECORDER_FILE_PATH).exists()) {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(MediaRecorderDemo.RECORDER_FILE_PATH);
                    mediaPlayer.prepare();
                    audioFocusDemo.requestFocus();
                    mediaPlayer.start();
                    onPlayerStatusChange(true);
                } else {
                    Log.w(TAG, "File not exist");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void stopMediaPlayer() {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    onPlayerStatusChange(false);
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    audioFocusDemo.abandonFocus();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMsg(int what) {
        if (mHandler != null) {
            mHandler.removeMessages(what);
            mHandler.sendEmptyMessage(what);
        }
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "onError: what = " + what);
            return false;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion");
            stopPlay();
        }
    };

    public interface OnPlayerStatusListener {
        void isPlaying(boolean isPlaying);
    }

    private OnPlayerStatusListener mListener;

    public void setOnPlayerStatusListener(OnPlayerStatusListener listener) {
        mListener = listener;
    }

    private void onPlayerStatusChange(boolean isPlaying) {
        if (mListener != null) {
            mListener.isPlaying(isPlaying);
        }
    }
}

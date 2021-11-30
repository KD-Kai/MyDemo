package com.desaysv.dsvrecorderdemo.demo;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;

public class MediaRecorderDemo {

    private static final String TAG = "RecorderDemo";
    private static final String RECORDER_FILE_PATH = "/sdcard/test.mp3";

    private static final int MSG_START_RECORDER = 1;
    private static final int MSG_STOP_RECORDER = 2;

    private HandlerThread mRecorderThread;
    private Handler mHandler;

    private FileDescriptor fd = new FileDescriptor();

    public MediaRecorderDemo() {
        initRecorderThread();
    }

    public void destroy() {
        destroyRecorderThread();
    }

    public void startRecord() {
        Log.d(TAG, "startRecord");
        sendMsg(MSG_START_RECORDER);
    }

    public void stopRecord() {
        Log.d(TAG, "startRecord");
        sendMsg(MSG_STOP_RECORDER);
    }

    private void initRecorderThread() {
        mRecorderThread = new HandlerThread("mRecorderThread");
        mRecorderThread.start();
        mHandler = new Handler(mRecorderThread.getLooper(), new RecorderHandler());
    }

    private void destroyRecorderThread() {
        mHandler.removeCallbacksAndMessages(null);
        mRecorderThread.quitSafely();
    }

    private class RecorderHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_START_RECORDER:
                    synchronized (mLock) {
                        startMediaRecorder();
                    }
                    break;
                case MSG_STOP_RECORDER:
                    synchronized (mLock) {
                        stopMediaRecorder();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }

        private final Object mLock = new Object();
        private MediaRecorder mediaRecorder;

        private void startMediaRecorder() {
            try {
                if (mediaRecorder == null) {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setOnErrorListener(onErrorListener);
                }
                new File(RECORDER_FILE_PATH).deleteOnExit();
                mediaRecorder.reset();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setOutputFile(RECORDER_FILE_PATH);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.prepare();
                mediaRecorder.start();
                onRecorderStatusChange(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void stopMediaRecorder() {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    onRecorderStatusChange(false);
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    mediaRecorder = null;
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

    private MediaRecorder.OnErrorListener onErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.d(TAG, "onError: what = " + what);
        }
    };

    public interface OnRecorderStatusListener {
        void isRecording(boolean isRecording);
    }

    private OnRecorderStatusListener mListener;

    public void setOnRecorderStatusListener(OnRecorderStatusListener listener) {
        mListener = listener;
    }

    private void onRecorderStatusChange(boolean isRecording) {
        if (mListener != null) {
            mListener.isRecording(isRecording);
        }
    }
}

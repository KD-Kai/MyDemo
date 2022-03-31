package com.desaysv.dsvaudiodemo.fragment;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.desaysv.dsvaudiodemo.R;
import com.desaysv.dsvaudiodemo.util.MediaPlayerUtil;
import com.desaysv.dsvaudiodemo.util.TimeUtil;
import com.desaysv.ivi.car.audio.strategy.impl.SvCarAudioManager;

import java.io.File;
import java.util.ArrayList;

public abstract class BaseFragment extends Fragment implements View.OnClickListener, MediaPlayerUtil.MediaPlayerCallBack{

    protected final String TAG = "DsvAudioDemo|" + this.getClass().getSimpleName();
    protected View mRoot = null;
    protected FragmentActivity mActivity;
    protected Context mContext;
    protected int mAudioFocus;

    private HandlerThread mChildThread = null;
    private Handler mChildHandler = null;

    protected Button mPlayBtn;
    protected Button mPauseBtn;
    protected Button mRequestBtn;
    protected Button mAbandonBtn;
    protected TextView mPosition;
    protected TextView mDuration;
    protected TextView mRequestResult;
    protected TextView mCurrentFocus;
    protected AudioAttributes mAudioAttributes;

    private MediaPlayerUtil mediaPlayerUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = getContext();
        init(inflater, container);
        return mRoot;
    }

    protected void init(LayoutInflater inflater, ViewGroup container) {
        mRoot = inflater.inflate(R.layout.fragment_audio_demo, container, false);
        TextView textView = mRoot.findViewById(R.id.audio_demo_name_tv);
        textView.setText(TAG.substring(13));

        mPlayBtn = mRoot.findViewById(R.id.audio_demo_play_btn);
        mPauseBtn = mRoot.findViewById(R.id.audio_demo_pause_btn);
        mRequestBtn = mRoot.findViewById(R.id.audio_demo_request_focus_btn);
        mAbandonBtn = mRoot.findViewById(R.id.audio_demo_abandon_focus_btn);
        mPosition = mRoot.findViewById(R.id.audio_demo_position_tv);
        mDuration = mRoot.findViewById(R.id.audio_demo_duration_tv);
        mRequestResult = mRoot.findViewById(R.id.audio_demo_request_result_tv);
        mCurrentFocus = mRoot.findViewById(R.id.audio_demo_current_focus_tv);
        mPlayBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mRequestBtn.setOnClickListener(this);
        mAbandonBtn.setOnClickListener(this);

        initChildThread();
        mediaPlayerUtil = new MediaPlayerUtil();
        mediaPlayerUtil.init(mContext, mAudioAttributes);
        mediaPlayerUtil.setMediaPlayerListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyChildThread();
        mediaPlayerUtil.destroy();
    }

    @Override
    public void onClick(View v) {
        if (v == mPlayBtn) {
            mediaPlayerUtil.start();
        }else if (v == mPauseBtn) {
            mediaPlayerUtil.pause();
        }else if (v == mRequestBtn) {
            sendChildThreadMsg(MSG_REQUEST_FOCUS);
        }else if (v == mAbandonBtn) {
            sendChildThreadMsg(MSG_ABANDON_FOCUS);
        }
    }

    @Override
    public void onPositionChange(int position) {
        mPosition.post(new Runnable() {
            @Override
            public void run() {
                mPosition.setText(TimeUtil.intToTimeStr(position));
            }
        });
    }

    @Override
    public void onDurationChange(int duration) {
        mDuration.post(new Runnable() {
            @Override
            public void run() {
                mDuration.setText(TimeUtil.intToTimeStr(duration));
            }
        });
    }

    @Override
    public void onError(String err) {
        Log.e(TAG, "onError: " + err);
    }

    protected boolean onRequestFocus() {
        return false;
    }

    protected boolean onAbandonFocus() {
        return false;
    }

    private static final int MSG_REQUEST_FOCUS = 5;
    private static final int MSG_ABANDON_FOCUS = 6;
    private static final int MONITOR_CURFOCUS = 8;

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
                case MSG_REQUEST_FOCUS:
                    requestFocus();
                    break;
                case MSG_ABANDON_FOCUS:
                    abandonFocus();
                    break;
                case MONITOR_CURFOCUS:
                    getCurrentFocus();
                    break;

            }
            return false;
        }

        private void requestFocus() {
            final boolean result = onRequestFocus();
            mRequestResult.post(new Runnable() {
                @Override
                public void run() {
                    mRequestResult.setText(String .valueOf(result));
                    sendChildThreadMsg(MONITOR_CURFOCUS);
                }
            });
            dumpCurrentActiveSource();
        }

        private void abandonFocus() {
            final boolean result = onAbandonFocus();
            mRequestResult.post(new Runnable() {
                @Override
                public void run() {
                    mRequestResult.setText(String .valueOf(result));
                    sendChildThreadMsg(MONITOR_CURFOCUS);
                }
            });
            dumpCurrentActiveSource();
        }

        private void getCurrentFocus() {
            mCurrentFocus.post(new Runnable() {
                @Override
                public void run() {
                    mCurrentFocus.setText(String.valueOf(mAudioFocus));
                }
            });
            if (mChildHandler != null) {
                mChildHandler.sendEmptyMessageDelayed(MONITOR_CURFOCUS, 100);
            }
        }
    }

    private void dumpCurrentActiveSource() {

    }
}
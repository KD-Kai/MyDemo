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
import com.desaysv.dsvaudiodemo.util.TimeUtil;
import com.desaysv.ivi.platformadapter.app.audio.SvCarAudioManager;

import java.io.File;
import java.util.ArrayList;

public abstract class BaseFragment extends Fragment implements View.OnClickListener{

    protected final String TAG = this.getClass().getSimpleName();
    protected View mRoot = null;
    protected FragmentActivity mActivity;
    protected Context mContext;
    protected int mAudioFocus;

    private HandlerThread mChildThread = null;
    private Handler mChildHandler = null;

    protected Button mPlayBtn;
    protected Button mPauseBtn;
    protected Button mSearchBtn;
    protected Button mRequestBtn;
    protected Button mAbandonBtn;
    protected TextView mSearchResult;
    protected TextView mPosition;
    protected TextView mDuration;
    protected TextView mRequestResult;
    protected TextView mCurrentFocus;
    protected AudioAttributes mAudioAttributes;

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
        textView.setText(TAG);

        mPlayBtn = mRoot.findViewById(R.id.audio_demo_play_btn);
        mPauseBtn = mRoot.findViewById(R.id.audio_demo_pause_btn);
        mSearchBtn = mRoot.findViewById(R.id.audio_demo_search_btn);
        mRequestBtn = mRoot.findViewById(R.id.audio_demo_request_focus_btn);
        mAbandonBtn = mRoot.findViewById(R.id.audio_demo_abandon_focus_btn);
        mSearchResult = mRoot.findViewById(R.id.audio_demo_search_result_tv);
        mPosition = mRoot.findViewById(R.id.audio_demo_position_tv);
        mDuration = mRoot.findViewById(R.id.audio_demo_duration_tv);
        mRequestResult = mRoot.findViewById(R.id.audio_demo_request_result_tv);
        mCurrentFocus = mRoot.findViewById(R.id.audio_demo_current_focus_tv);
        mPlayBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);
        mRequestBtn.setOnClickListener(this);
        mAbandonBtn.setOnClickListener(this);

        initChildThread();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyChildThread();
        sendChildThreadMsg(MSG_RELEASE_MP);
    }

    @Override
    public void onClick(View v) {
        if (v == mPlayBtn) {
            sendChildThreadMsg(MSG_PLAY);
        }else if (v == mPauseBtn) {
            sendChildThreadMsg(MSG_PAUSE);
        }else if (v == mSearchBtn) {
            sendChildThreadMsg(MSG_SEARCH);
        }else if (v == mRequestBtn) {
            sendChildThreadMsg(MSG_REQUEST_FOCUS);
        }else if (v == mAbandonBtn) {
            sendChildThreadMsg(MSG_ABANDON_FOCUS);
        }
    }

    protected boolean onRequestFocus() {
        return false;
    }

    protected boolean onAbandonFocus() {
        return false;
    }

    //下面是播放音乐的Demo；
    private static final int MSG_INIT_MP = 0;
    private static final int MSG_RELEASE_MP = 1;
    private static final int MSG_PLAY = 2;
    private static final int MSG_PAUSE = 3;
    private static final int MSG_SEARCH = 4;
    private static final int MSG_REQUEST_FOCUS = 5;
    private static final int MSG_ABANDON_FOCUS = 6;
    private static final int MONITOR_POSITION = 7;
    private static final int MONITOR_CURFOCUS = 8;


    private final Object mLock = new Object();
    private MediaPlayer mediaPlayer = null;
    private boolean isPlayerPrepared = false;
    private String mPlayPath = null;


    private void initChildThread() {
        Log.d(TAG, "initChildThread: mChildThread -> " + (mChildThread == null)
                + " mChildThread -> " + (mChildHandler == null));
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
                case MSG_SEARCH:
                    onSearch();
                    break;
                case MSG_REQUEST_FOCUS:
                    requestFocus();
                    break;
                case MSG_ABANDON_FOCUS:
                    abandonFocus();
                    break;
                case MONITOR_POSITION:
                    getPosition();
                    break;
                case MONITOR_CURFOCUS:
                    getCurrentFocus();
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
                }else if (!TextUtils.isEmpty(mPlayPath)){
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(mPlayPath);
                        mediaPlayer.prepareAsync();
                    }catch (Exception e) {
                        Log.d(TAG, "onPlay: Error, mPlayPath = " + mPlayPath);
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
                mPosition.post(new Runnable() {
                    @Override
                    public void run() {
                        mPosition.setText(TimeUtil.intToTimeStr(position));
                    }
                });
            }
            if (mChildHandler != null) {
                mChildHandler.sendEmptyMessageDelayed(MONITOR_POSITION, 500);
            }
        }

        private void getDuration() {
            if (mediaPlayer != null && isPlayerPrepared) {
                final int duration = mediaPlayer.getDuration();
                mDuration.post(new Runnable() {
                    @Override
                    public void run() {
                        mDuration.setText(TimeUtil.intToTimeStr(duration));
                    }
                });
            }
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

        private void onSearch() {
            final String searchPath = "/sdcard";
            File searchFile = new File(searchPath);
            Log.d(TAG, "onSearch: path = " + searchPath + "; canRead = " + searchFile.canRead());
            File[] files = searchFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.getName().contains(".mp3")) {
                        mPlayPath = file.getAbsolutePath();
                        Log.d(TAG, "onSearch: mPlayPath = " + mPlayPath);
                        sendChildThreadMsg(MSG_INIT_MP);
                        break;
                    }
                }
            }
            if (!TextUtils.isEmpty(mPlayPath)) {
                mSearchResult.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchResult.setText("Search Result = " + mPlayPath);
                    }
                });
            }else {
                mSearchResult.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchResult.setText("check path = " + searchPath + ", and try again.");
                    }
                });
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
    }

    private void dumpCurrentActiveSource() {
        ArrayList<String> srcList = SvCarAudioManager.get(mContext).getCurrentActiveSources();
        Log.d(TAG, "dumpCurrentActiveSource: size = " + srcList.size());
        for (String str : srcList) {
            Log.d(TAG, "dumpCurrentActiveSource: str = " + str);
        }
    }
}
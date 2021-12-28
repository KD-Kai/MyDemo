package com.desaysv.dsvbootanimdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class BootAnimService extends Service {
    private static final String TAG = "BootAnimService";

    private View mBootAnimLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private TextureView mVideoView;
    private Surface mSurface;
    private MediaPlayer mediaPlayer;

    public BootAnimService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setForeground();
        Log.d(TAG, "onCreate");
        registerBootAnimReceiver();
        initBootAnimWindow();
        displayBootAnim();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBootAnimReceiver();
    }

    private void setForeground() {
        //添加startForeground
        if (Build.VERSION.SDK_INT >= 26) {//创建后台服务（Android8.0特有）
            String CHANNEL_ID = "com.desaysv.dsvmedia.service";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(null, null);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("")
                    .setDefaults(0)
                    .setVibrate(new long[]{0})
                    .setSound(null)
                    .build();

            startForeground(1, notification);
        }
    }

    private void registerBootAnimReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("desaysv.intent.action.bootanim");
        registerReceiver(mBootAnimReceiver, intentFilter);
    }

    private void unregisterBootAnimReceiver() {
        unregisterReceiver(mBootAnimReceiver);
    }

    private final BroadcastReceiver mBootAnimReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mBootAnimReceiver: onReceive = " + intent.getAction());
            displayBootAnim();
        }
    };

    private void initBootAnimWindow() {
        mBootAnimLayout = LayoutInflater.from(this).inflate(R.layout.boot_anim_layout, null);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = 2145;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.gravity = Gravity.CENTER;
    }
    private void displayBootAnim() {
        Log.d(TAG, "displayBootAnim");
        mWindowManager.addView(mBootAnimLayout, mLayoutParams);
        mVideoView = mBootAnimLayout.findViewById(R.id.boot_anim_layout_tv);
        mVideoView.setSurfaceTextureListener(surfaceTextureListener);
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable: surface = " + surface);
            mSurface = new Surface(surface);
            createMediaPlayer();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    private void createMediaPlayer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMediaPlayer();

                try {
                    AssetFileDescriptor afd = getAssets().openFd("toyota_logo_760b.mp4");
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepareAsync();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void initMediaPlayer() {
        synchronized (BootAnimService.this) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(onCompletionListener);
                mediaPlayer.setOnPreparedListener(onPreparedListener);
                mediaPlayer.setSurface(mSurface);
            }
        }
    }

    private void releaseMediaPlayer() {
        synchronized (BootAnimService.this) {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPreparedListener");
            mp.start();
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletionListener");
            mWindowManager.removeView(mBootAnimLayout);
            releaseMediaPlayer();
        }
    };
}
package com.desaysv.dsvkeydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import com.desaysv.ivi.platformadapter.app.keypolicy.SvKeyPolicyManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DsvKeyDemo";

    private SvKeyPolicyManager mKeyPolicyManager = null;
    private Context mContext;

    /**
     * tag1和tag2可自定义，用于避免重复注册；
     * 1. 如果tag1+tag2相同，再次执行registerKeyCallBack将无效；
     * 2. registerKeyCallBack和unRegisterKeyCallBack需要使用相同的tags；
     */
    private final String tag1 = "com.desaysv.dsvkeydemo";
    private final String tag2 = "com.desaysv.dsvkeydemo.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mKeyPolicyManager = new SvKeyPolicyManager(this);
        mKeyPolicyManager.registerKeyCallBack(mKeyEventListener, tag1, tag2);
        mKeyPolicyManager.setHiCarNeedHandle(false);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.desaysv.action.key.OFF_HOOK");
        intentFilter.addAction("com.tencent.mm.plugin.openapi.Intent.ACTION_NOTIFY_VOIP");
        intentFilter.addCategory("com.tencent.mm.category.com.tencent.autowechat");
        registerReceiver(broadcastReceiver, intentFilter);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKeyPolicyManager.unRegisterKeyCallBack(mKeyEventListener, tag1, tag2);
        unregisterReceiver(broadcastReceiver);
        Log.d(TAG, "onDestroy");
    }

    private SvKeyPolicyManager.OnKeyCallBackListener mKeyEventListener = new SvKeyPolicyManager.OnKeyCallBackListener() {
        @Override
        public void onKeyEventCallBack(KeyEvent event) {
            Log.d(TAG, "keyCode = " + event.getKeyCode());
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: getAction = " + intent.getAction());
        }
    };
}
package com.desaysv.dsvkeydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
        intentFilter.addAction("com.desaysv.action.key.VR");
//        intentFilter.addAction("com.desaysv.action.key.VR_SINGLE_CLICK");
//        intentFilter.addAction("com.desaysv.action.key.VR_DOUBLE_CLICK");
        intentFilter.addAction("com.desaysv.action.key.ON_HOOK");
        intentFilter.addAction("com.desaysv.action.key.OFF_HOOK");
        registerReceiver(broadcastReceiver, intentFilter);

        Intent intent = new Intent("desaysv.intent.action.bootanim");
        intent.setClassName("com.desaysv.dsvbootanimdemo", "com.desaysv.dsvbootanimdemo.BootAnimReceiver");
        sendBroadcast(intent);
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
            if (intent.getAction().contains("HOOK")) {
                Log.d(TAG, "onReceive: EXTRA_KEY_EVENT_STATUS = " + intent.getStringExtra("com.desaysv.key.EXTRA_KEY_EVENT_STATUS"));
                Log.d(TAG, "onReceive: EXTRA_IS_ON_HOOK_VALID = " + intent.getBooleanExtra("com.desaysv.key.EXTRA_IS_ON_HOOK_VALID", false));
                Log.d(TAG, "onReceive: EXTRA_PROCESSOR_WE_CHAT = " + intent.getStringExtra("com.desaysv.key.EXTRA_PROCESSOR_WE_CHAT"));
                Log.d(TAG, "onReceive: EXTRA_PROCESSOR_BT_PHONE = " + intent.getStringExtra("com.desaysv.key.EXTRA_PROCESSOR_BT_PHONE"));
                Log.d(TAG, "onReceive: EXTRA_PROCESSOR_PHONE_LINK = " + intent.getStringExtra("com.desaysv.key.EXTRA_PROCESSOR_PHONE_LINK"));
            }
            if (intent.getAction().contains("VR")) {
                Log.d(TAG, "onReceive: EXTRA_KEY_EVENT_STATUS = " + intent.getStringExtra("com.desaysv.key.EXTRA_KEY_EVENT_STATUS"));
                Log.d(TAG, "onReceive: EXTRA_PROCESSOR_WE_CHAT = " + intent.getStringExtra("com.desaysv.key.EXTRA_PROCESSOR_WE_CHAT"));
                Log.d(TAG, "onReceive: EXTRA_PROCESSOR_PHONE_LINK = " + intent.getStringExtra("com.desaysv.key.EXTRA_PROCESSOR_PHONE_LINK"));
                Log.d(TAG, "onReceive: EXTRA_PROCESSOR_HUAWEI_VR = " + intent.getStringExtra("com.desaysv.key.EXTRA_PROCESSOR_HUAWEI_VR"));
                Log.d(TAG, "onReceive: EXTRA_PROCESSOR_DEVICE_MGR = " + intent.getStringExtra("com.desaysv.key.EXTRA_PROCESSOR_DEVICE_MGR"));
            }
        }
    };
}
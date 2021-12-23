package com.desaysv.dsvkeydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import desaysv.adapter.app.keypolicy.KeyPolicyManager;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DsvKeyDemo";

    private KeyPolicyManager mKeyPolicyManager = null;
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
        mKeyPolicyManager = new KeyPolicyManager(this);
        mKeyPolicyManager.registerKeyCallBack(mKeyEventListener, tag1, tag2);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKeyPolicyManager.unRegisterKeyCallBack(mKeyEventListener, tag1, tag2);
        Log.d(TAG, "onDestroy");
    }

    private KeyPolicyManager.OnKeyCallBackListener mKeyEventListener = new KeyPolicyManager.OnKeyCallBackListener() {
        @Override
        public void onKeyEventCallBack(KeyEvent event) {
            Log.d(TAG, "keyCode = " + event.getKeyCode());
        }
    };
}
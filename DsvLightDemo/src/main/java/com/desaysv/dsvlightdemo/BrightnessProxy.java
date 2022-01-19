package com.desaysv.dsvlightdemo;

import android.app.Activity;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class BrightnessProxy {

    private static final String TAG = "CarBrightnessProxy";

    public static void setGlobalBrightness(int brightness) {
        Log.d(TAG, "setBrightness: start --> " + System.currentTimeMillis());
        try {
            IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
            if (power != null) {
                power.setTemporaryScreenBrightnessSettingOverride(brightness);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setBrightness: done --> " + System.currentTimeMillis());
    }

    public static void setLocalBrightness(Activity activity, int brightness) {
        Log.d(TAG, "setWindowBrightness: start --> " + System.currentTimeMillis());
        float light = (float) brightness / 255;
        Window window = activity.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.screenBrightness = light;
            window.setAttributes(lp);
        }
        Log.d(TAG, "setWindowBrightness: start --> " + System.currentTimeMillis());
    }

}

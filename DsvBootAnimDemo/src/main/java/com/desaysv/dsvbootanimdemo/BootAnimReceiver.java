package com.desaysv.dsvbootanimdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootAnimReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootAnimReceiver", "onReceive: intent = " + intent.getAction());
    }
}

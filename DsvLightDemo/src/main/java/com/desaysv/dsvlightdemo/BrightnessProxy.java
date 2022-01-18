package com.desaysv.dsvlightdemo;

import android.annotation.SuppressLint;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarVendorExtensionManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IPowerManager;
import android.os.Message;
import android.os.ServiceManager;
import android.support.car.Car;
import android.support.car.CarConnectionCallback;
import android.support.car.CarNotConnectedException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrightnessProxy {

    private static final String TAG = "CarBrightnessProxy";

    private Car mCarApiClient;
    private CarVendorExtensionManager mCarVendorExtensionManager;

    private static final int MODE_AUTO = 0;
    private static final int MODE_MANUAL = 1;

    private static final int MODE_DAY = 0;
    private static final int MODE_NIGHT = 1;

    private static final int MSG_INIT_SPI = 0;
    private static final int MSG_SET_BRIGHTNESS_MODE = 1;
    private static final int MSG_SET_BRIGHTNESS_INFO = 2;
    private static final int MSG_GET_BRIGHTNESS_INFO = 3;

    private boolean isConnected = false;

    private static final int MAX_TIMES = 10;
    private int mBrightnessMode = MODE_AUTO;
    private int resetTimes = 0;

    @SuppressLint("StaticFieldLeak")
    private volatile static BrightnessProxy mInstance = null;

    public static BrightnessProxy getInstance() {
        if (null == mInstance) {
            synchronized (BrightnessProxy.class) {
                if (null == mInstance) {
                    mInstance = new BrightnessProxy();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (mCarApiClient == null) {
            synchronized (BrightnessProxy.class) {
                if (mCarApiClient == null) {
                    initChildThread();
                    sendHandlerMsg(MSG_INIT_SPI, context);
                }
            }
        }
    }

    public void destroy() {
        if (mCarVendorExtensionManager != null) {
            try {
                Log.d(TAG, "unregister mHardwareCallback = " + mHardwareCallback);
                mCarVendorExtensionManager.unregisterCallback(mHardwareCallback);
                mCarVendorExtensionManager = null;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        destroyChildThread();
    }

    private void initVendorExtensionManager() {
        Log.d(TAG, "register mHardwareCallback = " + mHardwareCallback);
        try {
            mCarVendorExtensionManager = (CarVendorExtensionManager) mCarApiClient.getCarManager(android.car.Car.VENDOR_EXTENSION_SERVICE);
            mCarVendorExtensionManager.registerCallback(mHardwareCallback);

        } catch (android.car.CarNotConnectedException e) {
            Log.e(TAG, "android.car not connected in VendorExtension");
        } catch (CarNotConnectedException e) {
            Log.e(TAG, "Car not connected in VendorExtension");
        }
    }

    private final CarVendorExtensionManager.CarVendorExtensionCallback mHardwareCallback =
            new CarVendorExtensionManager.CarVendorExtensionCallback() {
                @Override
                public void onChangeEvent(CarPropertyValue val) {
                    if (val.getPropertyId() == 0x21404107) {
                        notifyBrightness(val);
                    }
                }

                @Override
                public void onErrorEvent(final int propertyId, final int zone) {
                }
            };

    private void notifyBrightness(CarPropertyValue val) {
        Object[] objects = (Object[]) val.getValue();
        if (objects == null || objects.length < 4) {
            return;
        }
        int auto = Byte.parseByte(objects[0].toString());
        int day = Byte.parseByte(objects[1].toString());
        int dayBright = Byte.parseByte(objects[2].toString());
        int nightBright = Byte.parseByte(objects[3].toString());
        checkCurrentBrightnessMode(auto);

        Log.d(TAG, String.format("notifyBrightness: auto = %d; day = %d; dayBright = %d; nightBright = %d;",
                auto, day, dayBright, nightBright));
        for (IBrightnessListener listener : brightnessListeners) {
            listener.onBrightChange(auto, day, dayBright, nightBright);
        }
    }

    private void checkCurrentBrightnessMode(int mode) {
        Log.d(TAG, "checkCurrentBrightnessMode: mode = " + mode
                + "; mBrightnessMode = " + mBrightnessMode + "; resetTimes = " + resetTimes);
        if (mode != mBrightnessMode) {
            if (resetTimes <= MAX_TIMES) {
                setBrightnessMode(MODE_AUTO == mBrightnessMode);
                resetTimes ++;
            }else {
                resetTimes = 0;
                Log.w(TAG, "checkCurrentBrightnessMode: fail to set mode");
            }
        }
    }

    public void setBrightnessMode(boolean isAuto) {
        Log.d(TAG, "setBrightnessMode: isAuto = " + isAuto);
        sendHandlerMsg(MSG_SET_BRIGHTNESS_MODE, isAuto);
        sendHandlerMsg(MSG_GET_BRIGHTNESS_INFO);
    }

    public void setBrightnessInfo(int brightness) {
        Log.d(TAG, "setBrightnessInfo: brightness = " + brightness);
        if (brightness == 0) {  //防止屏幕黑屏
            brightness = 1;
        }
        sendHandlerMsg(MSG_SET_BRIGHTNESS_INFO, brightness);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public interface IBrightnessListener {
        /**
         *
         * @param auto 0:自动  1:手动
         * @param day  0:白天  1:黑夜
         * @param dayBright  白天亮度
         * @param nightBright  黑夜亮度
         */
        void onBrightChange(int auto, int day, int dayBright,int nightBright);
    }

    private List<IBrightnessListener> brightnessListeners = new ArrayList<>();

    public void addBrightnessListener(IBrightnessListener listener) {
        if (listener != null) {
            brightnessListeners.add(listener);
        }
    }

    public void removeBrightnessListener(IBrightnessListener listener) {
        if (listener != null) {
            brightnessListeners.remove(listener);
        }
    }

    /*
     * 子线程Lopper --> start
     */
    private HandlerThread mChildThread = null;
    private Handler mChildHandler = null;

    private void initChildThread() {
        if (mChildThread == null && mChildHandler == null) {
            mChildThread = new HandlerThread("CarBrightnessProxy");
            mChildThread.start();
            mChildHandler = new Handler(mChildThread.getLooper(), new ChildHandlerThread());
        }
    }

    private void destroyChildThread() {
        if (mChildThread != null) {
            mChildThread.quitSafely();
            mChildThread = null;
        }
    }

    private void sendHandlerMsg(int what) {
        Log.d(TAG, "sendHandlerMsg: what = " + what
                + "; mChildHandler = " + (mChildHandler != null));
        if (mChildHandler != null) {
            mChildHandler.removeMessages(what);
            mChildHandler.sendEmptyMessage(what);
        }
    }

    private void sendHandlerMsg(int what, Object obj) {
        Log.d(TAG, "sendHandlerMsg: what = " + what
                + "; mChildHandler = " + (mChildHandler != null));
        if (mChildHandler != null) {
            mChildHandler.removeMessages(what);
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = obj;
            mChildHandler.sendMessage(msg);
        }
    }

    private class ChildHandlerThread implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: msg = " + msg.what + "; obj = " + msg.obj);
            switch (msg.what) {
                case MSG_INIT_SPI:
                    if (msg.obj != null) {
                        InitSPI((Context) msg.obj);
                    }
                    break;
                case MSG_SET_BRIGHTNESS_MODE:
                    if (msg.obj != null) {
                        setBrightnessMode((boolean) msg.obj);
                    }
                    break;
                case MSG_SET_BRIGHTNESS_INFO:
                    if (msg.obj != null) {
                        int value = Integer.parseInt(msg.obj.toString());
                        setBrightnessInfo(value);
                    }
                    break;
                case MSG_GET_BRIGHTNESS_INFO:
                    getBrightnessInfo();
                    break;
            }
            return false;
        }

        private void InitSPI(Context context) {
            mCarApiClient = Car.createCar(context, new CarConnectionCallback() {
                @Override
                public void onConnected(Car car) {
                    isConnected = true;
                    initVendorExtensionManager();
                }

                @Override
                public void onDisconnected(Car car) {
                    isConnected = false;
                }
            });
            mCarApiClient.connect();
        }

        //发送SPI消息
        private void sendSPI(int i, Integer[] integers) {
            Log.d(TAG, "sendSPI" + Arrays.toString(integers));
            if (mCarVendorExtensionManager == null) {
                Log.e(TAG, "sendSPI: mCarVendorExtensionManager is null");
                return;
            }
            try {
                //发送SPI给MCU
                //第一个参数为第三个参数的数据类型
                //第二个参数为ID，与SPI的commandId会对应起来，具体看CarVendorExtensionManager类的定义。如下例子ID_SV_REQUEST_TO_RESET = 0x21405502;即对应5502的SPI
                //第三个参数是传给MCU的参数
                mCarVendorExtensionManager.setGlobalProperty(Integer[].class, i, integers); //set方法
            } catch (android.car.CarNotConnectedException e) {
                e.printStackTrace();
            }
        }

        //发送长消息的SPI消息
        private void sendLongSPI(int i, int j, Integer[] integers) {
            if (mCarVendorExtensionManager == null) {
                Log.e(TAG, "sendLongSPI: mCarVendorExtensionManager is null");
                return;
            }
            try {
                Log.d(TAG, "sendLongSPI: i = " + i + ", j = " + j);
                mCarVendorExtensionManager.setProperty(Integer[].class, i, j, integers);
            } catch (android.car.CarNotConnectedException e) {
                e.printStackTrace();
            }
        }

        private void setBrightnessMode(boolean isAuto) {
            mBrightnessMode = isAuto ? MODE_AUTO : MODE_MANUAL;
            sendSPI(0x21404410, new Integer[]{mBrightnessMode, MODE_DAY});
        }

        private void setBrightnessInfo(int brightness) {
            Log.d(TAG, "setBrightnessInfo: start --> " + System.currentTimeMillis());
            try {
                IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
                if (power != null) {
                    power.setTemporaryScreenBrightnessSettingOverride(brightness);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "setBrightnessInfo: done --> " + System.currentTimeMillis());
        }

        private void getBrightnessInfo() {
            sendSPI(0x21404203, new Integer[]{0});
        }
    }
    /*
     * 子线程Lopper --> end
     */
}

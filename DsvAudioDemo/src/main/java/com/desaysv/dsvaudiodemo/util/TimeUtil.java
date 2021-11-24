package com.desaysv.dsvaudiodemo.util;

public class TimeUtil {

    public static String intToTimeStr(int time) {
        if (time <= 0) return "00:00";
        int hour = 0;
        int min = 0;
        int sec = 0;
        String result;
        sec = time / 1000;
        if (sec > 59) {
            min = sec / 60;
            sec = sec % 60;
        }
        if (min > 59) {
            hour = min / 60;
            min = min % 60;
        }
        if (hour > 0) {
            result = getTwoLength(hour) + ":" + getTwoLength(min) + ":" + getTwoLength(sec);
        } else {
            result = getTwoLength(min) + ":" + getTwoLength(sec);

        }
        return result;
    }

    private static String getTwoLength(final int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }
}

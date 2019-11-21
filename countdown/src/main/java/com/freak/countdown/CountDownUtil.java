package com.freak.countdown;

import android.util.Log;

/**
 * 字符串工具
 */
public class CountDownUtil {
    /**
     * 整数(秒数)转换为时分秒数组
     *
     * @param time 秒数
     * @return 时分秒
     */
    public static String[] secToTimes(long time, int type, boolean isCloseDay) {
        int count = 3;
        if (type == 1) {
            if (isCloseDay) {
                count = 3;
            } else {
                count = 4;
            }
        } else {
            count = 3;
        }
        String[] timeStrings = new String[count];
        int day;
        int hour;
        int minute;
        int second;
        if (time <= 0) {
            timeStrings[0] = "00";
            timeStrings[1] = "00";
            timeStrings[2] = "00";
            return timeStrings;
        } else {
            //计算分钟
            minute = (int) (time / 60);
            if (minute < 60) {
                second = (int) (time % 60);
                timeStrings[0] = "00";
                timeStrings[1] = unitFormat(minute);
                timeStrings[2] = unitFormat(second);
            } else {
                //计算小时
                hour = minute / 60;
                //判断显示模式是否是Chinese
                if (type == 1) {
                    if (isCloseDay) {
                        minute = minute % 60;
                        second = (int) (time - hour * 3600 - minute * 60);
                        Log.e("TAG", " 小时 " + hour + " 分钟 " + minute + " 秒 " + second);
                        timeStrings[0] = unitFormat(hour);
                        timeStrings[1] = unitFormat(minute);
                        timeStrings[2] = unitFormat(second);
                    } else {
                        if (hour < 24) {
                            minute = minute % 60;
                            second = (int) (time - hour * 3600 - minute * 60);
                            timeStrings[0] = unitFormat(hour);
                            timeStrings[1] = unitFormat(minute);
                            timeStrings[2] = unitFormat(second);
                        } else {
                            day = hour / 24;
                            hour = hour % 24;
                            minute = minute % 60;
                            second = (int) (time - day * 24 * 3600 - hour * 3600 - minute * 60);
                            Log.e("TAG", "天 " + day + " 小时 " + hour + " 分钟 " + minute + " 秒 " + second);
                            timeStrings[0] = unitFormat(hour);
                            timeStrings[1] = unitFormat(minute);
                            timeStrings[2] = unitFormat(second);
                            timeStrings[3] = unitFormat(day);
                        }
                    }

                } else {
                    minute = minute % 60;
                    second = (int) (time - hour * 3600 - minute * 60);
                    Log.e("TAG", " 小时 " + hour + " 分钟 " + minute + " 秒 " + second);
                    timeStrings[0] = unitFormat(hour);
                    timeStrings[1] = unitFormat(minute);
                    timeStrings[2] = unitFormat(second);
                }


            }
        }
        return timeStrings;
    }

    // 格式化事件规格
    private static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }
}

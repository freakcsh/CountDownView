package com.freak.countdown;

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
    public static String[] secToTimes(long time) {
        String[] timeStrings = new String[3];
        int hour;
        int minute;
        int second;
        if (time <= 0) {
            timeStrings[0] = "00";
            timeStrings[1] = "00";
            timeStrings[2] = "00";
            return timeStrings;
        } else {
            minute = (int) (time / 60);
            if (minute < 60) {
                second = (int) (time % 60);
                timeStrings[0] = "00";
                timeStrings[1] = unitFormat(minute);
                timeStrings[2] = unitFormat(second);
            } else {
                hour = minute / 60;
//                if (hour > 99) {
//                    timeStrings[0] = "99";
//                    timeStrings[1] = "59";
//                    timeStrings[2] = "59";
//                    return timeStrings;
//                }
                minute = minute % 60;
                second = (int) (time - hour * 3600 - minute * 60);
                timeStrings[0] = unitFormat(hour);
                timeStrings[1] = unitFormat(minute);
                timeStrings[2] = unitFormat(second);
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

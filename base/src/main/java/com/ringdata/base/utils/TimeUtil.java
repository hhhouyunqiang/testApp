package com.ringdata.base.utils;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by admin on 17/11/24.
 */

public class TimeUtil {

    public static String survey(Long dateTime) {
        if (dateTime == null || dateTime == 0) {
            return "";
        }
        Date myDate = new Date(dateTime);
        Date nowDate = new Date();

        int nowYear = new Date().getYear();
        int myYear = myDate.getYear();
        // 不同年
        if (nowYear != myYear) {
            return DateFormat.format("yyyy-MM-dd", dateTime).toString();
        }

        int nowMonth = nowDate.getMonth();
        int myMonth = myDate.getYear();
        // 同年，不同月
        if (nowMonth != myMonth) {
            return DateFormat.format("MM-dd", dateTime).toString();
        }

        int nowDay = nowDate.getDay();
        int myDay = myDate.getDay();
        if (nowDay - myDay == 1) {
            return "昨天";
        }
        // 同年，同月，不同天
        if (nowDay != myDay) {
            return DateFormat.format("MM-dd HH:mm", dateTime).toString();
        }
        // 同年，同月，同天
        return "今天:";
    }

    public static String downloadTime(Long dateTime) {
        if (dateTime == null || dateTime == 0) {
            return "--";
        }
        long minute = 60 * 1000;// 1分钟
        long hour = 60 * minute;// 1小时
        long day = 24 * hour;// 1天
        long month = 31 * day;// 月
        long year = 12 * month;// 年
        Date date = new Date(dateTime);

        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static String question(Long dateTime) {
        if (dateTime == null || dateTime == 0) {
            return "";
        }
        Date myDate = new Date(dateTime);
        Date nowDate = new Date();

        int nowYear = new Date().getYear();
        int myYear = myDate.getYear();
        // 不同年
        if (nowYear != myYear) {
            return DateFormat.format("yyyy-MM-dd HH:mm", dateTime).toString();
        }

        int nowMonth = nowDate.getMonth();
        int myMonth = myDate.getMonth();
        // 同年，不同月
        if (nowMonth != myMonth) {
            return DateFormat.format("MM-dd HH:mm", dateTime).toString();
        }

        int nowDay = nowDate.getDay();
        int myDay = myDate.getDay();
//        if(nowDay - myDay == 1){
//            return "昨天" + DateFormat.format("HH:mm", dateTime).toString();
//        }
        // 同年，同月，不同天
        if (nowDay != myDay) {
            return DateFormat.format("MM-dd HH:mm", dateTime).toString();
        }
        // 同年，同月，同天
        return DateFormat.format("HH:mm", dateTime).toString();
        //return "今天:" + DateFormat.format("HH:mm", dateTime).toString();
    }

    public static String timePick(Date date) {
        return DateFormat.format("yyyy-MM-dd HH:mm", date).toString();
    }

    public static String millisecond2Time(Integer millisecond) {
        if (millisecond == null) {
            return "0秒";
        }
        int totalSecond = millisecond / 1000;
        int mSecond = totalSecond % 60;
        int mMinute = totalSecond / 60 % 60;
        if (mMinute > 0) {
            return mMinute + "分" + mSecond + "秒";
        } else {
            return mSecond + "秒";
        }
    }

    public static String second2Time(Integer second) {
        if (second == null) {
            return "0秒";
        }
        int mSecond = second % 60;
        int mMinute = second / 60 % 60;
        if (mMinute > 0) {
            return mMinute + "分" + mSecond + "秒";
        } else {
            return mSecond + "秒";
        }
    }

    public static String updateTime(long dateTime) {

        long minute = 60 * 1000;// 1分钟
        long hour = 60 * minute;// 1小时
        long day = 24 * hour;// 1天
        long month = 31 * day;// 月
        long year = 12 * month;// 年
        Date date = new Date(dateTime);

        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }
}

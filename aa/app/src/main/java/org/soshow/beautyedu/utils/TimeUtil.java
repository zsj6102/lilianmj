package org.soshow.beautyedu.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;

/**
 * 时间工具类
 * 
 * @author heq
 * 
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long HALF_AN_HOUR = 1800000L;

    private static final String ONE_SECOND_AGO = "刚刚";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String TODAY = "今天";
    private static final String YESTERDAY = "昨天";
    private static final String BEFORE_YESTERDAY = "前天";

    public static String data() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String time = dff.format(new Date());
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    // 获取时间列表
    public static ArrayList<String> getTimeList() {
        ArrayList<String> timeList = new ArrayList<String>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        for (int i = month; i <= 12; i++) {
            if (i < 10) {
                timeList.add((year - 1) + "-0" + i);
            } else {
                timeList.add((year - 1) + "-" + i);
            }
        }
        for (int i = 1; i <= month; i++) {
            if (i < 10) {
                timeList.add(year + "-0" + i);
            } else {
                timeList.add(year + "-" + i);
            }

        }
        Collections.reverse(timeList);
        return timeList;

    }

    // 获取年份列表
    public static ArrayList<String> getYearList() {
        ArrayList<String> yearList = new ArrayList<String>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 2; i++) {
            yearList.add(year - i + "");
        }
        return yearList;

    }

    // 获取当前年份
    public static String getCurrentYear() {
        String date = null;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        date = year + "";
        return date;
    }

    // 获取当前年月
    public static String getCurrentYearAndMonth() {
        String date = null;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (month < 10) {
            date = year + "-0" + month;
        } else {
            date = year + "-" + month;
        }
        return date;
    }

    // 获取当前中文格式的年月
    public static String getCNCurrentYearAndMonth() {
        String date = null;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (month < 10) {
            date = year + "年0" + month + "月";
        } else {
            date = year + "年" + month + "月";
        }
        return date;
    }

    public static String getCurrentTime(String format) {
        SimpleDateFormat dateForamt = new SimpleDateFormat(format);
        Date curDate = new Date(System.currentTimeMillis());
        String time = dateForamt.format(curDate);
        return time;
    }

    public static ArrayList<String> monthFirst2CurrentDay() {
        ArrayList<String> dates = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateForamt = new SimpleDateFormat("yyyy-MM-dd");
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= day; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            Date date = calendar.getTime();
            String time = dateForamt.format(date);
            dates.add(time);
        }
        Collections.reverse(dates);
        return dates;

    }

    public static String getStringTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        return format.format(new Date(time*1000));
    }

    public static String getStringdata(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return format.format(new Date(time));
    }

    public static String getStringdatas(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date(time * 1000));
    }
    
    public static String getStringDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return format.format(new Date(time));
    }
    
    public static String getStringDays(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMdd");
        return format.format(new Date(time*1000));
    }

    public static String getStringYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time* 1000));
    }
    
    public static String getStringTypeYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(new Date(time* 1000));
    }

    public static String getYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date(time));
    }

    public static String getMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMdd");
        return format.format(new Date(time));
    }

    public static String getStringYearAndMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getDateFormat(long timesamp) {

        long delta = System.currentTimeMillis() - timesamp;
        Calendar cal = Calendar.getInstance();
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        if (delta < 1L * ONE_MINUTE) {
            return ONE_SECOND_AGO;
        }
        if (delta < HALF_AN_HOUR) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < hours * ONE_HOUR + minute * ONE_MINUTE) {
            // long hours = toHours(delta);
            // return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
            return getOnlyTime(TODAY, timesamp);
        }
        if (delta < (24L + hours) * ONE_HOUR + minute * ONE_MINUTE) {
            // return "昨天";
            return getOnlyTime(YESTERDAY, timesamp);
        }
        if (delta < (48L + hours) * ONE_HOUR + minute * ONE_MINUTE) {
            return getOnlyTime(BEFORE_YESTERDAY, timesamp);
        }
        // if (delta < 30L * ONE_DAY) {
        // long days = toDays(delta);
        // if (days <= 6) {
        // return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        // } else {
        // return outputYear(timesamp);
        // }
        // }
        return outputYear(timesamp);
    }

    private static String getOnlyTime(String dayDes, long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return dayDes + " " + format.format(time);
    }

    private static String getBeforeYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    private static String getChineData(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        return format.format(new Date(time));
    }

    private static String getThisYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
        return format.format(new Date(time));
    }

    private static String outputYear(long timesamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        Date dateYear = new Date(timesamp);
        int year = Integer.parseInt(format.format(dateYear));
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        if (year == thisYear) {
            return getThisYear(timesamp);
        } else {
            return getBeforeYear(timesamp);
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    // 将字符串转为时间戳
    public static Long getTime(String user_time) {
        SimpleDateFormat simpleDateFormat;
        if (user_time.contains("-")) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        }

        Date date = null;
        try {
            date = simpleDateFormat.parse(user_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeStemp = date.getTime() / 1000;
        return timeStemp;
    }

    /**
     * 将时间格式的转成文字描述时间
     * @param time 时间格式的时间
     * @return
     */
    public static String formatTime(String time) {
        return getDateFormat(getTime(time));
    }
}

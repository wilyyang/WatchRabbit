package wily.apps.watchrabbit.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wily.apps.watchrabbit.AppConst;

public class DateUtil {
    public static final long MILLISECOND_TO_MINUTE = 60 * 1000;
    public static final long MILLISECOND_TO_SECOND = 1000;

    public static final long ONEDAY_TO_MILLISECOND = 86400000;

    public static String getDateString(long time){
        DateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public static String getTimeString(long time){
        DateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getDateStringDayLimit(long time){
        DateFormat format = new SimpleDateFormat("yy/MM/dd");
        return format.format(new Date(time));
    }

    // type : Calendar.DATE
    public static int getDateNum(long time, int type){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        return cal.get(type);
    }

    public static long getDateLong(int year, int month, int day, int hour, int minute, int second){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(year, month, day, hour, minute, second);
        return cal.getTimeInMillis();
    }

    public static long getDateLongAfter(long time, int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        cal.add(Calendar.DATE, day);
        return cal.getTimeInMillis();
    }

    public static long getDateLongBefore(long time, int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        cal.add(Calendar.DATE, -day);
        return cal.getTimeInMillis();
    }

    public static long convertDate(long time){
        Calendar cal = Calendar.getInstance();
        cal.set(getDateNum(time, Calendar.YEAR),getDateNum(time, Calendar.MONTH),getDateNum(time, Calendar.DAY_OF_MONTH),0,0,0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long convertDateAndTime(long date, long time){
        Calendar cal = Calendar.getInstance();
        cal.set(getDateNum(date, Calendar.YEAR),getDateNum(date, Calendar.MONTH),getDateNum(date, Calendar.DAY_OF_MONTH),getDateNum(time, Calendar.HOUR_OF_DAY),getDateNum(time, Calendar.MINUTE),getDateNum(time, Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}

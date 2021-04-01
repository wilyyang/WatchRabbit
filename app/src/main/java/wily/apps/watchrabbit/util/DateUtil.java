package wily.apps.watchrabbit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final long MILLISECOND_TO_MINUTE = 60 * 1000;
    public static final long MILLISECOND_TO_SECOND = 1000;

    public static final long ONEDAY_TO_MILLISECOND = 86400000;

    public static String getDateString(long time){
        DateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        return format.format(new Date(time));
    }

    // type : Calendar.DAY_OF_YEAR
    public static int getDateNum(long time, int type){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));

        return cal.get(type);
    }

    public static long getDateLong(int year, int month, int day, int hour, int minute, int second){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute, second);
        return cal.getTimeInMillis();
    }

    public static long getDateLongAfter(long time, int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        cal.add(Calendar.DAY_OF_YEAR, day);
        return cal.getTimeInMillis();
    }

    public static long getDateLongBefore(long time, int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        cal.add(Calendar.DAY_OF_YEAR, -day);
        return cal.getTimeInMillis();
    }

    public static long convertDate(long time){
        Calendar cal = Calendar.getInstance();
        cal.set(getDateNum(time, Calendar.YEAR),getDateNum(time, Calendar.MONTH),getDateNum(time, Calendar.DAY_OF_MONTH),0,0,0);
        return cal.getTimeInMillis();
    }
}

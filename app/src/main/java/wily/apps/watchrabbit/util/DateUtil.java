package wily.apps.watchrabbit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final int DATE_YEAR = 1;
    public static final int DATE_MONTH = 2;
    public static final int DATE_DAY = 3;
    public static final int DATE_HOUR = 4;
    public static final int DATE_MINUTE = 5;
    public static final int DATE_SECOND = 6;

    public static String getDateString(long time){
        DateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public static int getDateNum(long time, int type){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));

        return cal.get(type);
    }
}

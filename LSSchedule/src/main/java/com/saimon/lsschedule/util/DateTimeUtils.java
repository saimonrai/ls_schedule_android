package com.saimon.lsschedule.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created at 9:18 PM on 1/14/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class DateTimeUtils {
    public static final String NEPAL_TIMEZONE_ID = "Asia/Kathmandu";
    public static final TimeZone NEPAL_TIMEZONE = TimeZone.getTimeZone(NEPAL_TIMEZONE_ID);

    public static final DateFormat DATE_FORMAT_MONTH_DAY_YEAR_NEPAL_TZ;
    static {
        DATE_FORMAT_MONTH_DAY_YEAR_NEPAL_TZ = new SimpleDateFormat("MMM d, y");
        DATE_FORMAT_MONTH_DAY_YEAR_NEPAL_TZ.setTimeZone(TimeZone.getTimeZone(NEPAL_TIMEZONE_ID));
    }

//    public static String formatDate(Date date) {
//        return DATE_FORMAT_MONTH_DAY_YEAR_NEPAL_TZ.format(date);
//    }

    public static String dateInNepalFormatted() {
        Date date = dateInNepal();
        return DATE_FORMAT_MONTH_DAY_YEAR_NEPAL_TZ.format(date);
    }

    public static Date dateInNepal() {
        return date(NEPAL_TIMEZONE_ID);
    }

    public static Date date(String timezoneId) {
        TimeZone zone = TimeZone.getTimeZone(timezoneId);
        return date(zone);
    }

    public static Date date(TimeZone timezone) {
        return Calendar.getInstance(timezone).getTime();
    }

    public static Calendar nepaliCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone(NEPAL_TIMEZONE_ID));
    }

}

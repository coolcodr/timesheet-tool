package com.coolcodr.timesheet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String TIMESHEET_FILENAME = "timesheet";

    public static long SECOND = 1000;
    public static long MINUTE = SECOND * 60;
    public static long HOUR = 60 * MINUTE;

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static SimpleDateFormat getDateTimeFormatter() {
        return new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
    }

    public static SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    }

    public static NumberFormat getOneDecimalFormatter() {
        return new DecimalFormat("#0.0");
    }

    public static DatetimeComparator getDatetimeComparator() {
        return new DatetimeComparator();
    }
}

package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper class for performing calculations related to calendar building
 *
 * Created by acheshihin on 8/4/2016.
 */
public class CalendarUtils {

    private static final String TAG = CalendarUtils.class.getSimpleName();

    private static final Calendar CALENDAR;
    private static final DateFormat EEE_DAY_OF_WEEK_FORMAT;

    static {
        CALENDAR = Calendar.getInstance();
        EEE_DAY_OF_WEEK_FORMAT = new SimpleDateFormat("EEE", Locale.getDefault());
    }

    public static String getDayOfWeekNameThreeChars(@NonNull Date date){

        String result = EEE_DAY_OF_WEEK_FORMAT.format(date).toLowerCase(Locale.getDefault());
        // Make first char in upper case
        result = upperCaseWords(result);
        return result;
    }

    private static String upperCaseWords(@NonNull String line) {

        line = line.trim().toLowerCase();
        String data[] = line.split("\\s");
        line = "";

        for(int i=0; i < data.length; i++) {
            if (data[i].length() > 1) {
                line = line + data[i].substring(0, 1)
                        .toUpperCase(Locale.getDefault()) + data[i].substring(1) + " ";
            } else {
                line = line + data[i].toUpperCase(Locale.getDefault());
            }
        }
        return line.trim();
    }

    /**
     Return the number of days in the given month. The returned value depends on the year as
     well, because of leap years. Returns <tt>null</tt> if either year or month are
     absent. WRONG - should be public??
     Package-private, needed for interval calcs.
     */
    static Integer getNumDaysInMonth(Integer year, Integer month) {
        Integer result = null;
        if (year != null && month != null) {
            if (month == 1) {
                result = 31;
            }
            else if (month == 2) {
                result = isLeapYear(year) ? 29 : 28;
            }
            else if (month == 3) {
                result = 31;
            }
            else if (month == 4) {
                result = 30;
            }
            else if (month == 5) {
                result = 31;
            }
            else if (month == 6) {
                result = 30;
            }
            else if (month == 7) {
                result = 31;
            }
            else if (month == 8) {
                result = 31;
            }
            else if (month == 9) {
                result = 30;
            }
            else if (month == 10) {
                result = 31;
            }
            else if (month == 11) {
                result = 30;
            }
            else if (month == 12) {
                result = 31;
            }
            else {
                throw new AssertionError("Month is out of range 1..12:" + month);
            }
        }
        return result;
    }

    /**
     * Checks if provided year is leap year and returns result accordingly
     *
     * @param year, desired year
     * @return true|false
     */
    private static boolean isLeapYear(Integer year) {

        boolean result = false;
        if (year % 100 == 0) {
            // this is a century year
            if (year % 400 == 0) {
                result = true;
            }
        }
        else if (year % 4 == 0) {
            result = true;
        }
        return result;
    }

    public static Date getTomorrow(@NonNull final Date dayDate) {

        Calendar calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(dayDate);

        // Add one day
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date getYesterday(@NonNull final Date dayDate) {

        Calendar calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(dayDate);
        // Remove one day
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static Date getNextMonth(@NonNull final Date dayDate){

        Calendar calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(dayDate);
        // 1-based
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Change calendar by adding one month
        calendar.add(Calendar.MONTH,1);
        return calendar.getTime();
    }

    public static Date getPreviousMonth(@NonNull final Date dayDate){

        Calendar calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(dayDate);
        // 1-based
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // Change calendar by removing one month
        calendar.add(Calendar.MONTH,-1);
        return calendar.getTime();
    }

    public static Calendar getCalendarFrom(@NonNull final Date date){

        Calendar calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar setFirstDayOfMonth(Calendar calendar){

        if (calendar == null){
            throw new IllegalArgumentException("Calendar instance can't be null");
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    /**
     * Creates and returns {@link Calendar} object built according to current timestamp
     *
     * @return {@link Calendar}
     */
    public static Calendar getCalendarForToday(){

        Calendar calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(new Date(System.currentTimeMillis()));
        return calendar;
    }

    /**
     * Check if provided day is today and returns result accordingly
     *
     * @param day {@link Day}
     * @return true|false
     */
    public static boolean isToday(@NonNull Day day){

        return DateUtils.isToday(day.getDate().getTime());
    }

    public static List<Date> generateInitialMonthList(@NonNull Date desiredDate){

        final Date previousMonth = CalendarUtils.getPreviousMonth(desiredDate);
        final Date nextMonth = CalendarUtils.getNextMonth(desiredDate);

        return generateMonthRange(CalendarUtils.getCalendarFrom(previousMonth),
                CalendarUtils.getCalendarFrom(nextMonth));
    }

    public static List<Date> generateMonthRange(final Calendar calendarStart,
                                                final Calendar calendarEnd){

        List<Date> dateList = new ArrayList<>();
        // Set day of month to first day of mont (1-based)
        calendarStart.set(Calendar.DAY_OF_MONTH, 1);
        calendarEnd.set(Calendar.DAY_OF_MONTH, 1);

        // Add very first item
        dateList.add(calendarStart.getTime());

        // Add next months
        while (calendarStart.before(calendarEnd)){
            calendarStart.add(Calendar.MONTH, 1);
            dateList.add(calendarStart.getTime());

            final int year = calendarStart.get(Calendar.YEAR);
            // FYI: 0-based
            final int month = calendarStart.get(Calendar.MONTH);
            final int day = calendarStart.get(Calendar.DAY_OF_MONTH);

            // TODO: Delete after tests
            Log.i(TAG, getHumanFriendlyCalendarRepresentation(calendarStart));
        }

        return dateList;
    }

    public static String getHumanFriendlyCalendarRepresentation(@NonNull Calendar calendar){

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.getDefault(), "%d/%d/%d", day, month, year);
    }
}

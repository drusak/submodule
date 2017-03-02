package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;
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

    /**
     * Checks if provided calendar is set to today time and returns result accordingly. True
     * if it represents today, false otherwise
     * (Please note: hours are not taken in account)
     *
     * @param calendar instance of {@link Calendar}
     * @return true|false
     */
    public static boolean isToday(Calendar calendar){

        final Calendar todayCalendar = getCalendarForToday();
        return isSameDay(todayCalendar, calendar);
    }

    /**
     * Checks if provided date is set to today time and returns result accordingly. True
     * if it represents today, false otherwise
     * (Please note: hours are not taken in account, compares only YEAR, MONTH, DAY)
     *
     * @param date instance of {@link Date}
     * @return true|false
     */
    public static boolean isToday (Date date){
        final Calendar dateCalendar = (Calendar) CALENDAR.clone();
        dateCalendar.setTime(date);
        return isToday(dateCalendar);
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

    /**
     * Calculates date of 1 day of next month and returns it
     *
     * @param month {@link MixedVisibleMonth}
     * @return {@link Date}
     */
    public static Date getNextMonth(@NonNull MixedVisibleMonth month){

        return getNextMonth(month.getCurrentMonth().getDay(0).getDate());
    }

    /**
     * Calculates date of 1 day of previous month and returns it
     *
     * @param month {@link MixedVisibleMonth}
     * @return {@link Date}
     */
    public static Date getPreviousMonth(@NonNull MixedVisibleMonth month){

        return getPreviousMonth(month.getCurrentMonth().getDay(0).getDate());
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
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar;
    }

    /**
     * @return true if @param day is in same month as current date
     */
    public static boolean isSameMonthAsCurrent(@NonNull final Day day) {
        Calendar todayCalendar = (Calendar) CALENDAR.clone();
        todayCalendar.setTimeInMillis(System.currentTimeMillis());
        return todayCalendar.get(Calendar.YEAR) == day.getCalendar().get(Calendar.YEAR) &&
                todayCalendar.get(Calendar.MONTH) == day.getCalendar().get(Calendar.MONTH);
    }

    /**
     * Check if provided day is today and returns result accordingly
     *
     * @param day {@link Day}
     * @return true|false
     */
    /*public static boolean isToday(@NonNull final Day day){

        return DateUtils.isToday(day.getDate().getTime());
    }*/

    /**
     * Checks if provided day is today and returns result accordingly
     *
     * @param day {@link Day}
     * @return true|false
     */
    public static boolean isToday(@NonNull final Day day){

        final Calendar todayCalendar = (Calendar) CALENDAR.clone();
        todayCalendar.setTimeInMillis(System.currentTimeMillis());

        final Calendar checkedCalendar = day.getCalendar();
        return isSameDay(todayCalendar, checkedCalendar);
    }

    public static boolean isSameDay(@NonNull final Calendar day1, @NonNull final Calendar day2){

        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR)
                &&  day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
                && day1.get(Calendar.DAY_OF_MONTH) == day2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameDay(@NonNull final Date day1, @NonNull final Date day2){
        final Calendar day1Calendar = (Calendar) CALENDAR.clone();
        day1Calendar.setTime(day1);

        final Calendar day2Calendar = (Calendar) CALENDAR.clone();
        day2Calendar.setTime(day2);

        return isSameDay(day1Calendar, day2Calendar);
    }

    /**
     * @return true if today day is before or same as @param day
     */
    public static boolean isTodayBeforeOrSame(@NonNull final Day day) {
        // return DateUtils.isToday(day.getDate().getTime());
        final Calendar todayCalendar = (Calendar) CALENDAR.clone();
        todayCalendar.setTimeInMillis(System.currentTimeMillis());

        final Calendar checkedCalendar = day.getCalendar();

        if (todayCalendar.get(Calendar.YEAR) > checkedCalendar.get(Calendar.YEAR)) {
            return false;
        } else {
            if (todayCalendar.get(Calendar.YEAR) < checkedCalendar.get(Calendar.YEAR)) {
                return true;
            } else {
                if (todayCalendar.get(Calendar.MONTH) > checkedCalendar.get(Calendar.MONTH)) {
                    return false;
                } else {
                    if (todayCalendar.get(Calendar.MONTH) < checkedCalendar.get(Calendar.MONTH)) {
                        return true;
                    } else {
                        if (todayCalendar.get(Calendar.DAY_OF_MONTH) > checkedCalendar.get(Calendar.DAY_OF_MONTH)) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }

    }

    public static List<Date> generateInitialMonthList(@NonNull final Date desiredDate){
        // generate range of 5 initial months
        final Date previousMonth = CalendarUtils.getPreviousMonth(desiredDate);
        final Date previousPreviousMonth = CalendarUtils.getPreviousMonth(previousMonth);
        final Date nextMonth = CalendarUtils.getNextMonth(desiredDate);
        final Date nextNextMonth = CalendarUtils.getNextMonth(nextMonth);

        return generateMonthRange(CalendarUtils.getCalendarFrom(previousPreviousMonth),
                CalendarUtils.getCalendarFrom(nextNextMonth));
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
        }

        return dateList;
    }

    public static String getHumanFriendlyCalendarRepresentation(@NonNull Calendar calendar){

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.getDefault(), "%d/%d/%d", day, month, year);
    }

    /**
     * Adjusts origin calendar to same but day time is set to one second AFTER midnight
     *
     * @param originCalendar {@link Calendar} that should be converted
     * @return {@link Calendar} as result of conversion
     */
    @NonNull
    public static Calendar getCalendarBeginOfDay(@NonNull Calendar originCalendar){

        final Calendar  calendar = (Calendar) originCalendar.clone();

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * Adjusts origin calendar to same but day time is set to one second BEFORE midnight
     *
     * @param originCalendar {@link Calendar} that should be converted
     * @return {@link Calendar} as result of conversion
     */
    @NonNull
    public static Calendar getCalendarEndOfDay(Calendar originCalendar){

        final Calendar  calendar = (Calendar) originCalendar.clone();

        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar;
    }

    /**
     * Adjusts origin date to same but day time is set to one second AFTER midnight
     *
     * @param date {@link Date} that should be converted
     * @return {@link Date} as result of conversion
     */
    public static Date getDateBeginOfDay(@NonNull Date date){

        final Calendar  calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Adjusts origin date to same but day time is set to one second BEFORE midnight
     *
     * @param date {@link Date} that should be converted
     * @return {@link Date} as result of conversion
     */
    public static Date getDateEndOfDay(Date date){

        final Calendar  calendar = (Calendar) CALENDAR.clone();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * Checks
     *
     * @param startDate {@link Date}
     * @param endDate {@link Date}
     * @return {@link int} amount of month
     */
    public static int monthsBetween(Date startDate, Date endDate) {

        Calendar cal = CalendarUtils.getCalendarForToday();
        if (startDate.before(endDate)) {
            cal.setTime(startDate);
        } else {
            cal.setTime(endDate);
            endDate = startDate;
        }

        int count = 0;
        while (cal.getTime().before(endDate)) {
            cal.add(Calendar.MONTH, 1);
            count++;
        }
        return count - 1;
    }

    /**
     * Counting hash code of calendar date, so using only year-month-day combination, and 00:00:00.000 time
     * @return hashcode of @param calendar date
     */
    public static int getHashKey(@NonNull final Calendar calendar){
        // TODO: Check performance, maybe better to use processing locally
        return CalendarUtils.getCalendarBeginOfDay(calendar).getTime().toString().hashCode();
    }

    /**
     * Counting hash code of Date, so using only year-month-day combination, and 00:00:00.000 time
     * @see #getHashKey(Date)
     * @return hashcode of @param date
     */
    public static int getHashKey(@NonNull final Date date){
        // TODO: consider making here dedicated implementation in order to reduce usage of stack memory
        return getHashKey(CalendarUtils.getCalendarFrom(date));
    }
}

package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Data for single day item which will be represented as grid item of {@link CalendarWidget}
 * FYI:
 * http://tadtech.blogspot.co.il/2007/03/performance-clone-vs-new.html
 * Created by acheshihin on 8/4/2016.
 */
public class Day {

    private Date mDate;
    private DayState mDayState;
    private Calendar mCalendar;

    public Day(final Date date, @NonNull DayState dayState){

        if (date == null){
            throw new IllegalArgumentException("Provided argument can't be null");
        }

        mDate = date;
        mCalendar = CalendarUtils.getCalendarFrom(date);
        mDayState = dayState;
    }

    public Calendar getCalendar(){
        return mCalendar;
    }

    /**
     * Returns {@link Date} related to current day
     *
     * @return {@link Date}
     */
    public Date getDate(){
        return mDate;
    }

    public int getMonthDay(){
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns {@link DayState} object related to current {@link Day} object
     *
     * @return {@link DayState}
     */
    public DayState getDayState(){
        return mDayState;
    }

    /**
     * Checks if current day instance is actually today and returns result accordingly, true
     * if today, false otherwise
     *
     * @return true|false
     */
    public boolean isToday(){
        if (mDate == null){
            throw new IllegalStateException("Day was not initialized");
        }
        return DateUtils.isToday(mDate.getTime());
    }
}

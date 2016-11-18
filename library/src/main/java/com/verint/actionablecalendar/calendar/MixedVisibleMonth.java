package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains data required to represent single month and partially previous and next month as well
 * when current month not starts at the beginning of row and not finishes at the end
 *
 * Created by acheshihin on 8/4/2016.
 */
public class MixedVisibleMonth {

    private VisibleMonth mPreviousMonth;
    private VisibleMonth mCurrentMonth;
    private VisibleMonth mNextMonth;

    private List<String> mWeekDayNameList = new ArrayList<>();

    /**
     Creates mixed visible month which contains data that should be shown within
     * {@link CalendarWidget} which includes day from previous month, current month and next month
     *
     * @param previousMonth {@link VisibleMonth} previous month
     * @param currentMonth {@link VisibleMonth} current month
     * @param nextMonth {@link VisibleMonth} next month
     */
    public MixedVisibleMonth(final VisibleMonth previousMonth,
                             final VisibleMonth currentMonth,
                             final VisibleMonth nextMonth){

        if (previousMonth == null || currentMonth == null || nextMonth == null){
            throw new IllegalArgumentException("Provided arguments can't be null");
        }

        mPreviousMonth = previousMonth;
        mCurrentMonth = currentMonth;
        mNextMonth = nextMonth;
    }

    /**
     * Returns list of {@link String} with week day names for specific month
     *
     * @return List of {@link String} while each contains single day name in 3 chars representation
     */
    public List<String> getWeekDayNameList(){
        return mWeekDayNameList;
    }

    /**
     * Returns {@link Day} according to position we are interested in while
     *
     * @param position numeric value of item we are interested in
     * @return {@link Day}
     */
    public Day getDay(int position){
        if (position < mPreviousMonth.size()){
            return mPreviousMonth.getDay(position);
        }
        position -= mPreviousMonth.size();
        if (position < mCurrentMonth.size()){
            return mCurrentMonth.getDay(position);
        }
        position -= mCurrentMonth.size();
        return mNextMonth.getDay(position);
    }

    /**
     * Returns previous month instance
     *
     * @return {@link VisibleMonth}
     */
    public VisibleMonth getPreviousMonth(){
        return mPreviousMonth;
    }

    /**
     * Returns current month instance
     *
     * @return {@link VisibleMonth}
     */
    public VisibleMonth getCurrentMonth(){
        return mCurrentMonth;
    }

    /**
     * Returns next month instance
     *
     * @return {@link VisibleMonth}
     */
    public VisibleMonth getNextMonth(){
        return mNextMonth;
    }

    /**
     * Returns total amount of items within current {@link MixedVisibleMonth}
     *
     * @return int, numeric value of items for month representation
     */
    public int getCount(){
        return mPreviousMonth.size() + mCurrentMonth.size() + mNextMonth.size();
    }

    /**
     * Returns content of {@code mPreviousMonth}, {@code mCurrentMonth}, {@code mNextMonth}
     * as list of {@link Day} objects
     *
     * @return list of {@link Day}
     */
    public List<Day> getDayList(){

        final List<Day> dayList = new ArrayList<>();

        dayList.addAll(mPreviousMonth.getDayList());
        dayList.addAll(mCurrentMonth.getDayList());
        dayList.addAll(mNextMonth.getDayList());

        return dayList;
    }

    @NonNull
    public List<Day> getDayListWithHeaders(){

        final List<Day> dayList = new ArrayList<>();

        dayList.addAll(mPreviousMonth.getDayList());

        if (mCurrentMonth.size() > 0) {
            Day firstDay = mCurrentMonth.getDay(0);
            Day monthHeader = new Day(firstDay.getDate(), new DayState(DayState.DayType.MONTH_HEADER));
            dayList.add(0, monthHeader);
            dayList.addAll(mCurrentMonth.getDayList());
        }

        dayList.addAll(mNextMonth.getDayList());

        return dayList;
    }
}

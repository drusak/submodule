package com.verint.actionablecalendar.calendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Visible month that contains list of visible {@link Day} object for specific month
 *
 * Created by acheshihin on 8/4/2016.
 */
public class VisibleMonth {

    private final List<Day> mDayList;

    /**
     *
     * @param dayList lsit of {@link Day} objects
     */
    public VisibleMonth(List<Day> dayList){

        if (dayList == null){
            throw new IllegalArgumentException("Provided argument can't be null");
        }

        mDayList = new ArrayList<>(dayList);
    }


    public Day getDay(final int position){
        return mDayList.get(position);
    }

    public int size(){
        return mDayList.size();
    }

    public List<Day> getDayList(){
        return mDayList;
    }
}

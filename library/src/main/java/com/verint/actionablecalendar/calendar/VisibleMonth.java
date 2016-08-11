package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Visible month that contains list of visible {@link Day} object for specific month
 *
 * Created by acheshihin on 8/4/2016.
 */
public class VisibleMonth {

    private final List<Day> mDayArray;

    public VisibleMonth(@NonNull List<Day> dayArray){
        mDayArray = new ArrayList<>(dayArray);
    }

    public Day getDay(final int position){
        return mDayArray.get(position);
    }

    public int size(){
        return mDayArray.size();
    }
}

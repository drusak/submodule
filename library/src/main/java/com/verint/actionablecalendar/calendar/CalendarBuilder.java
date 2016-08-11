package com.verint.actionablecalendar.calendar;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

/**
 * Builds adapter for week day header and for month view as well
 *
 * Created by acheshihin on 8/4/2016.
 */
public class CalendarBuilder {

    private final int mGridItemLayoutId;
    private CalendarCallbacks mListener;

    public CalendarBuilder(@LayoutRes final int monthGridItemLayoutId,
                           @NonNull CalendarCallbacks listener){

        mGridItemLayoutId = monthGridItemLayoutId;
        mListener = listener;
    }

    @LayoutRes
    public int getGridItemLayout(){
        return mGridItemLayoutId;
    }

    public CalendarCallbacks getCalendarCallbacks(){
        return mListener;
    }

    public MonthGridAdapter createAdapterFor(MixedVisibleMonth monthDate){
        return new MonthGridAdapter(getGridItemLayout(), monthDate, getCalendarCallbacks());
    }
}

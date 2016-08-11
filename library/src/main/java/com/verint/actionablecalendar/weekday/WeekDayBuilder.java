package com.verint.actionablecalendar.weekday;

import android.support.annotation.LayoutRes;

/**
 * Created by acheshihin on 8/10/2016.
 */
public class WeekDayBuilder {

    private final int mWeekDayItemLayoutId;

    public WeekDayBuilder(@LayoutRes final int weekDayItemLayoutId){
        mWeekDayItemLayoutId = weekDayItemLayoutId;
    }

    @LayoutRes
    public int getWeekDayItemLayoutId(){
        return mWeekDayItemLayoutId;
    }
}

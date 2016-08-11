package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;

/**
 * Created by acheshihin on 8/7/2016.
 */
public class DayState {

    public DayState(DayType type) {
        mType = type;
    }

    public DayState(){
        this(DayType.CURRENT_MONTH_DAY_NORMAL);
    }

    public enum DayType {
        CURRENT_MONTH_DAY_NORMAL,
        CURRENT_MONTH_DAY_WEEKEND,
        NON_CURRENT_MONTH_DAY
    }

    private DayType mType;

    public void setType(@NonNull DayType type){
        mType = type;
    }

    public DayType getType(){
        return mType;
    }

}

package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;

/**
 * Logic part related to representation of single day item within month grid. Day state is mentioned
 * to be used as identification of current month day and rest days (that are not belong to current month).
 * Additionally there is relation to weekend day, since it has to be represented in a bit different
 * way from normal current month day
 *
 * Created by acheshihin on 8/7/2016.
 */
public class DayState {

    public enum DayType {
        CURRENT_MONTH_DAY_NORMAL,
        CURRENT_MONTH_DAY_WEEKEND,
        NON_CURRENT_MONTH_DAY
    }

    private DayType mType;

    /**
     * TODO: Add JavaDoc
     *
     * @param type
     */
    public DayState(DayType type) {
        mType = type;
    }

    /**
     * TODO: Add JavaDoc
     */
    public DayState(){
        this(DayType.CURRENT_MONTH_DAY_NORMAL);
    }

    public void setType(@NonNull DayType type){
        mType = type;
    }

    public DayType getType(){
        return mType;
    }

}

package com.verint.actionablecalendar.calendar;

/**
 * Created by acheshihin on 8/10/2016.
 */
public class MonthItemClickAttacher {
    public void attachListener(){

    }

    public void detachListener(){

    }

    public interface OnMonthItemClickListener {
        void onMonthItemClick();
    }

    public interface OnMonthItemLongClickListener {
        boolean onMonthItemLongClick();
    }
}

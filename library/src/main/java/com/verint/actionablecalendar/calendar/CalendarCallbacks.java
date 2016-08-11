package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;

/**
 * Created by acheshihin on 8/10/2016.
 */
public interface CalendarCallbacks {
    void onCalendarItemClick(@NonNull final Day day, final int position);
    void onCalendarItemLongClick(@NonNull final Day day, final int position);
}

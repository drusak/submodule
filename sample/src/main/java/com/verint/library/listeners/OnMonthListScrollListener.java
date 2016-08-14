package com.verint.library.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.verint.actionablecalendar.calendar.models.Direction;

/**
 * Redirects list scroll event from activity to {@link com.verint.library.adapters.MonthListAdapter}
 *
 * Created by acheshihin on 8/11/2016.
 */
public interface OnMonthListScrollListener {
    void onMonthListScroll(@NonNull LinearLayoutManager linearLayoutManager,
                           @NonNull Direction scrollDirection);
}

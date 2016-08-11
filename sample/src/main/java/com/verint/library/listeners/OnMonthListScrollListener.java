package com.verint.library.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Redirects list scroll event from activity to {@list com.verint.library.adapters.MonthListAdapter}
 *
 * Created by acheshihin on 8/11/2016.
 */
public interface OnMonthListScrollListener {
    void onMonthListScroll(@NonNull LinearLayoutManager linearLayoutManager);
}

package com.verint.library.listeners;

import com.verint.actionablecalendar.calendar.models.Direction;

/**
 * Provides ability to "ask" observer to load more
 *
 * Created by acheshihin on 8/11/2016.
 */
public interface OnLoadMoreListener {

    /**
     * Informs when more data should be loaded, scrollDirection represents direction of scroll gesture
     * that was done by user (scrolled up or scrolled down).
     *
     * @param scrollDirection true|false
     */
    void onLoadMore(Direction scrollDirection);
}

package com.verint.actionablecalendar.calendar;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.verint.actionablecalendar.calendar.listener.OnListScrollDirectionalListener;
import com.verint.actionablecalendar.calendar.listener.OnLoadMoreListener;
import com.verint.actionablecalendar.calendar.models.Direction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.verint.actionablecalendar.calendar.models.Direction.DOWN;
import static com.verint.actionablecalendar.calendar.models.Direction.UP;

/**
 * Created by Dmitry Rusak on 11/15/16.
 * <p>
 */

public class CalendarRecyclerView extends RecyclerView implements OnLoadMoreListener {

    private static final int NUMBER_DAYS_IN_A_WEEK = 7;

    public static final int NUMBER_OF_MONTHS_TO_LOAD = 2;
    /**when user scrolls to start or end of calendar We'll call notify adapter only if visible days in intervals:<br>
    at top - [0, NUMBER_DAYS_LIMIT_TO_START_VIEWS_UPDATE];
    at bottom - [daysCount - NUMBER_DAYS_LIMIT_TO_START_VIEWS_UPDATE, daysCount]*/
    private static final int NUMBER_DAYS_LIMIT_TO_START_VIEWS_UPDATE =
            NUMBER_OF_MONTHS_TO_LOAD * 31/*max number of days in month*/ * 2/*multi coefficient*/ +
                    NUMBER_OF_MONTHS_TO_LOAD * 10/*additional days*/;
    private static final int NUMBER_DAYS_TO_UPDATE_OVER_VISIBLE = 14;

    private static final int NUMBER_DAYS_TO_UPDATE_BY_ONE_UPDATE_ITERATION = 5;
    private static final int ONE_UPDATE_ITERATION_MS = 100;

    CalendarRecyclerViewAdapter mAdapter;
    private OnNewMonthsAddedListener mOnNewMonthsAddedListener;
    private GridLayoutManager mLayoutManager;
    private Handler mUiHandler;
    private Handler mLoadingMoreHandler;

    public CalendarRecyclerView(Context context) {
        super(context);
        init();
    }

    public CalendarRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mUiHandler = new Handler();
        mLoadingMoreHandler = new Handler();
        mLayoutManager = new GridLayoutManager(getContext(),
                NUMBER_DAYS_IN_A_WEEK,
                LinearLayoutManager.VERTICAL,
                false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // make non days items be with width as match_parent
                return getAdapter().getItemViewType(position) == CalendarRecyclerViewAdapter.VIEW_TYPE_MONTH_DAY ?
                        1 : NUMBER_DAYS_IN_A_WEEK;
            }
        });
        mAdapter = new CalendarRecyclerViewAdapter();
        setLayoutManager(mLayoutManager);
        setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(this);

        // Specify listener for scroll events in order to differentiate scroll direction
        addOnScrollListener(new OnListScrollDirectionalListener(this) {

            @Override
            public void onScrolledUp(RecyclerView recyclerView, int dx, int dy) {
                if (mAdapter != null){
                    mAdapter.onMonthListScroll((LinearLayoutManager) getLayoutManager(), UP);
                }
            }

            @Override
            public void onScrolledDown(RecyclerView recyclerView, int dx, int dy) {
                if (mAdapter != null){
                    mAdapter.onMonthListScroll((LinearLayoutManager) getLayoutManager(), DOWN);
                }
            }
        });

    }

    private boolean notifyUpdateVisibleItems() {
        final int first = mLayoutManager.findFirstVisibleItemPosition();
        final int last = mLayoutManager.findLastVisibleItemPosition();
        final int maxEnd = mAdapter.getItemCount() - NUMBER_DAYS_LIMIT_TO_START_VIEWS_UPDATE;
        if (first < NUMBER_DAYS_LIMIT_TO_START_VIEWS_UPDATE) {
            // clear previous update messages
            mUiHandler.removeCallbacksAndMessages(null);
            postNotifyUpdateByParts(first - NUMBER_DAYS_TO_UPDATE_OVER_VISIBLE, last + NUMBER_DAYS_TO_UPDATE_OVER_VISIBLE);
            return true;
        } else if (last > maxEnd) {
            // clear previous update messages
            mUiHandler.removeCallbacksAndMessages(null);
            postNotifyUpdateByParts(first - NUMBER_DAYS_TO_UPDATE_OVER_VISIBLE, last + NUMBER_DAYS_TO_UPDATE_OVER_VISIBLE);
            return true;
        }
        return false;
    }

    /**
     * send notification to adapter by parts #NUMBER_DAYS_TO_UPDATE_BY_ONE_UPDATE_ITERATION with specific interval of time
     */
    private void postNotifyUpdateByParts(final int currentPosition, final int maxPosition) {
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getScrollState() == SCROLL_STATE_IDLE && currentPosition <= maxPosition) {
                    mAdapter.notifyItemRangeChanged(currentPosition, NUMBER_DAYS_TO_UPDATE_BY_ONE_UPDATE_ITERATION);
                    postNotifyUpdateByParts(currentPosition + NUMBER_DAYS_TO_UPDATE_BY_ONE_UPDATE_ITERATION, maxPosition);
                } else {
                    mUiHandler.removeCallbacksAndMessages(null);
                }
            }
        }, ONE_UPDATE_ITERATION_MS);
    }


    public void initFirstLoading() {
        Date currentMonth = new Date(System.currentTimeMillis());
        List<MixedVisibleMonth> monthList = initMonthListForDate(currentMonth);
        setData(monthList);
        scrollToCurrentMonth();
        // fake call of smooth scroll, so it will invalidate calendar
        smoothScrollToCurrentMonth();
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (getScrollState() == SCROLL_STATE_IDLE) {
                    notifyUpdateVisibleItems();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    public void scrollToCurrentMonth() {
        stopScroll();
        // according to {@link http://stackoverflow.com/questions/30845742/smoothscrolltoposition-doesnt-work-properly-with-recyclerview}
        mLayoutManager.scrollToPositionWithOffset(mAdapter.getCurrentMonthHeaderPosition(), 0);
    }

    public void smoothScrollToCurrentMonth() {
        smoothScrollToPosition(mAdapter.getCurrentMonthHeaderPosition());
    }

    public void setData(List<MixedVisibleMonth> months) {
        mAdapter.setMonths(months);
        if (mOnNewMonthsAddedListener != null) {
            mOnNewMonthsAddedListener.onNewMonthsAdded(months);
        }

    }

    public void setCalendarItemClickListener(@NonNull CalendarCallbacks calendarItemClickListener) {
        mAdapter.setItemClickListener(calendarItemClickListener);
    }

    public void setOnNewMonthsAddedListener(OnNewMonthsAddedListener onNewMonthsAddedListener) {
        mOnNewMonthsAddedListener = onNewMonthsAddedListener;
    }

    public void updateMonths(@NonNull final List<MixedVisibleMonth> monthList, boolean shift, boolean myRequests) {
        mAdapter.updateMonthsIndicators(monthList, shift, myRequests);
        mLoadingMoreHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyUpdateVisibleItems();
            }
        });

    }

    @Override
    public void onLoadMore(final Direction scrollDirection) {

        if (mAdapter.getMonths().size() == 0) {
            return;
        }
        // Add null accordingly to scroll direction in order to enable loading progress bar
        final List<MixedVisibleMonth> months;
        switch (scrollDirection) {

            case DOWN:  // Load future dates

                // Load more
                months = new ArrayList<>(NUMBER_OF_MONTHS_TO_LOAD);
                int listCount = mAdapter.getMonths().size();
                for (int i = listCount, newListCount = listCount + NUMBER_OF_MONTHS_TO_LOAD; i < newListCount; i++) {
                    // Get current last item
                    MixedVisibleMonth lastMonth = mAdapter.getMonths().get(mAdapter.getMonths().size() - 1);

                    final MixedVisibleMonth nextMonth = CalendarDataFactory.newInstance()
                            .create(CalendarUtils.getNextMonth(lastMonth));

                    mAdapter.addItemAtTheEnd(nextMonth);
                    months.add(nextMonth);
                }

                break;

            case UP: // Load past dates

                // Load more
                months = new ArrayList<>(NUMBER_OF_MONTHS_TO_LOAD);
                for (int i = 0; i < NUMBER_OF_MONTHS_TO_LOAD; i++) {
                    // Get current first item
                    final MixedVisibleMonth firstMonth = mAdapter.getMonths().get(0);
                    final MixedVisibleMonth previousMonth =
                            CalendarDataFactory.newInstance().create(CalendarUtils.getPreviousMonth(
                                    firstMonth.getCurrentMonth().getDay(0).getDate()));

                    mAdapter.addItemAtBeginning(previousMonth);
                    months.add(previousMonth);
                }

                break;

            default:
                months = null;
                throw new IllegalStateException("Unknown case found");
        }
        // Inform regarding data set change and finish of loading process with delay
        mLoadingMoreHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (months != null && mOnNewMonthsAddedListener != null) {
                    mOnNewMonthsAddedListener.onNewMonthsAdded(months);
                }
            }
        }, 500);

        mAdapter.setLoaded();
    }

    private List<MixedVisibleMonth> initMonthListForDate(@NonNull Date date){

        List<MixedVisibleMonth> monthList = new ArrayList<>();

        List<Date> monthDateList = CalendarUtils.generateInitialMonthList(date);
        for (Date each : monthDateList){
            monthList.add(CalendarDataFactory.newInstance().create(each));
        }
        return monthList;
    }

    public interface OnNewMonthsAddedListener {
        void onNewMonthsAdded(List<MixedVisibleMonth> newMonths);
    }

}

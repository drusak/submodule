package com.verint.actionablecalendar.calendar;

import android.content.Context;
import android.graphics.Point;
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
import static com.verint.actionablecalendar.calendar.models.Direction.NONE;
import static com.verint.actionablecalendar.calendar.models.Direction.UP;

/**
 * Created by Dmitry Rusak on 11/15/16.
 * <p>
 */

public class CalendarRecyclerView extends RecyclerView implements OnLoadMoreListener {

    CalendarRecyclerViewAdapter mAdapter;
    private OnNewMonthsAddedListener mOnNewMonthsAddedListener;
    private GridLayoutManager mLayoutManager;
    private Handler mUiHandler;
    private Handler mLoadingHandler;

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
        mLoadingHandler = new Handler();
        mLayoutManager = new GridLayoutManager(getContext(), 7,
                LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getAdapter().getItemViewType(position) == CalendarRecyclerViewAdapter.VIEW_TYPE_MONTH_DAY ? 1 : 7;
            }
        });
        mAdapter = new CalendarRecyclerViewAdapter(new ArrayList<MixedVisibleMonth>());
        setLayoutManager(mLayoutManager);
        setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(this);
        mAdapter.setVisualCommunicatorCallback(new CalendarRecyclerViewAdapter.VisualCommunicatorCallback() {
            @Override
            public int getFirstVisibleItemPosition() {
                return mLayoutManager.findFirstVisibleItemPosition();
            }

            @Override
            public int getLastVisibleItemPosition() {
                return mLayoutManager.findLastVisibleItemPosition();
            }
        });

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

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    if (!mAdapter.isLoadingInProgress()) {
                        updateVisibleItems();
                    }
                } else {
                    mUiHandler.removeCallbacksAndMessages(null);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

//        // Scroll to current month if such month exists within currently set data list
//        final int currentMonthPosition = mAdapter.getCurrentMonthPosition();
//        if (RecyclerView.NO_POSITION != currentMonthPosition){
//            mRecyclerView.scrollToPosition(currentMonthPosition);
//        }
    }

    private void updateVisibleItems() {
        final int first = mLayoutManager.findFirstVisibleItemPosition();
        final int last = mLayoutManager.findLastVisibleItemPosition();
        Log.i("!!!", "scroll notify changed " + first + "/" + (last - first));
        Point adjustedChangedIndexes = mAdapter.containsChangedIndexes(first - 10, last + 10);
        if (adjustedChangedIndexes != null) {
            postUpdate(Math.max(0, adjustedChangedIndexes.x),
                    Math.min(adjustedChangedIndexes.y, mAdapter.getItemCount()));
        }
    }

    private void postUpdate(final int currentPosition, final int maxPosition) {
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getScrollState() == SCROLL_STATE_IDLE && currentPosition <= maxPosition) {
                    mAdapter.notifyItemRangeChanged(currentPosition, 5);
                    postUpdate(currentPosition + 5, maxPosition);
                } else {
                    mUiHandler.removeCallbacksAndMessages(null);
                }
            }
        }, 100);
    }


    public void initFirstLoading() {
        Date currentMonth = new Date(System.currentTimeMillis());
        List<MixedVisibleMonth> monthList = initMonthListForDate(currentMonth);
        setData(monthList);
        scrollToPosition(mAdapter.getItemCount() / 2);
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

    public void updateMonths(@NonNull final List<MixedVisibleMonth> monthList, boolean shift, boolean timeOff, boolean auction) {
        mAdapter.updateItems(monthList, shift, timeOff, auction);
    }

    @Override
    public void onLoadMore(final Direction scrollDirection) {

        mLoadingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
        // Add null accordingly to scroll direction in order to enable loading progress bar
        final List<MixedVisibleMonth> months;
        switch (scrollDirection){

            case DOWN:  // Load future dates

                // Remove loading item
//                        activity.mAdapter.removeLastItem();

                // Load more
                months = new ArrayList<>(3);
                int listCount = mAdapter.getMonths().size();
                int newListCount = listCount + 3;
                for (int i=listCount; i < newListCount; i++){
                    // Get current last item
                    MixedVisibleMonth lastItemDate;

                    if (mAdapter.getMonths().size() == 0){
                        // We take not item size-1 since it is null
                        lastItemDate = mAdapter.getMonths().get(mAdapter.getMonths().size()-2);
                    } else {
                        lastItemDate = mAdapter.getMonths().get(mAdapter.getMonths().size()-1);
                    }

                    final MixedVisibleMonth nextMonth = CalendarDataFactory.newInstance()
                            .create(CalendarUtils.getNextMonth(lastItemDate));

                    mAdapter.addItemAtTheEnd(nextMonth);
                    months.add(nextMonth);
                }

                break;

            case UP: // Load past dates

                // Remove loading item
//                        activity.mAdapter.removeFirstItem();
                // Load more
                months = new ArrayList<>(3);
                for (int i=0; i < 3; i++){
                    // Get current first item
                    final MixedVisibleMonth firstItemDate = mAdapter.getMonths().get(0);
                    final MixedVisibleMonth previousMonth =
                            CalendarDataFactory.newInstance().create(CalendarUtils.getPreviousMonth(
                                    firstItemDate.getCurrentMonth().getDay(0).getDate()));
                    // activity.mAdapter.addItemAtBeginning(CalendarUtils.getPreviousMonth(firstItemDate));
                    mAdapter.addItemAtBeginning(previousMonth);

                    months.add(previousMonth);
                }

                break;

            case NONE:
                months = null;
                // TODO: Consider adding here additional handling of such case
                break;

            default:
                months = null;
                throw new IllegalStateException("Unknown case found");
        }
        // Inform regarding data set change and finish of loading process
                mLoadingHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateVisibleItems();
                        if (months != null && mOnNewMonthsAddedListener != null) {
                            mOnNewMonthsAddedListener.onNewMonthsAdded(months);
                        }
                    }
                }, 500);

                mAdapter.setLoaded();

            }
        }, 700);
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

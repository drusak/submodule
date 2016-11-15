package com.verint.actionablecalendar.calendar;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

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
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 7,
                LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getAdapter().getItemViewType(position) == CalendarRecyclerViewAdapter.VIEW_TYPE_MONTH_HEADER ? 7 : 1;
            }
        });
        mAdapter = new CalendarRecyclerViewAdapter(new ArrayList<MixedVisibleMonth>());
        setLayoutManager(mLayoutManager);
        setHasFixedSize(true);
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

//        // Scroll to current month if such month exists within currently set data list
//        final int currentMonthPosition = mAdapter.getCurrentMonthPosition();
//        if (RecyclerView.NO_POSITION != currentMonthPosition){
//            mRecyclerView.scrollToPosition(currentMonthPosition);
//        }
        scrollToPosition(mAdapter.getItemCount() / 2);
    }

    public void initFirstLoading() {
        Date currentMonth = new Date(System.currentTimeMillis());
        List<MixedVisibleMonth> monthList = initMonthListForDate(currentMonth);
        setData(monthList);
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

        // Add null accordingly to scroll direction in order to enable loading progress bar

        switch (scrollDirection){

            case DOWN:  // Load future dates

                // Remove loading item
//                        activity.mAdapter.removeLastItem();

                // Load more
                int listCount = mAdapter.getMonths().size();
                int newListCount = listCount + 3;
                List<MixedVisibleMonth> newMonths = new ArrayList<>(3);
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
                    newMonths.add(lastItemDate);
                }
                if (mOnNewMonthsAddedListener != null) {
                    mOnNewMonthsAddedListener.onNewMonthsAdded(newMonths);
                }
                break;

            case UP: // Load past dates

                // Remove loading item
//                        activity.mAdapter.removeFirstItem();
                newMonths = new ArrayList<>(3);
                // Load more
                for (int i=0; i < 3; i++){
                    // Get current first item
                    final MixedVisibleMonth firstItemDate = mAdapter.getMonths().get(0);
                    // activity.mAdapter.addItemAtBeginning(CalendarUtils.getPreviousMonth(firstItemDate));
                    mAdapter.addItemAtBeginning(CalendarDataFactory.newInstance().create(CalendarUtils.getPreviousMonth(firstItemDate.getCurrentMonth().getDay(0).getDate())));
                    newMonths.add(firstItemDate);
                }

                if (mOnNewMonthsAddedListener != null) {
                    mOnNewMonthsAddedListener.onNewMonthsAdded(newMonths);
                }
                break;

            case NONE:
                // TODO: Consider adding here additional handling of such case
                break;

            default:
                throw new IllegalStateException("Unknown case found");
        }

        // Inform regarding data set change and finish of loading process
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

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
import com.verint.actionablecalendar.calendar.models.CalendarSnapshotData;

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

    private int mScrolledForwardMonthCount;
    private int mScrolledBackwardMonthCount;
    private Date mScrolledForwardDate;
    private Date mScrolledBackwardDate;

    private Date mInitialDate;

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

        // TODO: Consider delete
        mScrolledForwardDate = new Date();
        mScrolledBackwardDate = new Date();
        mInitialDate = new Date();

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
        List<MixedVisibleMonth> monthList = initMonthListForDate(mInitialDate);
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

    public Date getFirstDayOfFullyVisibleMonth() {
        final int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPosition();
        return mAdapter.getNextMonthFromPosition(firstVisiblePosition);
    }

    private void updateScrolledBoundariesStatistics(@Nullable final Direction scrollDirection,
                                                    @Nullable MixedVisibleMonth latestLoadedMonth){

        // TODO: Consider remove guard check
        if (latestLoadedMonth == null || scrollDirection == null){ // NPE guard check
            return;
        }

        final List<Day> dayList = latestLoadedMonth.getDayList();
        final Date latestLoadedMonthDate;

        switch (scrollDirection){
            case DOWN: // Future
                latestLoadedMonthDate = dayList.get(dayList.size()-1).getDate();
                if (latestLoadedMonthDate.after(mScrolledForwardDate)){
                    mScrolledForwardMonthCount = CalendarUtils.monthsBetween(mInitialDate, latestLoadedMonthDate);
                    mScrolledForwardDate.setTime(latestLoadedMonthDate.getTime());
                }
                break;

            case UP: // Past
                latestLoadedMonthDate = dayList.get(0).getDate();
                if (latestLoadedMonthDate.before(mScrolledBackwardDate)){
                    mScrolledBackwardMonthCount = CalendarUtils.monthsBetween(latestLoadedMonthDate, mInitialDate);
                    mScrolledBackwardDate.setTime(latestLoadedMonthDate.getTime());
                }
                break;

            default:
                throw new IllegalStateException();
        }
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

                    updateScrolledBoundariesStatistics(DOWN, nextMonth);
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

                    updateScrolledBoundariesStatistics(UP, previousMonth);
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

    /////////////////////////////////////////////
    /////// User Behavior Analytics Data ////////
    /////////////////////////////////////////////

    /**
     * Returns amount of total scrolled forward (future) by user months from initial date
     *
     * @return {@link int} scrolled months count from initial date
     */
    public int getScrolledForwardMonthCount(){
        return mScrolledForwardMonthCount;
    }

    /**
     * Returns amount of total scrolled backward (past) by user months from inital date
     *
     * @return {@link int} scrolled months count from initial date
     */
    public int getScrolledBackwardMonthCount(){
        return mScrolledBackwardMonthCount;
    }

    /**
     * counts indicators and icons completely visible to user
     *
     * @return {@link CalendarSnapshotData}
     */
    public CalendarSnapshotData getVisibleSnapshotData() {

        final CalendarSnapshotData snapshotData = new CalendarSnapshotData();

        if (mLayoutManager != null && mAdapter != null) {

            final int firstVisiblePosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            final int lastVisiblePosition = mLayoutManager.findLastCompletelyVisibleItemPosition();

            fillSnapshotDataByRange(snapshotData, firstVisiblePosition, lastVisiblePosition);
        }

        return snapshotData;
    }

    /**
     * Counts indicators and icons that potentially were visible to user during interaction
     * with application
     *
     * @return {@link CalendarSnapshotData}
     */
    public CalendarSnapshotData getLoadedSnapshotData() {

        final CalendarSnapshotData snapshotData = new CalendarSnapshotData();

        if (mLayoutManager != null && mAdapter != null) {

            final int firstItemIndex = 0;
            final int lastItemIndex = mAdapter.getItemCount() - 1;

            fillSnapshotDataByRange(snapshotData, firstItemIndex, lastItemIndex);
        }

        return snapshotData;
    }

    /**
     * Fills {@link CalendarSnapshotData} with snapshot data which is represented within
     * {@link CalendarRecyclerView} (list of all months calendars)
     *
     * @param snapshotData {@link CalendarSnapshotData}
     * @param beginDayPosition index of first {@link Day} item within {@code mAdapter}
     * @param endDayPosition index of last {@link Day} item within {@code mAdapter}
     */
    private void fillSnapshotDataByRange(@NonNull CalendarSnapshotData snapshotData,
                                                         final int beginDayPosition,
                                                         final int endDayPosition){

        if (mLayoutManager != null && mAdapter != null
                && beginDayPosition < endDayPosition
                && endDayPosition < mAdapter.getItemCount()) {

            int numCellsWithIndicators = 0;
            int numCellsWithOneIcon = 0;
            int numCellsWithTwoIcons = 0;
            int numCellsWithPotentiallyExtraIcon = 0;

            for (int i = beginDayPosition; i <= endDayPosition; i++) {

                Day day = mAdapter.getDayByPosition(i);

                if (DayState.DayType.NON_CURRENT_MONTH_DAY != day.getDayState().getType()
                        && DayState.DayType.MONTH_HEADER != day.getDayState().getType()) {

                    // count only normal days
                    if (day.isShiftEnabled()) {
                        numCellsWithIndicators++;
                    }

                    boolean auctionWithoutBidsPresent = day.getAuctionNoBidItem() != null;
                    boolean auctionWithBidsPresent = day.getAuctionWithBidItem() != null;
                    boolean timeOffRequestPresent = day.getTimeOffItem() != null;

                    boolean requestIconVisible = timeOffRequestPresent || auctionWithBidsPresent;
                    boolean auctionIconVisible = auctionWithoutBidsPresent;


                    if (requestIconVisible && auctionIconVisible) {

                        numCellsWithTwoIcons++;

                    } else if (requestIconVisible || auctionIconVisible) {
                        numCellsWithOneIcon++;
                    }

                    // Check if theoretically we have to show third icon
                    if (auctionWithBidsPresent && auctionWithoutBidsPresent && timeOffRequestPresent){
                        numCellsWithPotentiallyExtraIcon++;
                    }
                }
            }

            snapshotData.setIndicatorCount(numCellsWithIndicators);
            snapshotData.setTwoIconCellCount(numCellsWithTwoIcons);
            snapshotData.setSingleIconCellCount(numCellsWithOneIcon);
            snapshotData.setPotentiallyExtraIconCellCount(numCellsWithPotentiallyExtraIcon);
        }
    }


    /////////////////////////////////////////
    /////////// Listener region /////////////
    /////////////////////////////////////////

    public interface OnNewMonthsAddedListener {
        void onNewMonthsAdded(List<MixedVisibleMonth> newMonths);
    }

}

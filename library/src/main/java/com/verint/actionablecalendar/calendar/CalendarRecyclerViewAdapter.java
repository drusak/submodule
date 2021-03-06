package com.verint.actionablecalendar.calendar;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.verint.actionablecalendar.calendar.listener.OnLoadMoreListener;
import com.verint.actionablecalendar.calendar.listener.OnMonthListScrollListener;
import com.verint.actionablecalendar.calendar.models.Direction;
import com.verint.mylibrary.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by Dmitry Rusak on 11/15/16.
 * <p>
 */

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnMonthListScrollListener {

    public static final int VIEW_TYPE_MONTH_HEADER = 0;
    public static final int VIEW_TYPE_MONTH_DAY = 1;

    private static final int MIN_DAYS_INTERVAL_COUNT_TO_START_LOADING_MORE =
            CalendarRecyclerView.NUMBER_OF_MONTHS_TO_LOAD * 31 / 2 + CalendarRecyclerView.NUMBER_OF_MONTHS_TO_LOAD * 10/*additional days*/ ;

    private final List<MixedVisibleMonth> mMonths = new ArrayList<>();
    private final List<Day> mDays = new ArrayList<>();

    private OnLoadMoreListener mOnLoadMoreListener;
    private CalendarCallbacks mItemClickListener;

    // contains current month start position, in order not to count it all the time
    private int mCurrentMonthHeaderPosition;


    private boolean mLoadingInProgress;

    public CalendarRecyclerViewAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        Day day = mDays.get(position);
        return day.getDayState().getType() == DayState.DayType.MONTH_HEADER ?
                VIEW_TYPE_MONTH_HEADER : VIEW_TYPE_MONTH_DAY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MONTH_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.month_header_item, parent, false));
        } else if (viewType == VIEW_TYPE_MONTH_DAY) {
            return new MonthDayViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.month_grid_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Day day = mDays.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(day);
        } else if (holder instanceof MonthDayViewHolder) {
            ((MonthDayViewHolder) holder).bind(day, mItemClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public int getCurrentMonthHeaderPosition() {
        return mCurrentMonthHeaderPosition;
    }

    public Date getDateByPosition(int position) {
        if (position < getItemCount()) {
            return mDays.get(position).getDate();
        }
        // to avoid null pointer
        return new Date();
    }

    public Day getDayByPosition(int position) {
        if (position < getItemCount()) {
            return mDays.get(position);
        }
        // to avoid null pointer
        return new Day(new Date(), new DayState());
    }

    /**
     * @param position position (day) in calendar to start search next month from
     * @return date of first day of founded next month
     */
    public Date getNextMonthFromPosition(int position) {
        if (position >= getItemCount()) {
            return new Date();
        }
        Date monthDate = new Date();
        for ( ; position < getItemCount(); position++) {
            if (getItemViewType(position) == VIEW_TYPE_MONTH_HEADER) {
                monthDate = mDays.get(position).getDate();
                break;
            }
        }
        return monthDate;
    }

    @Override
    public void onMonthListScroll(@NonNull LinearLayoutManager linearLayoutManager, @NonNull Direction scrollDirection) {
        switch (scrollDirection){

            case UP:
                final int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (!mLoadingInProgress &&
                        (firstVisibleItemPosition - MIN_DAYS_INTERVAL_COUNT_TO_START_LOADING_MORE) <= 0){
                    mLoadingInProgress = true;
                    if (mOnLoadMoreListener != null){
                        mOnLoadMoreListener.onLoadMore(scrollDirection);
                    }
                }
                break;

            case DOWN:
                final int totalItemCount = linearLayoutManager.getItemCount();
                final int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (!mLoadingInProgress &&
                        totalItemCount <= (lastVisibleItemPosition + MIN_DAYS_INTERVAL_COUNT_TO_START_LOADING_MORE)){
                    mLoadingInProgress = true;
                    if (mOnLoadMoreListener != null){
                        mOnLoadMoreListener.onLoadMore(scrollDirection);
                    }
                }
                break;

            default:
                throw new IllegalStateException("Unknown direction case found");
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setItemClickListener(CalendarCallbacks itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setMonths(List<MixedVisibleMonth> months) {
        clear();
        addMonths(months, false);
        notifyItemRangeInserted(0, mDays.size());
    }

    /**
     * Adds {@link MixedVisibleMonth} item to data list at position 0 and notifies adapter that item was
     * inserted
     *
     * @param month {@link MixedVisibleMonth}|null
     */
    public void addItemAtBeginning(final MixedVisibleMonth month){
        notifyItemRangeInserted(0, addMonth(month, true));
    }

    /**
     * Adds {@link MixedVisibleMonth} item to data list at the position of the end of the list and notifies that
     * item was inserted at the end of list
     *
     * @param month {@link MixedVisibleMonth}|null
     */
    public void addItemAtTheEnd(final MixedVisibleMonth month){
        int startIndex = mDays.size();
        notifyItemRangeInserted(startIndex, addMonth(month, false));
    }

    public boolean updateMonthsIndicators(@NonNull final List<MixedVisibleMonth> monthList, boolean shift, boolean myRequests) {
        boolean updated = false;
        for (MixedVisibleMonth each : monthList) {
            boolean oneItemUpdated = updateItemIndicators(each, shift, myRequests);
            if (!updated) {
                updated = oneItemUpdated;
            }
        }

        return updated;
    }

    private void clear() {
        int size = mDays.size();
        mDays.clear();
        mMonths.clear();
        mCurrentMonthHeaderPosition = 0;
        notifyItemRangeRemoved(0, size);
    }

    /**
     * add new month into adapter
     * @param atBeginning true if need to insert items at beginning, false - at end
     * @return number of added days
     */
    private int addMonth(MixedVisibleMonth month, boolean atBeginning) {
        return addMonths(Collections.singletonList(month), atBeginning);
    }

    /**
     * @see #addMonth(MixedVisibleMonth, boolean)
     */
    private int addMonths(List<MixedVisibleMonth> months, boolean atBeginning) {
        int countAddedDays = 0;
        if (atBeginning) {
            mMonths.addAll(0, months);
        } else {
            mMonths.addAll(months);
        }
        for (MixedVisibleMonth month : months) {
            boolean isCurrentMonth = false;
            // check if month is current, and update current month position
            if (month.getCurrentMonth().size() > 0) {
                Day firstDayOfMonth = month.getCurrentMonth().getDay(0);
                if (CalendarUtils.isSameMonthAsCurrent(firstDayOfMonth)) {
                    mCurrentMonthHeaderPosition = mDays.size();
                    isCurrentMonth = true;
                }
            }
            List<Day> monthDays = month.getDayListWithHeaders();
            if (atBeginning) {
                mDays.addAll(0, monthDays);
                if (!isCurrentMonth) {
                    // We're adding days under today
                    mCurrentMonthHeaderPosition += monthDays.size();
                }
            } else {
                mDays.addAll(monthDays);
            }
            countAddedDays += monthDays.size();
        }
        return countAddedDays;
    }

    private void removeAllItemsEquals(Day day) {
        int index = -1;
        do {
            index = mDays.indexOf(day);
            if (index >= 0) {
                mDays.remove(index);
                notifyItemRemoved(index);
            }
        } while (index >= 0);
    }

    /**
     * for synchronization and changing days from different threads -
     * We're not setting new days, but updating existing according to indicators to update
     * @return true if items were updated
     */
    private boolean updateItemIndicators(final MixedVisibleMonth month, boolean shift, boolean myRequests) {
        if (month != null && month.getDayList().size() > 0) {
            Day firstDay = month.getDay(0);
            final int index = mDays.indexOf(firstDay);
            if (index >= 0) {
                List<Day> newDays = month.getDayList();
                final int newDaysSize = newDays.size();
                // one by one update in case same date
                for (int i = 0; i < newDaysSize; i++) {
                    Day day = mDays.get(i + index);
                    Day newDay = newDays.get(i);
                    if (shift) {
                        day.setShiftEnabled(newDay.isShiftEnabled());
                    }
                    if (myRequests) {
                        day.setTimeOffItem(newDay.getTimeOffItem());
                        day.setAuctionNoBidItem(newDay.getAuctionNoBidItem());
                        day.setAuctionWithBidItem(newDay.getAuctionWithBidItem());

                        day.setMySwapPost(newDay.getMySwapPost());
                        day.setSwapRequest(newDay.getSwapRequest());
                    }
                }
                return true;
            }
        }
        return false;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView;
        }

        public void bind(Day day) {
            Calendar calendar = CalendarUtils.getCalendarFrom(day.getDate());
            final String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                    Locale.getDefault());
            final int year = calendar.get(Calendar.YEAR);
            mTvTitle.setText(String.format(Locale.getDefault(), "%s %d", monthName, year));
        }

    }

    public boolean isLoadingInProgress() {
        return mLoadingInProgress;
    }

    public void setLoaded(){
        mLoadingInProgress = false;
    }

    public List<MixedVisibleMonth> getMonths() {
        return mMonths;
    }

    // --------------------------------------------------------------------------------------------

    private class MonthDayViewHolder extends RecyclerView.ViewHolder {

        protected View mRootView;
        protected View mContainerForIndicators;
        protected TextView mMonthDay;
        protected View mShiftIndicator;
        protected EventIndicatorView mDayIconFirstLevelView;
        protected EventIndicatorView mDayIconSecondLevelView;

        public MonthDayViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mContainerForIndicators = itemView.findViewById(R.id.containerMonthGridItem);
            mMonthDay = (TextView) itemView.findViewById(R.id.tvMonthGridItemMonthDay);
            mShiftIndicator = itemView.findViewById(R.id.vMonthGridItemShiftIndicator);
            mDayIconFirstLevelView = (EventIndicatorView) itemView.findViewById(R.id.abvCalendarDayIconFirstLevelView);
            mDayIconSecondLevelView = (EventIndicatorView) itemView.findViewById(R.id.abvCalendarDayIconSecondLevelView);
        }

        public void bind(@NonNull final Day day, final CalendarCallbacks listener){

            switch (day.getDayState().getType()){

                case CURRENT_MONTH_DAY_NORMAL: // Current month day

                    mMonthDay.setVisibility(View.VISIBLE);
                    mShiftIndicator.setVisibility(day.isShiftEnabled() ? View.VISIBLE : View.INVISIBLE);

                    setVisibilityForLevelIcon(day);

                    mMonthDay.setText(String.valueOf(day.getMonthDay()));

                    if (!CalendarUtils.isToday(day)){ // Not today
                        mContainerForIndicators.setBackgroundColor(Color.WHITE);
                    } else { // Today
                        mContainerForIndicators.setBackgroundResource(R.drawable.calendar_item_current_day_background);
                    }

                    // Specify click listeners
                    mRootView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (listener != null){
                                listener.onCalendarItemClick(day, -1);
                            }
                        }
                    });

                    mRootView.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {

                            if (listener != null){
                                listener.onCalendarItemLongClick(day, -1);
                            }
                            return true;
                        }
                    });

                    break;

                case CURRENT_MONTH_DAY_WEEKEND: // Current month week end day

                    mMonthDay.setVisibility(View.VISIBLE);
                    mShiftIndicator.setVisibility(day.isShiftEnabled() ? View.VISIBLE : View.INVISIBLE);

                    setVisibilityForLevelIcon(day);

                    // Change day value text color
                    mMonthDay.setText(String.valueOf(day.getMonthDay()));

                    if (!CalendarUtils.isToday(day)){ // Not today
                        mContainerForIndicators.setBackgroundColor(Color.parseColor("#ebebeb"));
                    } else { // Today
                        mContainerForIndicators.setBackgroundResource(R.drawable.calendar_item_current_day_background);
                    }

                    // Specify click listeners
                    mRootView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (listener != null){
                                listener.onCalendarItemClick(day, -1);
                            }
                        }
                    });

                    mRootView.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {

                            if (listener != null){
                                listener.onCalendarItemLongClick(day, -1);
                            }
                            return true;
                        }
                    });
                    break;

                case NON_CURRENT_MONTH_DAY: // Previous or next month

                    mMonthDay.setVisibility(View.INVISIBLE);
                    mShiftIndicator.setVisibility(View.INVISIBLE);
                    mDayIconFirstLevelView.setVisibility(View.GONE);
                    mDayIconSecondLevelView.setVisibility(View.GONE);

                    mContainerForIndicators.setBackgroundColor(Color.WHITE);

                    // Remove click listeners
                    mRootView.setOnClickListener(null);
                    mRootView.setOnLongClickListener(null);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown "
                            + DayState.DayType.class.getSimpleName() + " case found");
            }
        }

        /**
         * Assigns icon and badge on first from two possible icons
         *
         * @param day The day to assigns icons on it
         * @param iconTotalCount Total amount of potential icons that can be assigned
         */
        private void setVisibilityForFirstIcon(@NonNull final Day day, final int iconTotalCount){

            int firstIconPriorityLevel = -1;

            if (iconTotalCount > 0){

                if (day.getTimeOffItem() != null){

                    mDayIconFirstLevelView.setImage(day.getTimeOffItem().getIconImage());
                    mDayIconFirstLevelView.setBadge(day.getTimeOffItem().getBadgeImage());
                    firstIconPriorityLevel = 0;

                } else if (day.getSwapRequest() != null){

                    mDayIconFirstLevelView.setImage(day.getSwapRequest().getIconImage());
                    mDayIconFirstLevelView.setBadge(day.getSwapRequest().getBadgeImage());
                    firstIconPriorityLevel = 1;

                } else if (day.getMySwapPost() != null){

                    if (day.getRestUsersSwapPost() != null){

                        mDayIconFirstLevelView.setImage(day.getGeneralSwapPost().getIconImage());
                        mDayIconFirstLevelView.setBadge(day.getGeneralSwapPost().getBadgeImage());

                    } else {

                        mDayIconFirstLevelView.setImage(day.getMySwapPost().getIconImage());
                        mDayIconFirstLevelView.setBadge(day.getMySwapPost().getBadgeImage());
                    }
                    firstIconPriorityLevel = 2;

                } else if (day.getAuctionWithBidItem() != null){

                    mDayIconFirstLevelView.setImage(day.getAuctionWithBidItem().getIconImage());
                    mDayIconFirstLevelView.setBadge(day.getAuctionWithBidItem().getBadgeImage());
                    firstIconPriorityLevel = 3;

                } else if (day.getAuctionNoBidItem() != null){

                    mDayIconFirstLevelView.setImage(day.getAuctionNoBidItem().getIconImage());
                    mDayIconFirstLevelView.setBadge(day.getAuctionNoBidItem().getBadgeImage());
                    firstIconPriorityLevel = 4;

                } else if (day.getRestUsersSwapPost() != null){

                    if (day.getMySwapPost() == null){

                        mDayIconFirstLevelView.setImage(day.getRestUsersSwapPost().getIconImage());
                        mDayIconFirstLevelView.setBadge(day.getRestUsersSwapPost().getBadgeImage());
                        firstIconPriorityLevel = 5;
                    }
                }
            }

            // Set visibility for first icon according to matching state
            mDayIconFirstLevelView.setVisibility(iconTotalCount > 0 ? View.VISIBLE : View.INVISIBLE);

            // Set visibility for second icon
            setVisibilityForSecondIcon(day, iconTotalCount, firstIconPriorityLevel);
        }

        /**
         * Assigns icon and badge on second from two possible icons
         *
         * @param day The day to assigns icons on it
         * @param iconTotalCount Total amount of potential icons that can be assigned
         * @param firstIconPriorityLevel The priority of first assigned icon
         */
        private void setVisibilityForSecondIcon(@NonNull final Day day,
                                                final int iconTotalCount,
                                                final int firstIconPriorityLevel){

            boolean iconMatched = false;

            if (iconTotalCount > 1){ // Check only if we have two icons and more (first was assigned in previous method)

                if (iconTotalCount < 3){ // If we have less than potential 3 icons

                    // Priority should inform us which type of icon was set on the first level
                    // icon, next one should be one priority level higher, hence we can exclude from
                    // matching search several cases, as result result will be matched faster
                    switch (firstIconPriorityLevel){

                        case -1:
                            break;

                        case 0:

                            if (day.getSwapRequest() != null) {
                                mDayIconSecondLevelView.setImage(day.getSwapRequest().getIconImage());
                                mDayIconSecondLevelView.setBadge(day.getSwapRequest().getBadgeImage());
                                iconMatched = true;
                                break;
                            }

                        case 1:


                            if (day.getMySwapPost() != null){

                                if (day.getRestUsersSwapPost() != null){
                                    mDayIconSecondLevelView.setImage(day.getGeneralSwapPost().getIconImage());
                                    mDayIconSecondLevelView.setBadge(day.getGeneralSwapPost().getBadgeImage());
                                } else {
                                    mDayIconSecondLevelView.setImage(day.getMySwapPost().getIconImage());
                                    mDayIconSecondLevelView.setBadge(day.getMySwapPost().getBadgeImage());
                                }
                                iconMatched = true;
                                break;
                            }

                        case 2:

                            if (day.getAuctionWithBidItem() != null) {
                                mDayIconSecondLevelView.setImage(day.getAuctionWithBidItem().getIconImage());
                                mDayIconSecondLevelView.setBadge(day.getAuctionWithBidItem().getBadgeImage());
                                iconMatched = true;
                                break;
                            }

                        case 3:

                            if (day.getAuctionNoBidItem() != null && day.getAuctionWithBidItem() == null) {
                                mDayIconSecondLevelView.setImage(day.getAuctionNoBidItem().getIconImage());
                                mDayIconSecondLevelView.setBadge(day.getAuctionNoBidItem().getBadgeImage());
                                iconMatched = true;
                                break;
                            }

                        case 4:

                            if (day.getRestUsersSwapPost() != null) {

                                if (day.getMySwapPost() != null){

                                    mDayIconFirstLevelView.setImage(day.getGeneralSwapPost().getIconImage());
                                    mDayIconFirstLevelView.setBadge(day.getGeneralSwapPost().getBadgeImage());

                                } else {

                                    mDayIconSecondLevelView.setImage(day.getRestUsersSwapPost().getIconImage());
                                    mDayIconSecondLevelView.setBadge(day.getRestUsersSwapPost().getBadgeImage());
                                }

                                iconMatched = true;
                                break;
                            }

                        case 5:
                        default:
                            // Do nothing
                            break;
                    }

                } else {  // If we have 3 icons or more

                    mDayIconSecondLevelView.setImage(day.getMore().getIconImage());
                    mDayIconSecondLevelView.setBadge(day.getMore().getBadgeImage());
                    iconMatched = true;
                }
            }

            // Change icon visibility accordingly to matching value
            mDayIconSecondLevelView.setVisibility(iconMatched ? View.VISIBLE : View.INVISIBLE);
        }

        /**
         * hide, show icons and badges for levels depending on day
         */
        private void setVisibilityForLevelIcon(Day day) {

            setVisibilityForFirstIcon(day, CalendarUtils.getIconTotalForDay(day));
        }
    }
}

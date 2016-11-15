package com.verint.actionablecalendar.calendar;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.verint.actionablecalendar.calendar.listener.OnLoadMoreListener;
import com.verint.actionablecalendar.calendar.listener.OnMonthListScrollListener;
import com.verint.actionablecalendar.calendar.models.Direction;
import com.verint.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry Rusak on 11/15/16.
 * <p>
 */

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnMonthListScrollListener {

    public static final int VIEW_TYPE_MONTH_HEADER = 0;
    public static final int VIEW_TYPE_MONTH_DAY = 1;

    private final List<MixedVisibleMonth> mMonths;
    private final List<Day> mDays;

    private OnLoadMoreListener mOnLoadMoreListener;
    private CalendarCallbacks mItemClickListener;

    private boolean mLoadingInProgress;

    public CalendarRecyclerViewAdapter(@NonNull List<MixedVisibleMonth> months) {
        mMonths = months;
        mDays = new ArrayList<>();
        for (MixedVisibleMonth month : months) {
            mDays.addAll(month.getDayListWithHeaders());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDays.get(position).getDayState().getType() == DayState.DayType.MONTH_HEADER ?
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

    @Override
    public void onMonthListScroll(@NonNull LinearLayoutManager linearLayoutManager, @NonNull Direction scrollDirection) {
        switch (scrollDirection){

            case UP:
                final int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (/*!mLoadingInProgress && */(firstVisibleItemPosition - 30) <= 0){
                    if (mOnLoadMoreListener != null){
                        mOnLoadMoreListener.onLoadMore(scrollDirection);
                    }
                    mLoadingInProgress = true;
                }
                break;

            case DOWN:
                final int totalItemCount = linearLayoutManager.getItemCount();
                final int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (/*!mLoadingInProgress && */totalItemCount <= (lastVisibleItemPosition + 30)){
                    if (mOnLoadMoreListener != null){
                        mOnLoadMoreListener.onLoadMore(scrollDirection);
                    }
                    mLoadingInProgress = true;
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

    /**
     * Adds {@link MixedVisibleMonth} item to data list at position 0 and notifies adapter that item was
     * inserted
     *
     * @param month {@link MixedVisibleMonth}|null
     */
    public void addItemAtBeginning(final MixedVisibleMonth month){

        if (mMonths == null){
            throw new IllegalStateException("Data was not initialized");
        }

        mMonths.add(0, month);

        List<Day> newDays = month.getDayListWithHeaders();
        mDays.addAll(0, newDays);
        notifyItemRangeInserted(0, newDays.size());
    }

    /**
     * Adds {@link MixedVisibleMonth} item to data list at the position of the end of the list and notifies that
     * item was inserted at the end of list
     *
     * @param month {@link MixedVisibleMonth}|null
     */
    public void addItemAtTheEnd(final MixedVisibleMonth month){

        if (mMonths == null){
            throw new IllegalStateException("Data was not initialized");
        }

        // Count
        mMonths.add(month);
        int startIndex = mDays.size();
        List<Day> newDays = month.getDayListWithHeaders();
        mDays.addAll(newDays);
        notifyItemRangeInserted(startIndex, newDays.size());
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView;
        }

        public void bind(Day day) {
            mTvTitle.setText(day.getDate().toString());
        }
    }

    public void setLoaded(){
        mLoadingInProgress = false;
    }

    public List<MixedVisibleMonth> getMonths() {
        return mMonths;
    }

    class MonthDayViewHolder extends RecyclerView.ViewHolder {

        protected View mRootView;
        protected TextView mMonthDay;
        protected View mShiftIndicator;
        protected AuctionBidView mDayIconFirstLevelView;
        protected AuctionBidView mDayIconSecondLevelView;

        public MonthDayViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mMonthDay = (TextView) itemView.findViewById(R.id.tvMonthGridItemMonthDay);
            mShiftIndicator = itemView.findViewById(R.id.vMonthGridItemShiftIndicator);
            mDayIconFirstLevelView = (AuctionBidView) itemView.findViewById(R.id.abvCalendarDayIconFirstLevelView);
            mDayIconSecondLevelView = (AuctionBidView) itemView.findViewById(R.id.abvCalendarDayIconSecondLevelView);
        }

        public void bind(@NonNull final Day day, final CalendarCallbacks listener){

            switch (day.getDayState().getType()){

                case CURRENT_MONTH_DAY_NORMAL: // Current month day

                    mMonthDay.setVisibility(View.VISIBLE);
                    mShiftIndicator.setVisibility(day.isShiftEnabled() ? View.VISIBLE : View.INVISIBLE);

                    setVisibilityForLevelIcon(day);

                    mMonthDay.setText(String.valueOf(day.getMonthDay()));

                    if (!CalendarUtils.isToday(day)){ // Not today
                        mRootView.setBackgroundColor(Color.WHITE);
                    } else { // Today
                        mRootView.setBackgroundResource(R.drawable.calendar_item_current_day_background);
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
                        mRootView.setBackgroundColor(Color.parseColor("#ebebeb"));
                    } else { // Today
                        mRootView.setBackgroundResource(R.drawable.calendar_item_current_day_background);
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

                    mRootView.setBackgroundColor(Color.WHITE);

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
         * hide, show icons and badges for levels depending on day
         */
        private void setVisibilityForLevelIcon(Day day) {

            if (day.getTimeOffItem() != null || day.getAuctionWithBidItem() != null) {
                mDayIconFirstLevelView.setVisibility(View.VISIBLE);
                if (day.getTimeOffItem() != null) {
                    mDayIconFirstLevelView.setImage(day.getTimeOffItem().getAuctionImage());
                    mDayIconFirstLevelView.setBadge(day.getTimeOffItem().getBadgeImage());
                } else {
                    mDayIconFirstLevelView.setImage(day.getAuctionWithBidItem().getAuctionImage());
                    mDayIconFirstLevelView.setBadge(day.getAuctionWithBidItem().getBadgeImage());
                }

                if (day.getAuctionNoBidItem() != null) {
                    mDayIconSecondLevelView.setVisibility(View.VISIBLE);
                    mDayIconSecondLevelView.setImage(day.getAuctionNoBidItem().getAuctionImage());
                    mDayIconSecondLevelView.setBadge(day.getAuctionNoBidItem().getBadgeImage());
                } else {
                    mDayIconSecondLevelView.setVisibility(View.GONE);
                }
            } else {
                if (day.getAuctionNoBidItem() != null) {
                    mDayIconFirstLevelView.setVisibility(View.VISIBLE);
                    mDayIconFirstLevelView.setImage(day.getAuctionNoBidItem().getAuctionImage());
                    mDayIconFirstLevelView.setBadge(day.getAuctionNoBidItem().getBadgeImage());
                } else {
                    mDayIconFirstLevelView.setVisibility(View.GONE);
                }
                mDayIconSecondLevelView.setVisibility(View.GONE);
            }

        }
    }
}

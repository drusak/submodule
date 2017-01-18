package com.verint.actionablecalendar.calendar;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.verint.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Makes adapting of model data to view by inflating related layouts and binding data accordingly
 *
 * Created by acheshihin on 8/3/2016.
 */
public class MonthGridAdapter extends BaseAdapter {

    private static final String TAG = MonthGridAdapter.class.getSimpleName();

    private MixedVisibleMonth mMonthDate;
    private CalendarCallbacks mListener;
    private List<Day> mDayList;

    private int mGridItemLayoutId;
    private int mSelectedPosition = -1;

    /**
     * Constructor, receives resource layout id for item and data to represent
     *
     * @param gridItemLayoutId int, layout resource id
     * @param monthDate {@link MixedVisibleMonth} with data to represent
     * @param listener {@link CalendarCallbacks} listener for click events
     */
    public MonthGridAdapter(@LayoutRes final int gridItemLayoutId,
                            @NonNull final MixedVisibleMonth monthDate,
                            @NonNull final CalendarCallbacks listener){

        mGridItemLayoutId = gridItemLayoutId;
        mMonthDate = monthDate;
        if (mDayList == null){
            mDayList = new ArrayList<>();
        }
        mDayList.addAll(monthDate.getDayList());
        mListener = listener;
    }

    /**
     * Replace existing data set used by adapter to newly provided as an argument
     *
     * @param dayList - list of  {@link Day} objects
     */
    public void replace(final List<Day> dayList){

        if (mDayList == null){
            throw new IllegalArgumentException("Provided argument can't be  null");
        }

        if (!mDayList.isEmpty()) {
            mDayList.clear();
        }

        mDayList.addAll(dayList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDayList.size();
    }

    @Override
    public Object getItem(final int position) {
        return mDayList.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final MonthGridViewHolder viewHolder;

        if (convertView == null){

            // Prepare view
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.month_grid_item, parent, false);
            // Create view holder and store references to widgets
            viewHolder = new MonthGridViewHolder(convertView);
            // Store tag into view for further usage
            convertView.setTag(viewHolder);

        } else {
            // Retrieve view holder from view
            viewHolder = (MonthGridViewHolder) convertView.getTag();
        }

        viewHolder.bind(mDayList.get(position), position, mListener);
        return convertView;
    }

    // --------------------------------------------------------------------------------------------
    protected static class MonthGridViewHolder {

        protected View mRootView;
        protected TextView mMonthDay;
        protected View mShiftIndicator;
        protected EventIndicatorView mDayIconFirstLevelView;
        protected EventIndicatorView mDayIconSecondLevelView;

        protected MonthGridViewHolder(@NonNull final View view){

            mRootView = view;
            mMonthDay = (TextView) view.findViewById(R.id.tvMonthGridItemMonthDay);
            mShiftIndicator = view.findViewById(R.id.vMonthGridItemShiftIndicator);
            mDayIconFirstLevelView = (EventIndicatorView) view.findViewById(R.id.abvCalendarDayIconFirstLevelView);
            mDayIconSecondLevelView = (EventIndicatorView) view.findViewById(R.id.abvCalendarDayIconSecondLevelView);
        }

        protected void bind(@NonNull final Day day, final int position,
                         final CalendarCallbacks listener){

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
                                listener.onCalendarItemClick(day, position);
                            }
                        }
                    });

                    mRootView.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {

                            if (listener != null){
                                listener.onCalendarItemLongClick(day, position);
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
                                listener.onCalendarItemClick(day, position);
                            }
                        }
                    });

                    mRootView.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {

                            if (listener != null){
                                listener.onCalendarItemLongClick(day, position);
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

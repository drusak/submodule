package com.verint.actionablecalendar.calendar;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.verint.mylibrary.R;

/**
 * Makes adapting of model data to view by inflating related layouts and binding data accordingly
 *
 * Created by acheshihin on 8/3/2016.
 */
public class MonthGridAdapter extends BaseAdapter {

    private int mGridItemLayoutId;
    private MixedVisibleMonth mMonthDate;
    private int mSelectedPosition = -1;
    private CalendarCallbacks mListener;

    private int mNormalDayBackgroundColorId;
    private int mWeekendDayBackgroundColorId;

    /**
     * Constructor, receives resource layout id for item and data to represent
     *
     * @param gridItemLayoutId int, layout resource id
     * @param monthDate {@link MixedVisibleMonth} with data to represent
     * @param listener {@link CalendarCallbacks} listener for click events
     */
    public MonthGridAdapter(@LayoutRes final int gridItemLayoutId,
                            @NonNull MixedVisibleMonth monthDate,
                            @NonNull CalendarCallbacks listener){

        mGridItemLayoutId = gridItemLayoutId;
        mMonthDate = monthDate;
        mListener = listener;

    }


    /**
     * Replace existing data set used by adapter to newly provided as an argument
     *
     * @param monthDate {@link MixedVisibleMonth}
     */
    public void replace(MixedVisibleMonth monthDate){

        if (monthDate == null){
            throw new IllegalArgumentException("Provided argument can't be  null");
        }

        mMonthDate = monthDate;
    }

    @Override
    public int getCount() {
        return mMonthDate.getCount();
    }

    @Override
    public Object getItem(int position) {

        return mMonthDate.getDay(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        MonthGridViewHolder viewHolder;

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

        viewHolder.bind(mMonthDate.getDay(position), position, mListener);
        return convertView;
    }

    // --------------------------------------------------------------------------------------------
    protected static class MonthGridViewHolder {

        protected View mRootView;
        protected TextView mMonthDay;
        protected View mShiftIndicator;
        protected AuctionBidView mAuctionBidView;
        protected ImageView mBidView;

        MonthGridViewHolder(@NonNull final View view){
            mRootView = view;
            mMonthDay = (TextView) view.findViewById(R.id.tvMonthGridItemMonthDay);
            mShiftIndicator = view.findViewById(R.id.vMonthGridItemShiftIndicator);
            mAuctionBidView = (AuctionBidView) view.findViewById(R.id.abvCalendarAuctionBidView);
            mBidView = (ImageView) view.findViewById(R.id.ivCalendarBidView);
        }

        protected void bind(@NonNull final Day day,
                         final int position,
                         final CalendarCallbacks listener){

            switch (day.getDayState().getType()){

                case CURRENT_MONTH_DAY_NORMAL: // Current month

                    mMonthDay.setVisibility(View.VISIBLE);
                    mShiftIndicator.setVisibility(day.isShiftEnabled() ? View.VISIBLE : View.INVISIBLE);
                    // mShiftIndicator.setVisibility(View.VISIBLE);
                    mBidView.setVisibility(View.VISIBLE);
                    mAuctionBidView.setVisibility(View.VISIBLE);

                    // Change background color
                    mRootView.setBackgroundColor(Color.WHITE);
                    // Change day value text color
                    mMonthDay.setTextColor(CalendarUtils.isToday(day) ? Color.RED : Color.BLACK);
                    mMonthDay.setText(String.valueOf(day.getMonthDay()));

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

                case CURRENT_MONTH_DAY_WEEKEND: // Week end

                    // Change day value text color
                    mMonthDay.setTextColor(CalendarUtils.isToday(day) ? Color.RED : Color.BLACK);
                    mMonthDay.setText(String.valueOf(day.getMonthDay()));

                    mMonthDay.setVisibility(View.VISIBLE);
                    mShiftIndicator.setVisibility(View.INVISIBLE);
                    mBidView.setVisibility(View.INVISIBLE);
                    mAuctionBidView.setVisibility(View.INVISIBLE);

                    // TODO: Reimplement
                    mRootView.setBackgroundColor(Color.parseColor("#ebebeb"));

                    // TODO: Consider adding click listeners for this specific view
                    // Remove click listeners
                    mRootView.setOnClickListener(null);
                    mRootView.setOnLongClickListener(null);
                    break;

                case NON_CURRENT_MONTH_DAY: // Previous or next month

                    // TODO: Consider remove binding of text since visibility will be changed anyway
                    // mMonthDay.setText("");

                    mMonthDay.setVisibility(View.INVISIBLE);
                    mShiftIndicator.setVisibility(View.INVISIBLE);
                    mBidView.setVisibility(View.INVISIBLE);
                    mAuctionBidView.setVisibility(View.INVISIBLE);

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
    }
}

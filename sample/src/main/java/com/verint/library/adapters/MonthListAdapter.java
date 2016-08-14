package com.verint.library.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.verint.actionablecalendar.calendar.CalendarBuilder;
import com.verint.actionablecalendar.calendar.CalendarCallbacks;
import com.verint.actionablecalendar.calendar.CalendarDataFactory;
import com.verint.actionablecalendar.calendar.CalendarUtils;
import com.verint.actionablecalendar.calendar.CalendarWidget;
import com.verint.actionablecalendar.calendar.MixedVisibleMonth;
import com.verint.actionablecalendar.calendar.models.Direction;
import com.verint.library.R;
import com.verint.library.listeners.OnLoadMoreListener;
import com.verint.library.listeners.OnMonthListScrollListener;

import java.util.Date;
import java.util.List;

/**
 * Adapter for representation of list of {@link CalendarWidget} widgets
 *
 * Created by acheshihin on 8/10/2016.
 */
public class MonthListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnMonthListScrollListener {

    private static final String TAG = MonthListAdapter.class.getSimpleName();

    public static final int VISIBLE_THRESHOLD = 3;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;
    private CalendarCallbacks mListener;

    private boolean mLoadingInProgress;

    private int mFirstVisibleItem;
    private int mLastVisibleItem;
    private int mTotalItemCount;

    private List<Date> mData;


    public MonthListAdapter(@NonNull List<Date> data, @NonNull CalendarCallbacks listener){

        mData = data;
        mListener = listener;
    }

    /**
     * Assigns listener for load more event
     *
     * @param listener {@link OnLoadMoreListener}
     */
    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener listener){
        mOnLoadMoreListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case VIEW_TYPE_ITEM:
                return new MonthViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.month_list_item, parent, false));

            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.month_list_item_loading, parent, false));

            default:
                throw new IllegalStateException("Unknown view type found");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MonthViewHolder){

            // Build days object for representation
            MixedVisibleMonth month = CalendarDataFactory.newInstance()
                    .create(CalendarUtils.getCalendarFrom(getListItem(position)).getTime());
            // Bind data to adapter
            ((MonthViewHolder) holder).mCalendarWidget
                    .set(month, new CalendarBuilder(R.layout.month_grid_item, mListener));

        } else if (holder instanceof LoadingViewHolder){

            ((LoadingViewHolder) holder).mProgressBar.setIndeterminate(true);

        } else {

            throw new IllegalStateException("View holder is null or belongs to improper type");
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    /**
     * Adds {@link Date} item to data list at last position and notifies adapter that data
     * was changed
     *
     * @param date {@link Date}|null
     */
    public void addItem(Date date){

        if (mData == null){
            throw new IllegalStateException("Data list was not initialized");
        }

        mData.add(date);
        notifyItemInserted(mData.size()-1);
    }

    /**
     * Adds {@link Date} item to data list at position 0 and notifies adapter that item was
     * inserted
     *
     * @param date {@link Date}|null
     */
    public void addItemAtBeginning(Date date){

        if (mData == null){
            throw new IllegalStateException("Data list was not initialized");
        }

        mData.add(0, date);
        notifyItemInserted(0);
    }

    /**
     * Adds {@link Date} item to data list at the position of the end of the list and notifies that
     * item was inserted at the end of list
     *
     * @param date {@link Date}|null
     */
    public void addItemAtTheEnd(Date date){

        if (mData == null){
            throw new IllegalStateException("Data list was not initialized");
        }

        // Count
        final int listCount = mData.size() - 1;
        mData.add(date);
        notifyItemInserted(listCount + 1);
    }

    /**
     * Removes {@link Date} item from the data list's last position and notifies adapter that
     * item at the end of list was removed
     */
    public void removeLastItem(){

        if (mData == null){
            throw new IllegalStateException("Data list was not initialized");
        }

        final int listCount = mData.size() - 1;
        mData.remove(listCount);
        notifyItemRemoved(listCount);
    }

    /**
     * Removes {@link Date} item from the data list's last position and notifies adapter that
     * item at the end of list was removed
     */
    public void removeFirstItem(){

        if (mData == null){
            throw new IllegalStateException("Data list was not initialized");
        }

        mData.remove(0);
        notifyItemRemoved(0);
    }

    /**
     * Removes {@link Date} item from data list at the desired position and notifies adapter regarding
     * the data change
     *
     * @param position
     */
    public void removeItem(final int position){

        mData.remove(position);
        notifyItemRemoved(position);
    }

    private Date getListItem(final int position){

        if (getItemCount() < position){
            throw new IllegalArgumentException("Position is out of bounds");
        }
        return mData.get(position);
    }

    // {@link OnLoadMoreListener} region begin

    /**
     * Changes loading in progress state value to false (Loading completed)
     */
    public void setLoaded(){
        mLoadingInProgress = false;
    }

    // {@link OnLoadMoreListener} region end

    // {@link OnMonthListScrollListener} region begin

    @Override
    public void onMonthListScroll(@NonNull LinearLayoutManager linearLayoutManager) {

        // Get current visible item position
        int currentFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

        if (currentFirstVisibleItemPosition > mFirstVisibleItem){ // Load data for future

            mTotalItemCount = linearLayoutManager.getItemCount();
            mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

            if (!mLoadingInProgress && mTotalItemCount <= (mLastVisibleItem + VISIBLE_THRESHOLD)){
                if (mOnLoadMoreListener != null){
                    mOnLoadMoreListener.onLoadMore(Direction.DOWN);
                }
                mLoadingInProgress = true;
            }

        } else { // Load data for past

            if (!mLoadingInProgress && (currentFirstVisibleItemPosition - VISIBLE_THRESHOLD) <= 0){
                if (mOnLoadMoreListener != null){
                    mOnLoadMoreListener.onLoadMore(Direction.UP);
                }
                mLoadingInProgress = true;
            }
        }

        mFirstVisibleItem = currentFirstVisibleItemPosition;
    }

    // {@link OnMonthListScrollListener} region end



    // --------------------------------------------------------------------------------------------
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar mProgressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.pbMonthListItemLoadingProgressBar);
        }
    }

    // --------------------------------------------------------------------------------------------
    public static class MonthViewHolder extends RecyclerView.ViewHolder {

        public CalendarWidget mCalendarWidget;

        public MonthViewHolder(View itemView) {
            super(itemView);
            mCalendarWidget = (CalendarWidget) itemView.findViewById(R.id.cwMonthListItem);
        }
    }
}

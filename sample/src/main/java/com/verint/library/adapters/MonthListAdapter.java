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
import com.verint.actionablecalendar.calendar.CalendarUtils;
import com.verint.actionablecalendar.calendar.CalendarWidget;
import com.verint.actionablecalendar.calendar.MixedVisibleMonth;
import com.verint.actionablecalendar.calendar.listener.OnLoadMoreListener;
import com.verint.actionablecalendar.calendar.listener.OnMonthListScrollListener;
import com.verint.actionablecalendar.calendar.models.Direction;
import com.verint.library.R;

import java.util.Calendar;
import java.util.List;

/**
 * Adapter for representation of list of {@link CalendarWidget} widgets
 *
 * Created by acheshihin on 8/10/2016.
 */
public class MonthListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnMonthListScrollListener {

    // This constant specifies amount of items to load when user approaching the end of the list
    public static final int VISIBLE_THRESHOLD = 3;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;
    private final CalendarCallbacks mListener;

    private boolean mLoadingInProgress;

    private final List<MixedVisibleMonth> mData;

    public MonthListAdapter(List<MixedVisibleMonth> data,
                            @NonNull CalendarCallbacks listener){

        if (data == null){
            throw new IllegalArgumentException("Provided data argument is null");
        }

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

            MixedVisibleMonth month = getListItem(position);
            // Bind data to adapter
            ((MonthViewHolder) holder).mCalendarWidget
                    .set(month, new CalendarBuilder(R.layout.month_grid_item, mListener));

            /*// Build days object for representation
            MixedVisibleMonth month = CalendarDataFactory.newInstance()
                    .create(CalendarUtils.getCalendarFrom(getListItem(position)).getTime());
            // Bind data to adapter
            ((MonthViewHolder) holder).mCalendarWidget
                    .set(month, new CalendarBuilder(R.layout.month_grid_item, mListener));*/


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
     * Returns position of item which represents current month and year
     *
     * @return int, position of current month or {@link RecyclerView#NO_POSITION} if not found
     */
    public final int getCurrentMonthPosition(){

        if (mData == null){
            throw new IllegalStateException("Data list was not initialized");
        }

        Calendar currentMonthCalendar = CalendarUtils.getCalendarForToday();
        final int currentMonth = currentMonthCalendar.get(Calendar.MONTH);
        final int currentYear = currentMonthCalendar.get(Calendar.YEAR);

        int itemCount = getItemCount();
        for (int i=0; i < itemCount; i++){

            final MixedVisibleMonth month = getListItem(i);
            if (month != null){ // If loader item it will be null, hence skip iteration for it

                Calendar dateCalendar = month.getCurrentMonth().getDay(0).getCalendar();
                if (dateCalendar.get(Calendar.MONTH) == currentMonth
                        && dateCalendar.get(Calendar.YEAR) == currentYear){
                    return i;
                }
            }
        }

        return RecyclerView.NO_POSITION;
    }

    /**
     * Adds {@link MixedVisibleMonth} item to data list at last position and notifies adapter that data
     * was changed
     *
     * @param month {@link MixedVisibleMonth}|null
     */
    public void addItem(MixedVisibleMonth month){

        if (mData == null){
            throw new IllegalStateException("Data was not initialized");
        }

        mData.add(month);
        notifyItemInserted(mData.size()-1);
    }

    /**
     * Adds {@link MixedVisibleMonth} item to data list at position 0 and notifies adapter that item was
     * inserted
     *
     * @param month {@link MixedVisibleMonth}|null
     */
    public void addItemAtBeginning(final MixedVisibleMonth month){

        if (mData == null){
            throw new IllegalStateException("Data was not initialized");
        }

        mData.add(0, month);
        notifyItemInserted(0);
    }

    /**
     * Adds {@link MixedVisibleMonth} item to data list at the position of the end of the list and notifies that
     * item was inserted at the end of list
     *
     * @param month {@link MixedVisibleMonth}|null
     */
    public void addItemAtTheEnd(final MixedVisibleMonth month){

        if (mData == null){
            throw new IllegalStateException("Data was not initialized");
        }

        // Count
        final int listCount = mData.size() - 1;
        mData.add(month);
        notifyItemInserted(listCount + 1);
    }

    /**
     * Removes {@link MixedVisibleMonth} item from the data list's last position and notifies adapter that
     * item at the end of list was removed
     */
    public void removeLastItem(){

        if (mData == null){
            throw new IllegalStateException("Data was not initialized");
        }

        final int listCount = mData.size() - 1;
        mData.remove(listCount);
        notifyItemRemoved(listCount);
    }

    /**
     * Removes {@link MixedVisibleMonth} item from the data list's last position and notifies adapter that
     * item at the end of list was removed
     */
    public void removeFirstItem(){

        if (mData == null){
            throw new IllegalStateException("Data was not initialized");
        }

        mData.remove(0);
        notifyItemRemoved(0);
    }

    public void addItemAtPosition(final int position, @NonNull MixedVisibleMonth newMonth){

        if (mData == null) {
            throw new IllegalStateException("Data was not initialized");
        }

        mData.set(position, newMonth);
        notifyItemChanged(position);
    }

    /**
     * Removes {@link MixedVisibleMonth} item from data list at the desired position and notifies adapter regarding
     * the data change
     *
     * @param position position of item within list which should be removed
     */
    public void removeItem(final int position){

        if (mData == null) {
            throw new IllegalStateException("Data was not initialized");
        }

        mData.remove(position);
        notifyItemRemoved(position);
    }

    private MixedVisibleMonth getListItem(final int position){

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

    /**
     * After notification regarding scroll event within list decide if additional items should
     * be loaded and trigger loading part accordingly
     *
     * @param linearLayoutManager utilized {@link LinearLayoutManager}
     * @param scrollDirection {@link Direction} gesture direction of occurred scroll event
     */
    @Override
    public void onMonthListScroll(@NonNull final LinearLayoutManager linearLayoutManager,
                                  @NonNull final Direction scrollDirection) {

        switch (scrollDirection){

            case UP:
                final int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (!mLoadingInProgress && (firstVisibleItemPosition - VISIBLE_THRESHOLD) <= 0){
                    if (mOnLoadMoreListener != null){
                        mOnLoadMoreListener.onLoadMore(scrollDirection);
                    }
                    mLoadingInProgress = true;
                }
                break;

            case DOWN:
                final int totalItemCount = linearLayoutManager.getItemCount();
                final int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (!mLoadingInProgress && totalItemCount <= (lastVisibleItemPosition + VISIBLE_THRESHOLD)){
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

    // {@link OnMonthListScrollListener} region end

    // --------------------------------------------------------------------------------------------
    protected static class LoadingViewHolder extends RecyclerView.ViewHolder {

        protected ProgressBar mProgressBar;

        protected LoadingViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.pbMonthListItemLoadingProgressBar);
        }
    }

    // --------------------------------------------------------------------------------------------
    protected static class MonthViewHolder extends RecyclerView.ViewHolder {

        protected CalendarWidget mCalendarWidget;

        protected MonthViewHolder(View itemView) {
            super(itemView);
            mCalendarWidget = (CalendarWidget) itemView.findViewById(R.id.cwMonthListItem);
        }
    }
}

package com.verint.library.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.verint.actionablecalendar.calendar.CalendarBuilder;
import com.verint.actionablecalendar.calendar.CalendarCallbacks;
import com.verint.actionablecalendar.calendar.CalendarDataFactory;
import com.verint.actionablecalendar.calendar.CalendarUtils;
import com.verint.actionablecalendar.calendar.CalendarWidget;
import com.verint.actionablecalendar.calendar.MixedVisibleMonth;
import com.verint.library.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Adapter for representation of list of {@link CalendarWidget} widgets
 *
 * Created by acheshihin on 8/10/2016.
 */
public class MonthListAdapter extends RecyclerView.Adapter<MonthListAdapter.MonthListItemViewHolder> {

    private static final String TAG = MonthListAdapter.class.getSimpleName();

    private List<Date> mData;
    private CalendarCallbacks mListener;

    public MonthListAdapter(@NonNull List<Date> data, @NonNull CalendarCallbacks listener){
        mData = data;
        mListener = listener;
    }


    @Override
    public MonthListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MonthListItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.month_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MonthListItemViewHolder holder, int position) {

        // TODO: Consider rolling back to commented implementation in case test will fail

        Date date = getListItem(position);

        CalendarDataFactory factory = CalendarDataFactory.newInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        MixedVisibleMonth month = factory.create(date);

        holder.mCalendarWidget.set(month, new CalendarBuilder(R.layout.month_grid_item, mListener));

        Log.i(TAG, "Position: " + position + ", for: " + CalendarUtils.getHumanFriendlyCalendarRepresentation(CalendarUtils.getCalendarFrom(date)));
        Log.i(TAG, "Month: " + month.toString());


        // Build days object for representation
        // MixedVisibleMonth month = new CalendarDataFactory().create(CalendarUtils.getCalendarFrom(getListItem(position)).getTime());
        // Bind data to adapter
        // holder.mCalendarWidget.set(month, new CalendarBuilder(R.layout.month_grid_item, mListener));
    }

    private Date getListItem(final int position){

        if (getItemCount() < position){
            throw new IllegalArgumentException("Position is out of bounds");
        }
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        if (mData == null){
            throw new IllegalStateException("Data was not initialized");
        }

        return mData.size();
    }

    public static class MonthListItemViewHolder extends RecyclerView.ViewHolder {

        CalendarWidget mCalendarWidget;

        public MonthListItemViewHolder(View itemView) {
            super(itemView);
            mCalendarWidget = (CalendarWidget) itemView.findViewById(R.id.cwMonthListItem);
        }
    }
}

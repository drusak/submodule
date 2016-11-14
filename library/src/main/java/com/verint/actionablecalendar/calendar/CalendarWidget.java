package com.verint.actionablecalendar.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.verint.actionablecalendar.weekday.HeightWrapGridView;
import com.verint.mylibrary.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Represents calendar view with grid of days representing all days of current month, and partially
 * next and previous months as well
 *
 * Created by acheshihin on 8/4/2016.
 */
public class CalendarWidget extends LinearLayout {

    private TextView mDateTitle;
    private HeightWrapGridView mGridView;
    private MonthGridAdapter mAdapter;

    public CalendarWidget(Context context) {
        super(context);
        init();
    }

    public CalendarWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Assigns data to {@link CalendarWidget} view
     *
     * @param mixedMonth {@link MixedVisibleMonth} with data for {@link CalendarWidget} adapter
     * @param factory {@link CalendarBuilder} required for creating {@link MonthGridAdapter}
     */
    public void set(MixedVisibleMonth mixedMonth, CalendarBuilder factory){
        // Assign generated year and month title to related view
        setMonthTitle(mixedMonth.getCurrentMonth());
        // Assign generated month days data to related view
        setMonthDays(mixedMonth, factory);
    }

    private void init(){

        // Inflate provided layout
        final View view
                = LayoutInflater.from(getContext()).inflate(R.layout.calendar_widget, this, true);
        setOrientation(VERTICAL);
        // Set bridges from XML to java part
        mDateTitle = (TextView) view.findViewById(R.id.tvCalendarWidgetDateTitle);
        mGridView = (HeightWrapGridView) view.findViewById(R.id.hwgvCalendarWidgetContent);
    }

    private void setMonthTitle(VisibleMonth currentMonth){

        Date firstDayOfCurrentMonth = currentMonth.getDay(0).getDate();
        Calendar calendar = CalendarUtils.getCalendarFrom(firstDayOfCurrentMonth);
        final String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault());
        final int year = calendar.get(Calendar.YEAR);
        mDateTitle.setText(String.format(Locale.getDefault(), "%s %d", monthName, year));
    }

    private void setMonthDays(@NonNull MixedVisibleMonth monthDate, CalendarBuilder factory){

        if (mAdapter == null){
            // Create new adapter and assign it to view
            mAdapter = factory.createAdapterFor(monthDate);
            mGridView.setAdapter(mAdapter);

        } else {
            // Replace data of currently shown adapter and invalidate widget
            mAdapter.replace(monthDate.getDayList());
            mGridView.invalidateViews();
        }
    }
}

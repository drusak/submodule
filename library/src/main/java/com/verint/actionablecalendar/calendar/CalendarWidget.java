package com.verint.actionablecalendar.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
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
 *
 * Created by acheshihin on 8/4/2016.
 */
public class CalendarWidget extends LinearLayout {

    private static final String TAG = CalendarWidget.class.getSimpleName();

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

    private void init(){

        View view = LayoutInflater.from(getContext()).inflate(R.layout.calendar_widget, this, true);

        mDateTitle = (TextView) view.findViewById(R.id.tvCalendarWidgetDateTitle);
        mGridView = (HeightWrapGridView) view.findViewById(R.id.hwgvCalendarWidgetContent);
    }

    public void set(MixedVisibleMonth mixedMonth, CalendarBuilder factory){

        // Assign generated year and month to related view
        setMonthAndYearDate(mixedMonth.getCurrentMonth());
        // Assign generated month days data to related view
        setMonthDays(mixedMonth, factory);
    }

    private void setMonthAndYearDate(VisibleMonth currentMonth){

        Date firstDayOfCurrentMonth = currentMonth.getDay(0).getDate();
        Calendar calendar = CalendarUtils.getCalendarFrom(firstDayOfCurrentMonth);
        final String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault());
        final int year = calendar.get(Calendar.YEAR);
        mDateTitle.setText(String.format(Locale.getDefault(), "%s %d", monthName, year));
    }

    private void setMonthDays(@NonNull MixedVisibleMonth monthDate, CalendarBuilder factory){

        if (mAdapter == null){

            mAdapter = factory.createAdapterFor(monthDate);
            mGridView.setAdapter(mAdapter);

        } else {

            // Replace data within adapter
            mAdapter.replace(monthDate);
            mGridView.invalidateViews();
        }
    }
}

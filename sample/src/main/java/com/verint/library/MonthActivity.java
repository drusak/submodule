package com.verint.library;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.verint.actionablecalendar.calendar.CalendarCallbacks;
import com.verint.actionablecalendar.calendar.CalendarUtils;
import com.verint.actionablecalendar.calendar.Day;
import com.verint.actionablecalendar.weekday.WeekDayBuilder;
import com.verint.actionablecalendar.weekday.WeekDayDataFactory;
import com.verint.actionablecalendar.weekday.WeekDayWidget;
import com.verint.library.adapters.MonthListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents list of {@link com.verint.actionablecalendar.calendar.CalendarWidget} widgets
 * according to predefined date range
 */
public class MonthActivity extends AppCompatActivity implements CalendarCallbacks {

    private List<Date> mDateList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MonthListAdapter mAdapter;

    private WeekDayWidget mWeekDayWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        initWeekDayNames();
    }

    private void init() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rvMainActivityMonthList);

        generateMonthList(new Date(System.currentTimeMillis()));

        mAdapter = new MonthListAdapter(mDateList, MonthActivity.this);
        mLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // TODO: Set to true once you finish tests
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void generateMonthList(@NonNull Date date){

        if (mDateList == null){
            mDateList = new ArrayList<>();
        }

        if (!mDateList.isEmpty()){
            mDateList.clear();
        }

        Calendar calendarStart = CalendarUtils.getCalendarFrom(new Date(System.currentTimeMillis()));
        Calendar calendarEnd = (Calendar) calendarStart.clone();
        calendarEnd.add(Calendar.YEAR, 3);

        mDateList.addAll(CalendarUtils.generateMonthRange(calendarStart, calendarEnd));
    }

    private void initWeekDayNames(){

        mWeekDayWidget = (WeekDayWidget) findViewById(R.id.wdwMainActivityWeekNameList);
        WeekDayBuilder builder = new WeekDayBuilder(R.layout.weekday_header_layout);
        mWeekDayWidget.set(new WeekDayDataFactory().create());
    }

    // {@link CalendarCallbacks} region begin

    @Override
    public void onCalendarItemClick(@NonNull Day day, int position) {

        Calendar calendar = CalendarUtils.getCalendarFrom(day.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = String.format(Locale.getDefault(), "%d/%d/%d", monthDay, month, year);

        Toast.makeText(getApplicationContext(), "click " + date
                + ", position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarItemLongClick(@NonNull Day day, int position) {

        Calendar calendar = CalendarUtils.getCalendarFrom(day.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = String.format(Locale.getDefault(), "%d/%d/%d", monthDay, month, year);

        Toast.makeText(getApplicationContext(), "long click " + date
                + ", position: " + position, Toast.LENGTH_SHORT).show();
    }

    // {@link CalendarCallbacks} region end
}

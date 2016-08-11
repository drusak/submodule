package com.verint.library;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.verint.actionablecalendar.calendar.CalendarCallbacks;
import com.verint.actionablecalendar.calendar.CalendarUtils;
import com.verint.actionablecalendar.calendar.Day;
import com.verint.actionablecalendar.calendar.models.AuctionBid;
import com.verint.actionablecalendar.calendar.models.Shift;
import com.verint.actionablecalendar.weekday.WeekDayBuilder;
import com.verint.actionablecalendar.weekday.WeekDayDataFactory;
import com.verint.actionablecalendar.weekday.WeekDayWidget;
import com.verint.library.adapters.MonthListAdapter;
import com.verint.library.listeners.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents list of {@link com.verint.actionablecalendar.calendar.CalendarWidget} widgets
 * according to predefined date range
 */
public class MonthActivity extends AppCompatActivity implements CalendarCallbacks, OnLoadMoreListener {

    private List<Date> mDateList;
    private List<Shift> mShiftList;
    private List<AuctionBid> mAuctionBidList;

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

        Date currentMonth = new Date(System.currentTimeMillis());
        generateInitialMonthList(currentMonth);

        mLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        mAdapter = new MonthListAdapter(mDateList, MonthActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(MonthActivity.this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mAdapter != null){
                    mAdapter.onMonthListScroll(mLayoutManager);
                }
            }
        });
    }

    private void generateInitialMonthList(@NonNull Date date){

        if (mDateList == null){
            mDateList = new ArrayList<>();
        }

        if (!mDateList.isEmpty()){
            mDateList.clear();
        }

        Calendar calendarStart = CalendarUtils.getCalendarFrom(date);
        Calendar calendarEnd = (Calendar) calendarStart.clone();
        calendarEnd.add(Calendar.MONTH, 2);

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

    // {@link OnLoadMoreListener} region begin
    @Override
    public void onLoadMore() {

        // Add null item in order to enable loading progress bar at the bottom
        mAdapter.addItem(null);

        // Load more data with simulated long running processing
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                // Remove null item in order to disable loading progress bar at the bottom
                mAdapter.removeItem(mDateList.size()-1);

                // Load more
                int index = mDateList.size();
                int end = index + MonthListAdapter.VISIBLE_THRESHOLD;
                for (int i=index; i < end; i++){

                    Date currentLastItem = mDateList.get(i-1);

                    if (currentLastItem == null){
                        // Since last item is current null
                        currentLastItem = mDateList.get(i-2);
                    }

                    final Date nextMonth = CalendarUtils.getNextMonth(currentLastItem);
                    // Add data here
                    mDateList.add(nextMonth);
                }

                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
            }
        }, 1_500L);
    }

    // {@link OnLoadMoreListener} region end
}

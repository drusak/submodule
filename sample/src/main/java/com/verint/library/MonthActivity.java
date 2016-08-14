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
import com.verint.actionablecalendar.calendar.models.Direction;
import com.verint.actionablecalendar.calendar.models.Shift;
import com.verint.actionablecalendar.weekday.WeekDayBuilder;
import com.verint.actionablecalendar.weekday.WeekDayDataFactory;
import com.verint.actionablecalendar.weekday.WeekDayWidget;
import com.verint.library.adapters.MonthListAdapter;
import com.verint.library.listeners.OnListScrollDirectionalListener;
import com.verint.library.listeners.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.verint.library.adapters.MonthListAdapter.VISIBLE_THRESHOLD;

/**
 * Represents list of {@link com.verint.actionablecalendar.calendar.CalendarWidget} widgets
 * according to predefined date range
 */
public class MonthActivity extends AppCompatActivity
        implements CalendarCallbacks, OnLoadMoreListener{

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
        initMonthListForDate(currentMonth);

        mLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        mAdapter = new MonthListAdapter(mDateList, MonthActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        // Scroll to current month between 3 initial months within list
        mRecyclerView.scrollToPosition(1);

        // Provide listener for load more flow
        mAdapter.setOnLoadMoreListener(MonthActivity.this);
        // Specify listener for scroll events in order to differentiate scroll direction
        mRecyclerView.addOnScrollListener(new OnListScrollDirectionalListener(mRecyclerView) {

            @Override
            public void onScrolledUp(RecyclerView recyclerView, int dx, int dy) {

                if (mAdapter != null){
                    mAdapter.onMonthListScroll(mLayoutManager, Direction.UP);
                }
            }

            @Override
            public void onScrolledDown(RecyclerView recyclerView, int dx, int dy) {

                if (mAdapter != null){
                    mAdapter.onMonthListScroll(mLayoutManager, Direction.DOWN);
                }
            }
        });
    }

    private void initMonthListForDate(@NonNull Date date){

        if (mDateList == null){
            mDateList = new ArrayList<>();
        }

        if (!mDateList.isEmpty()){
            mDateList.clear();
        }

        mDateList.addAll(CalendarUtils.generateInitialMonthList(date));
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
    public void onLoadMore(final Direction scrollDirection) {

        // Insert loading item first
        switch (scrollDirection){

            case UP: // Past dates
                //  TODO: Not implemented
                mAdapter.addItemAtBeginning(null);
                break;

            case DOWN:// Future dates
                // Add null item in order to enable loading progress bar at the bottom
                mAdapter.addItem(null);
                break;

            default:
                throw new IllegalStateException("Unknown case found");
        }


        // Load more data with simulated long running processing

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                switch (scrollDirection){

                    case DOWN:  // Load future dates

                        // Remove loading item
                        mAdapter.removeLastItem();

                        // Load more
                        int listCount = mDateList.size();
                        int newListCount = listCount + VISIBLE_THRESHOLD;

                        for (int i=listCount; i < newListCount; i++){
                            // Get current last item
                            final Date lastItemDate = mDateList.get(i-1);
                            mAdapter.addItemAtTheEnd(CalendarUtils.getNextMonth(lastItemDate));
                        }
                        break;

                    case UP: // Load past dates

                        // Remove loading item
                        mAdapter.removeFirstItem();

                        // Load more
                        for (int i=0; i < VISIBLE_THRESHOLD; i++){
                            // Get current first item
                            final Date firstItemDate = mDateList.get(0);
                            mAdapter.addItemAtBeginning(CalendarUtils.getPreviousMonth(firstItemDate));
                        }
                        break;

                    default:
                        throw new IllegalStateException("Unknown case found");
                }

                // Inform regarding data set change and finish of loading process
                mAdapter.setLoaded();
            }
        }, 1_500L);
    }

    // {@link OnLoadMoreListener} region end
}

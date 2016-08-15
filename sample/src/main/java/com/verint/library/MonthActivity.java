package com.verint.library;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.verint.actionablecalendar.calendar.CalendarCallbacks;
import com.verint.actionablecalendar.calendar.CalendarUtils;
import com.verint.actionablecalendar.calendar.Day;
import com.verint.actionablecalendar.calendar.MixedVisibleMonth;
import com.verint.actionablecalendar.calendar.models.Direction;
import com.verint.actionablecalendar.weekday.WeekDayBuilder;
import com.verint.actionablecalendar.weekday.WeekDayDataFactory;
import com.verint.actionablecalendar.weekday.WeekDayWidget;
import com.verint.library.adapters.MonthListAdapter;
import com.verint.library.listeners.OnListScrollDirectionalListener;
import com.verint.library.listeners.OnLoadMoreListener;

import java.lang.ref.WeakReference;
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

    // Week day name related fields
    private WeekDayWidget mWeekDayWidget;

    // Calendar related fields
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MonthListAdapter mAdapter;

    private List<Date> mMonthDateList;

    private List<MixedVisibleMonth> mMonthList;

    private Runnable mLoadMoreRunnable;

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
        mAdapter = new MonthListAdapter(mMonthDateList, MonthActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        // TODO: Consider implementing dedicated method to scroll to specific month
        mRecyclerView.scrollToPosition(mAdapter.getCurrentMonthPosition());

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

        // Add null accordingly to scroll direction in order to enable loading progress bar

        switch (scrollDirection){

            case DOWN:// Future dates
                mAdapter.addItemAtTheEnd(null);
                break;

            case UP: // Past dates
                mAdapter.addItemAtBeginning(null);
                break;

            default:
                throw new IllegalStateException("Unknown case found");
        }

        mLoadMoreRunnable = new LoadMoreRunnable(MonthActivity.this, scrollDirection);
        new Handler().postDelayed(mLoadMoreRunnable, 1_500L);
    }

    // {@link OnLoadMoreListener} region end

    private void initMonthListForDate(@NonNull Date date){

        if (mMonthDateList == null){
            mMonthDateList = new ArrayList<>();
        }

        if (!mMonthDateList.isEmpty()){
            mMonthDateList.clear();
        }

        mMonthDateList.addAll(CalendarUtils.generateInitialMonthList(date));
    }

    private void initWeekDayNames(){

        mWeekDayWidget = (WeekDayWidget) findViewById(R.id.wdwMainActivityWeekNameList);
        WeekDayBuilder builder = new WeekDayBuilder(R.layout.weekday_header_layout);
        mWeekDayWidget.set(new WeekDayDataFactory().create());
    }

    // --------------------------------------------------------------------------------------------
    protected static class MoreLoaderHandler extends Handler {

        private WeakReference<MonthActivity> mMonthActivity;

        public MoreLoaderHandler(@NonNull MonthActivity activity){
            mMonthActivity = new WeakReference<MonthActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }


        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    }

    // --------------------------------------------------------------------------------------------
    protected static class LoadMoreRunnable implements Runnable {

        private WeakReference<MonthActivity> mMonthActivity;
        private Direction mScrollDirection;

        protected LoadMoreRunnable(@NonNull MonthActivity activity,
                                   @NonNull final Direction scrollDirection){

            mMonthActivity = new WeakReference<MonthActivity>(activity);
            mScrollDirection = scrollDirection;
        }

        @Override
        public void run() {

            MonthActivity activity = mMonthActivity.get();

            if (activity != null){

                switch (mScrollDirection){

                    case DOWN:  // Load future dates

                        // Remove loading item
                        activity.mAdapter.removeLastItem();

                        // Load more
                        int listCount = activity.mMonthDateList.size();
                        int newListCount = listCount + VISIBLE_THRESHOLD;

                        for (int i=listCount; i < newListCount; i++){
                            // Get current last item
                            final Date lastItemDate = activity.mMonthDateList.get(i-1);
                            activity.mAdapter.addItemAtTheEnd(CalendarUtils.getNextMonth(lastItemDate));
                        }
                        break;

                    case UP: // Load past dates

                        // Remove loading item
                        activity.mAdapter.removeFirstItem();

                        // Load more
                        for (int i=0; i < VISIBLE_THRESHOLD; i++){
                            // Get current first item
                            final Date firstItemDate = activity.mMonthDateList.get(0);
                            activity.mAdapter.addItemAtBeginning(CalendarUtils.getPreviousMonth(firstItemDate));
                        }
                        break;

                    default:
                        throw new IllegalStateException("Unknown case found");
                }

                // Inform regarding data set change and finish of loading process
                activity.mAdapter.setLoaded();
            }
        }
    }
}

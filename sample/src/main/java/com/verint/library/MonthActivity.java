package com.verint.library;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.verint.actionablecalendar.calendar.CalendarCallbacks;
import com.verint.actionablecalendar.calendar.CalendarDataFactory;
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

import static com.verint.library.adapters.MonthListAdapter.VISIBLE_THRESHOLD;

/**
 * Represents list of {@link com.verint.actionablecalendar.calendar.CalendarWidget} widgets
 * according to predefined date range
 */
public class MonthActivity extends AppCompatActivity implements CalendarCallbacks,
        OnLoadMoreListener{

    // Title of week days above list of {@link CalendarWidget}
    private WeekDayWidget mWeekDayWidget;

    // Calendar related fields
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MonthListAdapter mAdapter;
    private List<MixedVisibleMonth> mMonthList;

    private Runnable mLoadMoreRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initWeekDayNames();
    }

    private void performTestUpdate(@NonNull Date date){

        final MixedVisibleMonth month = CalendarDataFactory.newInstance().create(date);
        month.getCurrentMonth().getDay(0).setShiftEnabled(true);
        month.getCurrentMonth().getDay(1).setShiftEnabled(true);
        month.getCurrentMonth().getDay(2).setShiftEnabled(true);

        final int currentMonthPosition = mAdapter.getCurrentMonthPosition();

        mAdapter.addItemAtPosition(currentMonthPosition, month);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MonthActivity.this, "Changing current month shifts", Toast.LENGTH_SHORT).show();
        // super.onBackPressed();
        performTestUpdate(new Date(System.currentTimeMillis()));
    }

    private void init() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rvMainActivityMonthList);

        Date currentMonth = new Date(System.currentTimeMillis());
        initMonthListForDate(currentMonth);

        mLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        // TODO: Consider passing {@link RecyclerView} and adding listener inside of adapter
        mAdapter = new MonthListAdapter(mMonthList, MonthActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

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

        // Scroll to current month if such month exists within currently set data list
        final int currentMonthPosition = mAdapter.getCurrentMonthPosition();
        if (RecyclerView.NO_POSITION != currentMonthPosition){
            mRecyclerView.scrollToPosition(currentMonthPosition);
        }
    }

    // {@link CalendarCallbacks} region begin

    @Override
    public void onCalendarItemClick(@NonNull Day day, int position) {

        Calendar calendar = CalendarUtils.getCalendarFrom(day.getDate());
        Toast.makeText(getApplicationContext(), "click "
                + CalendarUtils.getHumanFriendlyCalendarRepresentation(calendar)
                + ", position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarItemLongClick(@NonNull Day day, int position) {

        Calendar calendar = CalendarUtils.getCalendarFrom(day.getDate());
        Toast.makeText(getApplicationContext(), "long click "
                + CalendarUtils.getHumanFriendlyCalendarRepresentation(calendar)
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

        if (mMonthList == null){
            mMonthList = new ArrayList<>();
        }

        if (!mMonthList.isEmpty()){
            mMonthList.clear();
        }


        List<Date> monthDateList = CalendarUtils.generateInitialMonthList(date);
        for (Date each : monthDateList){
            mMonthList.add(CalendarDataFactory.newInstance().create(each));
        }
        // mMonthList.addAll(CalendarUtils.generateInitialMonthList(date));
    }

    private void initWeekDayNames(){

        mWeekDayWidget = (WeekDayWidget) findViewById(R.id.wdwMainActivityWeekNameList);
        WeekDayBuilder builder = new WeekDayBuilder(R.layout.weekday_header_layout);
        mWeekDayWidget.set(new WeekDayDataFactory().create());
    }

    // --------------------------------------------------------------------------------------------
    protected static class LoadMoreRunnable implements Runnable {

        private WeakReference<MonthActivity> mMonthActivity;
        private Direction mScrollDirection;

        protected LoadMoreRunnable(@NonNull MonthActivity activity,
                                   @NonNull final Direction scrollDirection){

            mMonthActivity = new WeakReference<>(activity);
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
                        int listCount = activity.mMonthList.size();
                        int newListCount = listCount + VISIBLE_THRESHOLD;

                        for (int i=listCount; i < newListCount; i++){
                            // Get current last item
                            final MixedVisibleMonth lastItemDate = activity.mMonthList.get(i-1);
                            // activity.mAdapter.addItemAtTheEnd(CalendarUtils.getNextMonth(lastItemDate));
                            activity.mAdapter.addItemAtTheEnd(CalendarDataFactory.newInstance().create(CalendarUtils.getNextMonth(lastItemDate.getCurrentMonth().getDay(0).getDate())));
                        }
                        break;

                    case UP: // Load past dates

                        // Remove loading item
                        activity.mAdapter.removeFirstItem();

                        // Load more
                        for (int i=0; i < VISIBLE_THRESHOLD; i++){
                            // Get current first item
                            final MixedVisibleMonth firstItemDate = activity.mMonthList.get(0);
                            // activity.mAdapter.addItemAtBeginning(CalendarUtils.getPreviousMonth(firstItemDate));
                            activity.mAdapter.addItemAtBeginning(CalendarDataFactory.newInstance().create(CalendarUtils.getPreviousMonth(firstItemDate.getCurrentMonth().getDay(0).getDate())));
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

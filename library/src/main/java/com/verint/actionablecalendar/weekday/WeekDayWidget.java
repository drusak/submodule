package com.verint.actionablecalendar.weekday;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.verint.mylibrary.R;

import java.util.List;

/**
 * Custom view that represents week day names according to used by device locale
 *
 * Created by acheshihin on 8/10/2016.
 */
public class WeekDayWidget extends LinearLayout {

    private LinearLayout mWeekDayHeader;

    public WeekDayWidget(Context context) {
        super(context);
        init();
    }

    public WeekDayWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekDayWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeekDayWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void set(@NonNull List<String> weekDayNameList){
        // Assign generated week day names to related view
        setWeekDayNames(weekDayNameList);
    }

    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.week_day_widget, this, true);
        mWeekDayHeader = (LinearLayout) view.findViewById(R.id.llWeekdayWidget);
    }

    private void setWeekDayNames(@NonNull List<String> weekDayNameList){

        final int weekDayViewChildCount = mWeekDayHeader.getChildCount();

        if (weekDayNameList.size() > weekDayViewChildCount){
            throw new IllegalArgumentException("Provided week day list size is bigger than view count");
        }

        for (int i=0; i < mWeekDayHeader.getChildCount(); i++){
            View view = mWeekDayHeader.getChildAt(i);
            if (view instanceof TextView){
                ((TextView) view).setText(weekDayNameList.get(i));
            }
        }
    }
}

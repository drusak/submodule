package com.verint.actionablecalendar.weekday;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by acheshihin on 8/10/2016.
 */
public class WeekDayDataFactory {

    private static final int DAYS_PER_WEEK = 7;
    private static final String DATE_FORMAT = "EEE";

    /**
     * Creates and populates list of week day names for {@link WeekDayWidget}
     *
     * @return List of {@link String} with week day names
     */
    @NonNull
    public List<String> create(){

        // Prepare empty list
        final List<String> weekDayNameList = new ArrayList<>(DAYS_PER_WEEK);

        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final int firstDayOfWeek = calendar.getFirstDayOfWeek();

        // Find first day of the week value for currently used locale
        while(calendar.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek){
            calendar.add(Calendar.DATE, -1);
        }

        // Prepare formatter for date
        final SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        // Proceed from first day until end of the week and add them to week day names list
        for (int i=0; i < DAYS_PER_WEEK; i++){

            weekDayNameList.add(simpleDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        return weekDayNameList;
    }
}

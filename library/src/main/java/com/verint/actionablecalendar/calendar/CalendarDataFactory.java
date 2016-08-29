package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Generates data (days) that should be shown within Month grid view for single month
 * (including last days before month and first days of next month if there is space in rows)
 *
 * Created by acheshihin on 8/4/2016.
 */
public class CalendarDataFactory {

    private static final int DAYS_PER_WEEK = 7;

    private CalendarDataFactory(){
        // TODO: Consider adding here locale initialization
    }

    public static CalendarDataFactory newInstance(){
        return new CalendarDataFactory();
    }

    /**
     * Creates and populates {@link MixedVisibleMonth} object by days required to represent
     * data related to provided {@link Date} month data
     *
     * @param monthDate {@link Date}
     * @return {@link MixedVisibleMonth} populated by day for representation of date
     */
    public MixedVisibleMonth create(@NonNull Date monthDate){

        Calendar calendar = CalendarUtils.getCalendarFrom(monthDate);

        // Rewind to beginning of the month in order to calculate days for month representation
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Generate data for month days representation
        final List<Day> previousMonth = calculatePreviousMonth(calendar);
        final List<Day> currentMonth = calculateCurrentMonth(calendar);
        final List<Day> nextMonth = calculateNextMonth(previousMonth, currentMonth);


        return new MixedVisibleMonth(new VisibleMonth(previousMonth),
                new VisibleMonth(currentMonth), new VisibleMonth(nextMonth));
    }

    @NonNull
    private List<Day> calculateNextMonth(@NonNull final List<Day> previousMonthDayArray,
                                         @NonNull final List<Day> currentMonthDayArray) {

        List<Day> nextMonthDayArray = new ArrayList<>(DAYS_PER_WEEK);
        final int currentMonthDayCount = currentMonthDayArray.size();
        final int previousMonthDayCount = previousMonthDayArray.size();


        Date firstDayOfNextMonth = CalendarUtils
                .getTomorrow(currentMonthDayArray.get(currentMonthDayCount-1).getDate());

        Calendar calendar = CalendarUtils.getCalendarFrom(firstDayOfNextMonth);

        // Calculate amount of rows required to represent data according to amount of month days
        final int rows = ((previousMonthDayCount + currentMonthDayCount) > (5 * DAYS_PER_WEEK)) ? 6 : 5;
        final int totalDaysInMixedView = rows * DAYS_PER_WEEK;

        final int nextMonthDayCount
                = totalDaysInMixedView - previousMonthDayCount - currentMonthDayCount;

        for (int i=0; i< nextMonthDayCount; i++) {
            nextMonthDayArray.add(new Day(calendar.getTime(), new DayState(DayState.DayType.NON_CURRENT_MONTH_DAY)));
            calendar.add(Calendar.DATE, 1);
        }

        return nextMonthDayArray;
    }

    // TODO: Implement here week end day processing according to utilized Locale
    private static boolean isWeekEndDay(@NonNull final Calendar calendar){
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    @NonNull
    private List<Day> calculateCurrentMonth(final Calendar calendar) {
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return calculateCurrentMonth(actualMaximum, calendar.getTime());
    }

    @NonNull
    private List<Day> calculateCurrentMonth(int actualMaximum, Date date) {

        Calendar calendar = CalendarUtils.getCalendarFrom(date);

        List<Day> currentMonthDayArray = new ArrayList<>(actualMaximum);

        for (int i=0; i < actualMaximum; i++) {
            currentMonthDayArray.add(new Day(calendar.getTime(), new DayState(isWeekEndDay(calendar)
                    ? DayState.DayType.CURRENT_MONTH_DAY_WEEKEND : DayState.DayType.CURRENT_MONTH_DAY_NORMAL)));
            calendar.add(Calendar.DATE, 1);
        }

        return currentMonthDayArray;
    }

    @NonNull
    private List<Day> calculatePreviousMonth(final Calendar calendar){

        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        int firstDay = calendar.get(Calendar.DAY_OF_WEEK);
        return calculatePreviousMonth(calendar.getTime(), firstDayOfWeek, firstDay);
    }

    @NonNull
    private List<Day> calculatePreviousMonth(final Date firstDayOfCurrentMonth,
                                             final int firstDayOfWeek,
                                             final int firstDay) {

        List<Day> lastMonthDayArray = new ArrayList<>(DAYS_PER_WEEK);
        Date lastDayOfLastMonth = CalendarUtils.getYesterday(firstDayOfCurrentMonth);
        Calendar calendar = CalendarUtils.getCalendarFrom(lastDayOfLastMonth);

        if (firstDay != firstDayOfWeek) {
            int diff = firstDay > firstDayOfWeek ? firstDay - firstDayOfWeek
                    : DAYS_PER_WEEK - (firstDayOfWeek - firstDay);
            for (int i=diff-1; i>=0; i--) {
                calendar.add(Calendar.DATE, -i);
                lastMonthDayArray.add(new Day(calendar.getTime(), new DayState(DayState.DayType.NON_CURRENT_MONTH_DAY)));
                calendar.setTime(lastDayOfLastMonth);
            }
        }
        return lastMonthDayArray;
    }
}

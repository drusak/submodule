package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.verint.actionablecalendar.calendar.models.EventIndicator;

import java.util.Calendar;
import java.util.Date;

/**
 * Data for single day item which will be represented as grid item of {@link CalendarWidget}
 * FYI:
 * http://tadtech.blogspot.co.il/2007/03/performance-clone-vs-new.html
 * Created by acheshihin on 8/4/2016.
 */
public class Day {

    private Date mDate;
    private DayState mDayState;
    private Calendar mCalendar;

    // TODO: Reimplement to production data when will be ready
    private boolean mShiftEnabled;

    // view for time off icon and badge
    private EventIndicator mTimeOffItem;
    // view for auction with bids icon and badge
    private EventIndicator mAuctionWithBidItem;
    // view for auction without bids icon and badge
    private EventIndicator mAuctionNoBidItem;
    // view for swap request
    private EventIndicator mSwapRequest;
    // view for my swap post
    private EventIndicator mMySwapPost;
    // view for rest users swap post
    private EventIndicator mRestUsersSwapPost;
    // view for my + rest users swap posts
    private EventIndicator mGeneralSwapPost;
    // view for more (three dots)
    private EventIndicator mMore;

    public Day(final Date date, @NonNull DayState dayState){

        if (date == null){
            throw new IllegalArgumentException("Provided argument can't be null");
        }

        mDate = date;
        mDayState = dayState;
        mCalendar = CalendarUtils.getCalendarFrom(date);
    }

    public Calendar getCalendar(){
        return mCalendar;
    }

    /**
     * Returns {@link Date} related to current day
     *
     * @return {@link Date}
     */
    public Date getDate(){
        return mDate;
    }

    public int getMonthDay(){
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns {@link DayState} object related to current {@link Day} object
     *
     * @return {@link DayState}
     */
    public DayState getDayState(){
        return mDayState;
    }

    /**
     * Checks if current day instance is actually today and returns result accordingly, true
     * if today, false otherwise
     *
     * @return true|false
     */
    public boolean isToday(){
        if (mDate == null){
            throw new IllegalStateException("Day was not initialized");
        }
        return DateUtils.isToday(mDate.getTime());
    }

    public boolean isShiftEnabled(){
        return mShiftEnabled;
    }

    public void setShiftEnabled(boolean shiftEnabled){
        mShiftEnabled = shiftEnabled;
    }

    public EventIndicator getTimeOffItem() {
        return mTimeOffItem;
    }

    public void setTimeOffItem(EventIndicator timeOffItem) {
        mTimeOffItem = timeOffItem;
    }

    public EventIndicator getAuctionWithBidItem() {
        return mAuctionWithBidItem;
    }

    public void setAuctionWithBidItem(EventIndicator auctionWithBidItem) {
        mAuctionWithBidItem = auctionWithBidItem;
    }

    public EventIndicator getAuctionNoBidItem() {
        return mAuctionNoBidItem;
    }

    public void setAuctionNoBidItem(EventIndicator auctionNoBidItem) {
        mAuctionNoBidItem = auctionNoBidItem;
    }

    public EventIndicator getMySwapPost() {
        return mMySwapPost;
    }

    public void setMySwapPost(EventIndicator mySwapPost) {
        mMySwapPost = mySwapPost;
    }

    public EventIndicator getSwapRequest() {
        return mSwapRequest;
    }

    public void setSwapRequest(EventIndicator swapRequest) {
        mSwapRequest = swapRequest;
    }

    public EventIndicator getMore() {
        return mMore;
    }

    public void setMore(EventIndicator more) {
        mMore = more;
    }

    public EventIndicator getRestUsersSwapPost() {
        return mRestUsersSwapPost;
    }

    public void setRestUsersSwapPost(EventIndicator restUsersSwapPost) {
        mRestUsersSwapPost = restUsersSwapPost;
    }

    public EventIndicator getGeneralSwapPost() {
        return mGeneralSwapPost;
    }

    public void setGeneralSwapPost(EventIndicator generalSwapPost) {
        mGeneralSwapPost = generalSwapPost;
    }
}

package com.verint.actionablecalendar.calendar;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.verint.actionablecalendar.calendar.models.AuctionBid;
import com.verint.actionablecalendar.calendar.models.Bid;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

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
    private AuctionBid mTimeOffItem;
    // view for auction with bids icon and badge
    private AuctionBid mAuctionWithBidItem;
    // view for auction without bids icon and badge
    private AuctionBid mAuctionNoBidItem;

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

    public AuctionBid getTimeOffItem() {
        return mTimeOffItem;
    }

    public void setTimeOffItem(AuctionBid timeOffItem) {
        mTimeOffItem = timeOffItem;
    }

    public AuctionBid getAuctionWithBidItem() {
        return mAuctionWithBidItem;
    }

    public void setAuctionWithBidItem(AuctionBid auctionWithBidItem) {
        mAuctionWithBidItem = auctionWithBidItem;
    }

    public AuctionBid getAuctionNoBidItem() {
        return mAuctionNoBidItem;
    }

    public void setAuctionNoBidItem(AuctionBid auctionNoBidItem) {
        mAuctionNoBidItem = auctionNoBidItem;
    }
}

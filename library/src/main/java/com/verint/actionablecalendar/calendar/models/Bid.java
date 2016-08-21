package com.verint.actionablecalendar.calendar.models;

import android.support.annotation.DrawableRes;

/**
 * TODO: Add to JavaDoc
 *
 * Created by acheshihin on 8/15/2016.
 */
public class Bid {

    private int mBidImage;

    public Bid(@DrawableRes final int bidImage) {
        mBidImage = bidImage;
    }

    @DrawableRes
    public int getBidImage() {
        return mBidImage;
    }

    public void setBidImage(@DrawableRes int bidImage) {
        mBidImage = bidImage;
    }
}

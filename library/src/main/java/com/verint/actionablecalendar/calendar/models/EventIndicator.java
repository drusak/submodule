package com.verint.actionablecalendar.calendar.models;

import android.support.annotation.DrawableRes;

/**
 * Contains Drawable resources for Icon and Badge of Indicator (e.g. for Calendar)
 *
 * Created by acheshihin on 8/11/2016.
 */
public class EventIndicator {

    private int mAuctionImage;
    private int mBadgeImage;

    public EventIndicator(@DrawableRes final int auctionImage, @DrawableRes final int badgeImage) {
        this.mAuctionImage = auctionImage;
        this.mBadgeImage = badgeImage;
    }

    @DrawableRes
    public int getAuctionImage() {
        return mAuctionImage;
    }

    public void setAuctionImage(@DrawableRes final int auctionImage) {
        mAuctionImage = auctionImage;
    }

    @DrawableRes
    public int getBadgeImage() {
        return mBadgeImage;
    }

    public void setBadgeImage(@DrawableRes final int badgeImage) {
        mBadgeImage = badgeImage;
    }
}

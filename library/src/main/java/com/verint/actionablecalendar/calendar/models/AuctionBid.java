package com.verint.actionablecalendar.calendar.models;

import android.support.annotation.DrawableRes;

/**
 * TODO: Add JavaDoc
 *
 * Created by acheshihin on 8/11/2016.
 */
public class AuctionBid {

    private int mAuctionImage;
    private int mBadgeImage;

    public AuctionBid(@DrawableRes final int auctionImage, @DrawableRes final int badgeImage) {
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

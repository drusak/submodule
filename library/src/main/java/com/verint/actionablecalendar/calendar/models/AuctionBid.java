package com.verint.actionablecalendar.calendar.models;

/**
 * TODO: Add JavaDoc
 *
 * Created by acheshihin on 8/11/2016.
 */
public class AuctionBid {

    private String mAuctionImage;
    private String mBadgeImage;

    public AuctionBid(String auctionImage, String badgeImage) {
        this.mAuctionImage = auctionImage;
        this.mBadgeImage = badgeImage;
    }

    public String getAuctionImage() {
        return mAuctionImage;
    }

    public void setAuctionImage(String auctionImage) {
        mAuctionImage = auctionImage;
    }

    public String getBadgeImage() {
        return mBadgeImage;
    }

    public void setBadgeImage(String badgeImage) {
        mBadgeImage = badgeImage;
    }
}

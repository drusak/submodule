package com.verint.actionablecalendar.calendar.models;

import android.support.annotation.DrawableRes;

/**
 * Contains Drawable resources for Icon and Badge of Indicator (e.g. for Calendar)
 *
 * Created by acheshihin on 8/11/2016.
 */
public class EventIndicator {

    private int mIconImage;
    private int mBadgeImage;

    public EventIndicator(@DrawableRes final int iconImage, @DrawableRes final int badgeImage) {
        this.mIconImage = iconImage;
        this.mBadgeImage = badgeImage;
    }

    @DrawableRes
    public int getIconImage() {
        return mIconImage;
    }

    public void setIconImage(@DrawableRes final int iconImage) {
        mIconImage = iconImage;
    }

    @DrawableRes
    public int getBadgeImage() {
        return mBadgeImage;
    }

    public void setBadgeImage(@DrawableRes final int badgeImage) {
        mBadgeImage = badgeImage;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        EventIndicator that = (EventIndicator) obj;
        return mIconImage == that.mIconImage && mBadgeImage == that.mBadgeImage;
    }

    @Override
    public int hashCode() {
        int result = mIconImage;
        result = 31 * result + mBadgeImage;
        return result;
    }
}

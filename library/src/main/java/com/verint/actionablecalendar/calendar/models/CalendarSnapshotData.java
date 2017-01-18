package com.verint.actionablecalendar.calendar.models;

/**
 * Contains snapshot of presented on calendar data: event indicators,
 * icons of first level, amount of icons of second level and amount of cells with potentially
 * third icon which is not show according to current logic but can be shown if we change it
 *
 * Created by acheshihin on 1/1/2017.
 */
public class CalendarSnapshotData {

    private int mIndicatorCount;
    private int mSingleIconCellCount;
    private int mTwoIconCellCount;
    private int mPotentiallyExtraIconCellCount;

    public CalendarSnapshotData(int indicatorCount,
                                int singleIconCellCount,
                                int twoIconCellCount,
                                int potentiallyExtraIconCellCount) {

        mIndicatorCount = indicatorCount;
        mSingleIconCellCount = singleIconCellCount;
        mTwoIconCellCount = twoIconCellCount;
        mPotentiallyExtraIconCellCount = potentiallyExtraIconCellCount;
    }

    public CalendarSnapshotData(){
        this(-1, -1, -1, -1);
    }

    public int getIndicatorCount() {
        return mIndicatorCount;
    }

    public void setIndicatorCount(int indicatorCount) {
        mIndicatorCount = indicatorCount;
    }

    public int getSingleIconCellCount() {
        return mSingleIconCellCount;
    }

    public void setSingleIconCellCount(int singleIconCellCount) {
        mSingleIconCellCount = singleIconCellCount;
    }

    public int getTwoIconCellCount() {
        return mTwoIconCellCount;
    }

    public void setTwoIconCellCount(int twoIconCellCount) {
        mTwoIconCellCount = twoIconCellCount;
    }

    public int getPotentiallyExtraIconCellCount() {
        return mPotentiallyExtraIconCellCount;
    }

    public void setPotentiallyExtraIconCellCount(int potentiallyExtraIconCellCount) {
        mPotentiallyExtraIconCellCount = potentiallyExtraIconCellCount;
    }
}

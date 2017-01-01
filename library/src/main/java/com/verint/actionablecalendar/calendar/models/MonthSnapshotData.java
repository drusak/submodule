package com.verint.actionablecalendar.calendar.models;

/**
 * Represents snapshot of single month (what items are represented on it): event indicators,
 * icons of first level, amount of icons of second level
 *
 * Created by acheshihin on 1/1/2017.
 */
public class MonthSnapshotData {

    private int mIndicatorCount;
    private int mSingleIconCellCount;
    private int mMultipleIconCellCount;

    public MonthSnapshotData(int indicatorCount,
                             int singleIconCellCount,
                             int multipleIconCellCount) {

        mIndicatorCount = indicatorCount;
        mSingleIconCellCount = singleIconCellCount;
        mMultipleIconCellCount = multipleIconCellCount;
    }

    public MonthSnapshotData(){
        this(-1, -1, -1);
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

    public int getMultipleIconCellCount() {
        return mMultipleIconCellCount;
    }

    public void setMultipleIconCellCount(int multipleIconCellCount) {
        mMultipleIconCellCount = multipleIconCellCount;
    }
}

package com.verint.library.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * TODO: Add JavaDoc
 *
 * Created by acheshihin on 8/14/2016.
 */
public abstract class OnListScrollDirectionalListener extends RecyclerView.OnScrollListener {

    private int mRecentFirstVisibleItemPosition = RecyclerView.NO_POSITION;

    private LinearLayoutManager mLayoutManager;
    private boolean mVerticallyOriented;

    public OnListScrollDirectionalListener(@NonNull RecyclerView recyclerView) {

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager && layoutManager.canScrollVertically()) {

            mLayoutManager = (LinearLayoutManager) layoutManager;

        } else {

            throw new IllegalStateException("Please provide "
                    + LinearLayoutManager.class.getSimpleName()
                    + " with vertical orientation layout manager to utilized recycler view");
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // Get current visible item position
        int currentFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

        if (currentFirstVisibleItemPosition > mRecentFirstVisibleItemPosition){
            onScrolledDown(recyclerView, dx, dy);
        } else { // Load data for past
            onScrolledUp(recyclerView, dx, dy);
        }
        mRecentFirstVisibleItemPosition = currentFirstVisibleItemPosition;
    }

    public abstract void onScrolledUp(RecyclerView recyclerView, int dx, int dy);
    public abstract void onScrolledDown(RecyclerView recyclerView, int dx, int dy);
}

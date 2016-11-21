package com.verint.actionablecalendar.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.verint.mylibrary.R;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add JavaDoc
 *
 * Created by acheshihin on 8/11/2016.
 */
public class AuctionBidView extends FrameLayout {
    // todo: use weak references
    private static final Map<Integer, Drawable> sCachedDrawablesByResId = new HashMap<>();

    private View mRootView;
    private ImageView mEventImage;
    private ImageView mEventBadge;

    public AuctionBidView(Context context) {
        super(context);
        init(context, null);
    }

    public AuctionBidView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){

        mRootView = inflate(context, R.layout.action_bid_view_layout, this);
        mEventImage = (ImageView) mRootView.findViewById(R.id.ivEventViewImage);
        mEventBadge = (ImageView) mRootView.findViewById(R.id.ivEventViewBadge);

        // Check if badge should be visible
        final boolean showBadge;
        final Drawable imageDrawable;
        final Drawable badgeDrawable;
        int badgeWidth = -1;
        int badgeHeight = -1;

        if (attrs != null){

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AuctionBidView, 0, 0);

            try{
                showBadge = typedArray.getBoolean(R.styleable.AuctionBidView_showBadge, false);
                imageDrawable = typedArray.getDrawable(R.styleable.AuctionBidView_imageSrc);
                badgeDrawable = typedArray.getDrawable(R.styleable.AuctionBidView_badgeSrc);
                badgeWidth = typedArray.getDimensionPixelSize(R.styleable.AuctionBidView_badgeWidth, -1);
                badgeHeight = typedArray.getDimensionPixelSize(R.styleable.AuctionBidView_badgeHeight, -1);

            } finally {
                typedArray.recycle();
            }

        } else {

            showBadge = false;
            imageDrawable = null;
            badgeDrawable = null;
        }

        if (imageDrawable != null){
            mEventImage.setImageDrawable(imageDrawable);
        }

        if (badgeDrawable != null){
            mEventBadge.setImageDrawable(badgeDrawable);
        }

        // Adjust badge visibility
        mEventBadge.setVisibility(showBadge ? View.VISIBLE : View.INVISIBLE);
        if (showBadge) {
            if (badgeWidth > 0 && badgeHeight > 0) {
                mEventBadge.getLayoutParams().width = badgeWidth;
                mEventBadge.getLayoutParams().height = badgeHeight;
            }
        }


    }

    public void setImage(@DrawableRes int imageId){
        mEventImage.setImageDrawable(getOrCreateCachedDrawable(imageId));
    }

    public void setBadge(@DrawableRes int badgeId){
        mEventBadge.setImageDrawable(getOrCreateCachedDrawable(badgeId));
    }

    private Drawable getOrCreateCachedDrawable(@DrawableRes int drawableResId) {
        if (drawableResId == 0) {
            return null;
        }
        Drawable cachedDrawable = sCachedDrawablesByResId.get(drawableResId);
        if (cachedDrawable == null) {
            cachedDrawable = getResources().getDrawable(drawableResId);
            sCachedDrawablesByResId.put(drawableResId, cachedDrawable);
        }
        return cachedDrawable;
    }


}

package com.verint.actionablecalendar.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.verint.mylibrary.R;

/**
 * Created by acheshihin on 8/11/2016.
 */
public class ActionBidView extends FrameLayout {

    private View mRootView;
    private ImageView mEventImage;
    private ImageView mEventBadge;

    public ActionBidView(Context context) {
        super(context);
        init(context, null);
    }

    public ActionBidView(Context context, AttributeSet attrs) {
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

        if (attrs != null){

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ActionBidView, 0, 0);

            try{
                showBadge = typedArray.getBoolean(R.styleable.ActionBidView_showBadge, false);
                imageDrawable = typedArray.getDrawable(R.styleable.ActionBidView_imageSrc);
                badgeDrawable = typedArray.getDrawable(R.styleable.ActionBidView_badgeSrc);

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
    }

    public void setImage(int imageId){
        mEventImage.setImageResource(imageId);
    }

    public void setBadge(int badgeId){
        mEventBadge.setImageResource(badgeId);
    }
}

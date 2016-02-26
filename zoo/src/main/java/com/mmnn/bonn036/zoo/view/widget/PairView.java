package com.mmnn.bonn036.zoo.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

public abstract class PairView extends LinearLayout {
    private static final String TAG = "PairView";

    private View mFrontView;
    private View mBackView;

    private int mGap;

    public PairView(Context context) {
        super(context);
        setupViews();
    }

    public PairView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    private void setupViews() {
        mGap = getDefaultGap();
        Log.i(TAG, "orientation: " + getOrientation() + ", mGap" + mGap);

        mFrontView = getFrontView();
        mFrontView.setLayoutParams(getFrontPLayoutParams());
        addView(mFrontView);

        mBackView = getBackView();
        mBackView.setLayoutParams(getBackLayoutParams());
        addView(mBackView);
    }

    protected LayoutParams getFrontPLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (HORIZONTAL == getOrientation()) {
            params.gravity = Gravity.CENTER_VERTICAL;
        } else {
            params.gravity = Gravity.CENTER_HORIZONTAL;
        }
        return params;
    }

    protected LayoutParams getBackLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (HORIZONTAL == getOrientation()) {
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = mGap;
        } else {
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = mGap;
        }
        return params;
    }

    public void setGap(int gap) {
        Log.i(TAG, "gap: " + gap + ", mGap: " + mGap);
        if (mGap != gap) {
            mGap = gap;
            mBackView.setLayoutParams(getBackLayoutParams());
            requestLayout();
        }
    }

    protected int getDefaultGap() {
        return 0;
    }

    protected abstract View getFrontView();

    protected abstract View getBackView();

}

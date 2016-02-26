package com.mmnn.bonn036.zoo.view.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ProgressBar;

import com.mmnn.bonn036.zoo.R;


public class LoadingView extends TextBackPairView {
    public static final String TAG = "LoadingView";

    private ProgressBar mProgressBar;

    private int mTextColor;
    private float mTextSize;
    private boolean mBlack = false;

    public LoadingView(Context context) {
        super(context);
        initData();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        mTextColor = getTextColor();
        mTextSize = getResources().getDimensionPixelSize(R.dimen.margin_40);
    }

    public void setInverse(boolean black) {
        if (mBlack == black) {
            return;
        }
        mBlack = black;
        mTextColor = getTextColor();
        removeAllViews();
        getFrontView().setLayoutParams(getFrontPLayoutParams());
        addView(getFrontView());
        getBackView().setLayoutParams(getBackLayoutParams());
        addView(getBackView());
    }

    public void show(String loadText) {
        setBackText(loadText);
        setBackTextSize(mTextSize);
        setBackTextColor(mTextColor);
        setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0f, 1.0f);
        startAnimation(animation);
    }

    public void hide(boolean noAnimation) {
        if (View.VISIBLE == getVisibility()) {
            if (noAnimation) {
                setVisibility(View.GONE);
                return;
            }
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
            animation.setDuration(500);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(View.GONE);
                }
            });
            startAnimation(animation);
        }
    }

    @Override
    protected int getDefaultGap() {
        return getResources().getDimensionPixelSize(R.dimen.margin_20);
    }

    @Override
    protected View getFrontView() {
        if (mProgressBar == null) {
            mProgressBar = getProgressBar();
        }
        return mProgressBar;
    }

    private ProgressBar getProgressBar() {
        final ProgressBar progressBar;
        if (mBlack) {
            progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmallInverse);
        } else {
            progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        }
        progressBar.setIndeterminate(true);
        return progressBar;
    }

    private int getTextColor() {
        final int color;
        if (mBlack) {
            color = getResources().getColor(R.color.black_50_percent);
        } else {
            color = getResources().getColor(R.color.white_50_percent);
        }
        return color;
    }

}

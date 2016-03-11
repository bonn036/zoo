package com.mmnn.bonn036.zoo.view.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class Sea extends FrameLayout {

    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinimumFlingVelocity;
    private int mMaxVelocity;
    private float mInitialMotionY;
    private float mInitialMotionX;
    private float mLastMotionY;
    private float mLastMotionX;
    private int mPointerId;

    public Sea(Context context) {
        super(context);
        init(context);
    }

    public Sea(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Sea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMaxVelocity = config.getScaledMaximumFlingVelocity();
        mMinimumFlingVelocity = config.getScaledMinimumFlingVelocity();
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if(null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if(null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float y = event.getY(), x = event.getX();
                final float deltaY, deltaX, absDeltaY;
                deltaY = y - mLastMotionY;
                deltaX = x - mLastMotionX;
                absDeltaY = Math.abs(deltaY);
                if (absDeltaY > mTouchSlop && absDeltaY > Math.abs(deltaX)) {
                    mLastMotionY = y;
                    mLastMotionX = x;
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                mPointerId =event.getPointerId(0);
                mInitialMotionY = mLastMotionY = event.getY();
                mInitialMotionX = mLastMotionX = event.getX();
                break;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                VelocityTracker verTracker = mVelocityTracker;
                verTracker.computeCurrentVelocity(1000, mMaxVelocity);
                final float velocityY = verTracker.getYVelocity(mPointerId);
                if (Math.abs(velocityY) > mMinimumFlingVelocity) {
                }
                releaseVelocityTracker();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                break;
            }
        }
        return super.onTouchEvent(event);
    }

}

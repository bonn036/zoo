package com.mmnn.zoo.view.pulltozoom;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mmnn.zoo.R;


public abstract class PullToZoomBase<T extends View> extends LinearLayout implements IPullToZoom<T> {
    private static final float FRICTION = 2.0f;
    protected T mRootView;
    protected View mHeaderView;//头部View
    protected View mZoomView;//缩放拉伸View

    protected int mScreenHeight;
    protected int mScreenWidth;

    private boolean isZoomEnabled = true;
    private boolean isParallax = true;
    private boolean isZooming = true;
    private boolean isHideHeader = false;

    private int mTouchSlop;
    private boolean mIsBeingDragged = false;
    private float mLastMotionY;
    private float mLastMotionX;
    private float mInitialMotionY;
    private float mInitialMotionX;
    private float mZoomInitialMotionY;
    private float mZoomInitialMotionX;
    private OnPullZoomListener onPullZoomListener;
    private OnScrollListener onScrollListener;
    private VelocityTracker mVelocityTracker;
    private int mMinimumFlingVelocity;
    private int mMaxVelocity;
    private int mPointerId;

    public PullToZoomBase(Context context) {
        this(context, null);
    }

    public PullToZoomBase(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);

        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMaxVelocity = config.getScaledMaximumFlingVelocity();
        mMinimumFlingVelocity = 300;//config.getScaledMinimumFlingVelocity();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        mScreenHeight = localDisplayMetrics.heightPixels;
        mScreenWidth = localDisplayMetrics.widthPixels;

        // Refreshable View
        // By passing the attrs, we can add ListView/GridView params via XML
        mRootView = createRootView(context, attrs);

        if (attrs != null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());
            //初始化状态View
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PullToZoomView);

            int zoomViewResId = a.getResourceId(R.styleable.PullToZoomView_zoomView, 0);
            if (zoomViewResId > 0) {
                mZoomView = mLayoutInflater.inflate(zoomViewResId, null, false);
            }

            int headerViewResId = a.getResourceId(R.styleable.PullToZoomView_headerView, 0);
            if (headerViewResId > 0) {
                mHeaderView = mLayoutInflater.inflate(headerViewResId, null, false);
            }

            isParallax = a.getBoolean(R.styleable.PullToZoomView_isHeaderParallax, true);

            // Let the derivative classes have a go at handling attributes, then
            // recycle them...
            handleStyledAttributes(a);
            a.recycle();
        }
        addView(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setOnPullZoomListener(OnPullZoomListener onPullZoomListener) {
        this.onPullZoomListener = onPullZoomListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public T getPullRootView() {
        return mRootView;
    }

    @Override
    public View getZoomView() {
        return mZoomView;
    }

    public abstract void setZoomView(View zoomView);

    public View getContentView() {
        return null;
    }

    @Override
    public View getHeaderView() {
        return mHeaderView;
    }

    public abstract void setHeaderView(View headerView);

    @Override
    public boolean isPullToZoomEnabled() {
        return isZoomEnabled;
    }

    @Override
    public boolean isZooming() {
        return isZooming;
    }

    @Override
    public boolean isParallax() {
        return isParallax;
    }

    public void setParallax(boolean isParallax) {
        this.isParallax = isParallax;
    }

    @Override
    public boolean isHideHeader() {
        return isHideHeader;
    }

    public void setHideHeader(boolean isHideHeader) {//header显示才能Zoom
        this.isHideHeader = isHideHeader;
    }

    public void setZoomEnabled(boolean isZoomEnabled) {
        this.isZoomEnabled = isZoomEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isHideHeader()) {
            return false;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            isZooming = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final float y = event.getY(), x = event.getX();
                final float deltaY, deltaX, absDeltaY;
                deltaY = y - mLastMotionY;

                if (isReadyForPullStart() || (deltaY > 0 && getContentView() != null && !canScroll(getContentView(), true, deltaY, x, y))) {
                    // We need to use the correct values, based on scroll
                    // direction
                    deltaX = x - mLastMotionX;
                    absDeltaY = Math.abs(deltaY);
                    if (absDeltaY > mTouchSlop && absDeltaY > Math.abs(deltaX)) {
                        mLastMotionY = y;
                        mLastMotionX = x;
                        mIsBeingDragged = true;
                    }
                }
                if (deltaY != 0 && onScrollListener != null) {
                    onScrollListener.onScroll(deltaY);
                    if (deltaY < 0 && !canScroll(getContentView(), true, deltaY, x, y)) {
                        onScrollListener.onScrollToBottom();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                mPointerId =event.getPointerId(0);
                mInitialMotionY = mLastMotionY = mZoomInitialMotionY = event.getY();
                mInitialMotionX = mLastMotionX = mZoomInitialMotionX = event.getX();
                if (isReadyForPullStart()) {
                    mIsBeingDragged = false;
                }
                break;
            }
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        acquireVelocityTracker(event);
        if (isHideHeader()) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE: {
                if (mIsBeingDragged) {
                    moveDistance(event.getY() - mLastMotionY);
                    mLastMotionY = event.getY();
                    mLastMotionX = event.getX();
                    if (isReadyForZoomPullStart() && isPullToZoomEnabled()) {
                        pullEvent();
                        isZooming = true;
                    } else {
                        mZoomInitialMotionY = mLastMotionY;
                        mZoomInitialMotionX = mLastMotionX;
                    }
                    return true;
                }else{
                    final float y = event.getY(), x = event.getX();
                    final float diff, oppositeDiff, absDiff;
                    diff = y - mLastMotionY;

                    if (isReadyForPullStart() || (diff > 0 && getContentView() != null && !canScroll(getContentView(), true, diff, x, y))) {
                        // We need to use the correct values, based on scroll
                        // direction
                        oppositeDiff = x - mLastMotionX;
                        absDiff = Math.abs(diff);
                        if (absDiff > mTouchSlop && absDiff > Math.abs(oppositeDiff)) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;
                        }
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                mPointerId =event.getPointerId(0);
                mInitialMotionY = mLastMotionY = mZoomInitialMotionY = event.getY();
                mInitialMotionX = mLastMotionX = mZoomInitialMotionX = event.getX();
                if (isReadyForPullStart()) {
                    mIsBeingDragged = true;
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    VelocityTracker verTracker = mVelocityTracker;
                    verTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    final float velocityY = verTracker.getYVelocity(mPointerId);
                    if (Math.abs(velocityY) > mMinimumFlingVelocity) {
                        fastExpands(velocityY > 0 && (event.getY() - mInitialMotionY) > 0);
                    }
                    releaseVelocityTracker();
                    if (isZooming()) {
                        smoothScrollToTop();
                        if (onPullZoomListener != null) {
                            onPullZoomListener.onPullZoomEnd();
                        }
                        isZooming = false;
                        return true;
                    }
                    return true;
                }
                isZooming = false;
                releaseVelocityTracker();
                break;
            }
        }
        return true;
    }

    private void pullEvent() {
        final int newScrollValue;
        final float initialMotionValue, lastMotionValue;

        initialMotionValue = mZoomInitialMotionY;
        lastMotionValue = mLastMotionY;

        newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0) / FRICTION);

        pullHeaderToZoom(newScrollValue);
        if (onPullZoomListener != null) {
            onPullZoomListener.onPullZooming(newScrollValue);
        }
    }

    protected boolean canScroll(View v, boolean checkV, float deltaY, float x, float y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x >= child.getLeft() - scrollX && x < child.getRight() - scrollX
                        && y >= child.getTop() - scrollY && y < child.getBottom() - scrollY
                        && canScroll(child, true, deltaY, x + scrollX - child.getLeft(), y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && v.canScrollVertically((int) -deltaY);
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

    protected abstract void fastExpands(boolean isExpand);

    protected abstract void moveDistance(float dy);

    protected abstract void pullHeaderToZoom(int newScrollValue);

    protected abstract T createRootView(Context context, AttributeSet attrs);

    protected abstract void smoothScrollToTop();

    protected abstract boolean isReadyForPullStart();

    protected abstract boolean isReadyForZoomPullStart();

    public interface OnPullZoomListener {
        void onPullZooming(int newScrollValue);

        void onPullZoomEnd();
    }

    public interface OnScrollListener {
        void onScroll(float deltaY);

        void onScrollToBottom();
    }
}

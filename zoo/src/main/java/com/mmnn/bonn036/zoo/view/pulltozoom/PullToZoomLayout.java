package com.mmnn.bonn036.zoo.view.pulltozoom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mmnn.bonn036.zoo.R;


public class PullToZoomLayout extends PullToZoomBase<FrameLayout> {
    private FrameLayout mHeaderContainer;
    private int mHeaderHeight;
    private ScalingRunnable mScalingRunnable;
    private LinearLayout mRootContainer;
    private View mContentView;
    private int mDragMarginTop;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };

    public PullToZoomLayout(Context context) {
        this(context, null);
    }

    public PullToZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScalingRunnable = new ScalingRunnable();
        mHeaderHeight = 0;
    }

    /**
     * 是否显示headerView
     *
     * @param isHideHeader true: show false: hide
     */
    @Override
    public void setHideHeader(boolean isHideHeader) {
        if (isHideHeader != isHideHeader()) {
            super.setHideHeader(isHideHeader);
            if (isHideHeader) {
                removeHeaderView();
            } else {
                updateHeaderView();
            }
        }
    }

    @Override
    protected void fastExpands(boolean isExpand) {
        ValueAnimator valueAnimator = null;
        if (isExpand) {
            if(mContentView.getTranslationY() != 0){
                valueAnimator = ObjectAnimator.ofFloat(mContentView, "translationY", mContentView.getTranslationY(), 0);
            }
        } else {
            if(mContentView.getTranslationY() !=  -(mHeaderHeight - mDragMarginTop)){
                valueAnimator = ObjectAnimator.ofFloat(mContentView, "translationY", mContentView.getTranslationY(), -(mHeaderHeight - mDragMarginTop));
            }
        }
        if(valueAnimator != null){
            valueAnimator.setDuration(200);
            valueAnimator.start();
        }
    }

    @Override
    protected void moveDistance(float dy) {
        float f = mContentView.getTranslationY() + dy;
        if (dy > 0) {
            if (f >= 0) {
                mContentView.setTranslationY(0);
            } else {
                mContentView.setTranslationY(f);
            }
        } else {
            if (Math.abs(f) >= (mHeaderHeight - mDragMarginTop)) {
                mContentView.setTranslationY(-(mHeaderHeight - mDragMarginTop));
            } else {
                mContentView.setTranslationY(f);
            }
        }
    }

    private void removeHeaderView() {
        mHeaderContainer.removeAllViews();
        mHeaderHeight = 0;
    }

    @Override
    public void setHeaderView(View headerView) {
        if (headerView != null) {
            this.mHeaderView = headerView;
            updateHeaderView();
        }
    }

    @Override
    public void setZoomView(View zoomView) {
        if (zoomView != null) {
            this.mZoomView = zoomView;
            updateHeaderView();
        }
    }

    /**
     * 更新HeaderView  先移除-->再添加zoomView、HeaderView -->然后添加到listView的head
     * 如果要兼容API 9,需要修改此处逻辑，API 11以下不支持动态添加header
     */
    private void updateHeaderView() {
        if (mHeaderContainer != null) {
            mHeaderContainer.removeAllViews();
            if (mZoomView != null) {
                mHeaderContainer.addView(mZoomView);
            }
            if (mHeaderView != null) {
                mHeaderContainer.addView(mHeaderView);
            }
            mHeaderHeight = mHeaderContainer.getHeight();
        }
    }

    /**
     * 创建listView 如果要兼容API9,需要修改此处
     *
     * @param context 上下文
     * @param attrs   AttributeSet
     * @return FrameLayout
     */
    @Override
    protected FrameLayout createRootView(Context context, AttributeSet attrs) {
        FrameLayout rv = new FrameLayout(context, attrs);
        rv.setId(android.R.id.list);
        return rv;
    }

    /**
     * 重置动画，自动滑动到顶部
     */
    @Override
    protected void smoothScrollToTop() {
        mScalingRunnable.startAnimation(200L);
    }

    /**
     * zoomView动画逻辑
     *
     * @param newScrollValue 手指Y轴移动距离值
     */
    @Override
    protected void pullHeaderToZoom(int newScrollValue) {
        if (mContentView.getTranslationY() != 0) {
            return;
        }
        if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
            mScalingRunnable.abortAnimation();
        }

        ViewGroup.LayoutParams localLayoutParams = mHeaderContainer.getLayoutParams();
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight;
        mHeaderContainer.setLayoutParams(localLayoutParams);
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    @Override
    protected boolean isReadyForZoomPullStart() {
        return mContentView.getTranslationY() == 0;
    }

    private boolean isFirstItemVisible() {
        return mContentView.getTranslationY() != -(mHeaderHeight - mDragMarginTop);
    }

    @Override
    public void handleStyledAttributes(TypedArray a) {
        mRootContainer = new LinearLayout(getContext());
        mRootContainer.setOrientation(LinearLayout.VERTICAL);
        mHeaderContainer = new FrameLayout(getContext());
        if (mZoomView != null) {
            mHeaderContainer.addView(mZoomView);
        }
        if (mHeaderView != null) {
            mHeaderContainer.addView(mHeaderView);
        }
        mRootContainer.addView(mHeaderContainer);

        int headViewHeight = a.getDimensionPixelSize(R.styleable.PullToZoomView_headerHeight, 0);
        mDragMarginTop = a.getDimensionPixelSize(R.styleable.PullToZoomView_dragMarginTop, 0);
        int contentViewResId = a.getResourceId(R.styleable.PullToZoomView_contentView, 0);
        if (contentViewResId > 0) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());
            mContentView = mLayoutInflater.inflate(contentViewResId, null, false);
        }

        if (mContentView != null) {
            mRootContainer.addView(mContentView);
        }
        mRootContainer.setClipChildren(false);
        mHeaderContainer.setClipChildren(false);

        mRootView.addView(mRootContainer);
        if (headViewHeight > 0) {
            setHeaderViewSize(mScreenWidth, headViewHeight);
        }
    }

    /**
     * 设置HeaderView高度
     *
     * @param width  宽
     * @param height 高
     */
    public void setHeaderViewSize(int width, int height) {
        if (mHeaderContainer != null) {
            Object localObject = mHeaderContainer.getLayoutParams();
            if (localObject == null) {
                localObject = new ViewGroup.LayoutParams(width, height);
            }
            ((ViewGroup.LayoutParams) localObject).width = width;
            ((ViewGroup.LayoutParams) localObject).height = height;
            mHeaderContainer.setLayoutParams((ViewGroup.LayoutParams) localObject);
            mHeaderHeight = height;
        }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (mHeaderHeight == 0 && mHeaderContainer != null) {
            mHeaderHeight = mHeaderContainer.getHeight();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (heightSize > 0 && mContentView != null) {
            mContentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize - mDragMarginTop, MeasureSpec.EXACTLY));
        }
    }

    public View getContentView() {
        return mContentView;
    }

    class ScalingRunnable implements Runnable {
        protected long mDuration;
        protected boolean mIsFinished = true;
        protected float mScale;
        protected long mStartTime;

        ScalingRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public void run() {
            if (mZoomView != null) {
                float f2;
                ViewGroup.LayoutParams localLayoutParams;
                if ((!mIsFinished) && (mScale > 1.0D)) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime) / (float) mDuration;
                    f2 = mScale - (mScale - 1.0F) * PullToZoomLayout.sInterpolator.getInterpolation(f1);
                    localLayoutParams = mHeaderContainer.getLayoutParams();
                    if (f2 > 1.0F) {
                        localLayoutParams.height = ((int) (f2 * mHeaderHeight));
                        mHeaderContainer.setLayoutParams(localLayoutParams);
                        post(this);
                        return;
                    }
                    mIsFinished = true;
                }
            }
        }

        public void startAnimation(long paramLong) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis();
                mDuration = paramLong;
                mScale = ((float) (mHeaderContainer.getBottom()) / mHeaderHeight);
                mIsFinished = false;
                post(this);
            }
        }
    }
}

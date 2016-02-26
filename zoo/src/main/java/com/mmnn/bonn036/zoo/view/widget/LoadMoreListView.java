package com.mmnn.bonn036.zoo.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class LoadMoreListView extends PullDownRefreshListView implements OnScrollListener {

    private final String TAG = LoadMoreListView.class.getCanonicalName();

    private View loadMoreView;
    private boolean loadingMoreCurPhaseFinished = true;
    private boolean canLoadMore = false;

    private LinearLayout mFootParentView = null;
    private LinearLayout footerContainer;
    private View footer;
    private int footerHeight = 0;

    private OnTouchInterceptor onTouchInterceptor;
    private OnScrollListener onScrollListener;
    private OnLoadMoreListener onLoadMoreListener;

    private OnDispatchTouchEventListener mOnDispatchTouchEventListener;
    private OnCanLoadMoreListener mCanLoadMoreListener;

    private float lastMotionX;
    private float lastMotionY;

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreListView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.setWillNotDraw(false);
        // setOnScrollListener to base class.
        super.setOnScrollListener(this);

        footerContainer = new LinearLayout(getContext());
        footer = new TextView(getContext());
        LinearLayout.LayoutParams footerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, footerHeight, Gravity.CENTER_VERTICAL);
        footerContainer.addView(footer, footerParams);
        super.addFooterView(footerContainer, null, false);
    }

    public void setOnDispatchTouchEventListener(OnDispatchTouchEventListener listener) {
        mOnDispatchTouchEventListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (mOnDispatchTouchEventListener != null) {
            mOnDispatchTouchEventListener.OnDispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);

    }

    public void setViewContentBottomPadding(int padding) {
        if (padding < 0 || padding == footerHeight) {
            return;
        }
        footerHeight = padding;
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) footer.getLayoutParams();
        lParams.height = footerHeight;
        footer.setLayoutParams(lParams);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getAdapter() == null || getAdapter().isEmpty()) {
            hideView(loadMoreView);
        } else {
            if (canLoadMore) {
                showView(loadMoreView);
            } else {
                hideView(loadMoreView);
            }
        }
    }

    private void showView(View view) {
        if (view != null) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideView(View view) {
        if (view != null && view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    //private FrameLayout loadMoreFrame = null;
    public void setLoadMoreView(View loadMoreView) {
        if (this.loadMoreView != null) {
            getLoadMoreParentView().removeAllViews();
        }
        if (loadMoreView != null) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lParams.gravity = Gravity.CENTER_HORIZONTAL;
            loadMoreView.setLayoutParams(lParams);
            getLoadMoreParentView().addView(loadMoreView);
            this.loadMoreView = loadMoreView;
        }
    }

    private LinearLayout getLoadMoreParentView() {
        if (mFootParentView == null) {
            mFootParentView = new LinearLayout(getContext());
            LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            mFootParentView.setLayoutParams(lParams);
            mFootParentView.setOrientation(LinearLayout.VERTICAL);
//			int bottom = getContext().getResources().getDimensionPixelSize(R.dimen.margin_180);
//			mFootParentView.setPadding(0, 0, 0, bottom);
            super.addFooterView(mFootParentView, null, false);
        }
        return mFootParentView;
    }

    @Override
    public void addFooterView(View v, Object data, boolean isSelectable) {
        if (mFootParentView != null) {
            removeFooterView(mFootParentView);
            super.addFooterView(v, data, isSelectable);
            super.addFooterView(mFootParentView, null, false);
        } else {
            super.addFooterView(v, data, isSelectable);
        }
    }

    public boolean isCanLoadMore() {
        return this.canLoadMore;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
        if (mCanLoadMoreListener != null) {
            mCanLoadMoreListener.onCanLoadMoreChanged(canLoadMore);
        }
    }

    public void setOnCanLoadMoreListener(OnCanLoadMoreListener listener) {
        mCanLoadMoreListener = listener;
    }

    public boolean isLoadMorePhaseFinished() {
        return loadingMoreCurPhaseFinished;
    }

    public void setLoadMorePhaseFinished(boolean curPhaseFinished) {
        this.loadingMoreCurPhaseFinished = curPhaseFinished;
    }

    public boolean isTopmost() {
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            return Math.abs(view.getTop() - getListPaddingTop()) <= 1
                    && getFirstVisiblePosition() == 0;
        }
        return true;
    }

    public boolean isBottommost() {
        boolean canScrollDown;
        int count = getChildCount();
        // The last item is not visible.
        canScrollDown = (getFirstVisiblePosition() + count) < getCount();
        if (!canScrollDown && count > 0) {
            // The last item is visible and the last item's bottom is below
            // list' bottom.
            View child = getChildAt(count - 1);
            canScrollDown = child.getBottom() + getTop() > getBottom()
                    - getPaddingBottom();
        }
        return !canScrollDown;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnTouchInterceptor(OnTouchInterceptor onTouchInterceptor) {
        this.onTouchInterceptor = onTouchInterceptor;
    }

    private boolean isLoadMoreViewShown() {
        if (mFootParentView != null && getAdapter() != null
                && !getAdapter().isEmpty()) {
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) == mFootParentView) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onTouchInterceptor != null) {
            boolean isIntercepted = false;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastMotionX = ev.getRawX();
                    lastMotionY = ev.getRawY();
                    onTouchInterceptor.onPreIntercept(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = ev.getRawX();
                    float y = ev.getRawY();
                    if (Math.pow(Math.abs(x - lastMotionX), 2)
                            + Math.pow(Math.abs(y - lastMotionY), 2) < 2) {
                        return false;
                    }
                    if (lastMotionX != Integer.MIN_VALUE
                            && lastMotionY != Integer.MIN_VALUE) {
                        double angel = Math.atan(Math.abs(y - lastMotionY)
                                / Math.abs(x - lastMotionX));
                        if (angel > -Math.PI / 4 && angel < Math.PI / 4) {
                            // less than 45 degree, the direction is horizontal
                            // scroll.
                            isIntercepted = onTouchInterceptor
                                    .onIntercept(
                                            x < lastMotionX ? OnTouchInterceptor.SCROLL_LEFT
                                                    : OnTouchInterceptor.SCROLL_RIGHT,
                                            ev);
                        } else {
                            isIntercepted = onTouchInterceptor.onIntercept(
                                    y < lastMotionY ? OnTouchInterceptor.SCROLL_UP
                                            : OnTouchInterceptor.SCROLL_DOWN, ev);
                        }
                    }
                    lastMotionX = x;
                    lastMotionY = y;
                    if (isIntercepted) {
                        return false;
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        this.onScrollListener = l;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (canLoadMore && loadingMoreCurPhaseFinished) {
                if (isLoadMoreViewShown()) {
                    loadingMoreCurPhaseFinished = !(onLoadMoreListener != null
                            && onLoadMoreListener.onLoadMore(this));
                }
            }
        }
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus && direction == View.FOCUS_FORWARD && previouslyFocusedRect == null) {
            Log.d(TAG, "onFocusChanged: do not relayout");
        } else {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
    }

    public interface OnDispatchTouchEventListener {
        void OnDispatchTouchEvent(MotionEvent ev);
    }

    public interface OnCanLoadMoreListener {
        void onCanLoadMoreChanged(boolean canloadmore);
    }

    public interface OnLoadMoreListener {
        /**
         * @return return true means you want to do refreshing.
         */
        boolean onLoadMore(ListView listView);
    }

}

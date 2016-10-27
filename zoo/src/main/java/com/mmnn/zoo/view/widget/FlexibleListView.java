package com.mmnn.zoo.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmnn.zoo.R;


public class FlexibleListView extends RelativeLayout {

    private static final int MINI_TRIGGER_SCALE_DISTANCE = 10;
    private static final float SCALE_FACTOR = 0.00008f;
    //移动触发的最小距离
    private static final int MINI_TRIGGER_MOVE_DISTANCE = 30;
    // 移动因子, 是一个百分比, 比如手指移动了100px, 那么View就只移动50px
    // 目的是达到一个延迟的效果
    private static final float MOVE_FACTOR = 0.5f;
    protected View mCustomResultView;
    //
    protected LoadMoreListView mListView;
    protected TextView mLoadingResulView;
    protected TextView mLoadingMoreResultView;
    protected int mBottomMargin = -1;
    private float lastY;
    private float startY;
    private int scaleTrigger = -1;
    // 在手指滑动的过程中记录是否移动了布局
    private int scaleYType = 0;
    private int originTop;
    private int moveTrigger = -1;
    // 在手指滑动的过程中记录是否移动了布局
    private boolean isMoved = false;
    private View mLoadingMoreView;
    private LoadMoreListView.OnDispatchTouchEventListener mDispatchTouchListener = new LoadMoreListView.OnDispatchTouchEventListener() {

        @Override
        public void OnDispatchTouchEvent(MotionEvent ev) {

//			Log.d(TAG, "OnDispatchTouchEvent mScrollView.getScrollY: " + mScrollView.getScrollY() );
            int action = ev.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // 记录按下时的Y值
                    startY = ev.getY();
                    lastY = startY;
                    break;

                case MotionEvent.ACTION_UP:

                    if (scaleYType != 0) {
                        float curScaleY = mListView.getScaleY();
                        mListView.setScaleY(1.0f);
                        float pivotYValue = 0;
                        if (scaleYType < 0) {
                            pivotYValue = 1f;
                        }
                        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, curScaleY, 1f,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, pivotYValue);
                        animation.setDuration(300);//设置动画持续时间
                        mListView.startAnimation(animation);


                        // 将标志位设回false
                        scaleYType = 0;
                    } else if (isMoved && isNoDataViewShow()) {
                        int currentTop = mCustomResultView.getTop() - originTop;

                        // 设置回到正常的布局位置
                        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        params.addRule(CENTER_HORIZONTAL);
                        params.topMargin = originTop;
                        mCustomResultView.setLayoutParams(params);

                        // 开启动画
                        TranslateAnimation anim = new TranslateAnimation(0, 0,
                                currentTop, 0);
                        anim.setDuration(300);
                        mCustomResultView.startAnimation(anim);


                        isMoved = false;

                    }

                    break;
                case MotionEvent.ACTION_MOVE:

                    if (isNoDataViewShow()) {
                        float nowY = ev.getY();
                        int deltaY = (int) (nowY - startY);

                        boolean shouldMove = deltaY > moveTrigger;//下拉
                        if (shouldMove) {
                            // 计算偏移量
                            int offset = (int) (deltaY * MOVE_FACTOR);

                            // 随着手指的移动而移动布局
                            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            params.addRule(CENTER_HORIZONTAL);
                            params.topMargin = originTop + offset;
                            mCustomResultView.setLayoutParams(params);

                            isMoved = true; // 记录移动了布局
                        }

                    } else {
//					if (!isListViewBottom()) {
//						startY = ev.getY();
//						break;
//					}
//					// 计算手指移动的距离
//					float nowY = ev.getY();
//					int deltaY = (int) (nowY - startY);
//					// 是否应该移动布局
//					boolean shouldMove = (isListViewBottom()) && (deltaY < -scaleTrigger);//在底部并且上拉
//
//					if (shouldMove) {
//						Log.d(TAG, "OnDispatchTouchEvent should move");
//						// 计算偏移量
//						float scaleY = 1 - deltaY * SCALE_FACTOR;
//						mListView.setPivotY(mListView.getHeight());
//						mListView.setScaleY(scaleY);
//						isScaleY = true; // 记录移动了布局
//					}
                        // 在移动的过程中， 既没有滚动到可以上拉的程度， 也没有滚动到可以下拉的程度
                        float curY = ev.getY();
                        if ((curY > lastY && !isListViewTop()) ||
                                (curY < lastY && !isListViewBottom())) {
                            startY = curY;
                        }
                        if (startY != curY) {
                            float scaleY = 1;
                            int deltaY = (int) (curY - startY);
                            if (deltaY < scaleTrigger) {
                                mListView.setPivotY(mListView.getHeight());
                                scaleY = 1 - deltaY * SCALE_FACTOR;
                                scaleYType = -1;
                            } else if (deltaY > -scaleTrigger) {
                                if (!canPullDown()) {
                                    mListView.setPivotY(0);
                                    scaleY = 1 + deltaY * SCALE_FACTOR;
                                    scaleYType = 1;
                                }
                            }
                            if (scaleY != 1) {
                                mListView.setScaleY(scaleY);
                            }
                        }
                        lastY = curY;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public FlexibleListView(Context context) {
        super(context);
        init();
    }

    public FlexibleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlexibleListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void addHeaderView(View headerView) {
        mListView.addHeaderView(headerView);
    }

    public void setCanPullDown(boolean value) {
        mListView.setCanPullDown(value);
    }

    public boolean canPullDown() {
        return mListView.canPullDown();
    }

    public void setRefreshListener(PullDownRefreshListView.OnRefreshListener listener) {
        mListView.setRefreshListener(listener);
    }

    public void setCanLoadMore(boolean canLoadMore) {
        mListView.setCanLoadMore(canLoadMore);
    }

    public void setOnLoadMoreListener(LoadMoreListView.OnLoadMoreListener listener) {
        mListView.setOnLoadMoreListener(listener);
    }

    public void postRefresh() {
        if (mListView.isRefreshing()) {
            mListView.postRefresh();
        }
    }

    public ListView getListView() {
        return mListView;
    }

    private void init() {
        createView();
        scaleTrigger = MINI_TRIGGER_SCALE_DISTANCE;
//		mListView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mListView.setOnDispatchTouchEventListener(mDispatchTouchListener);
        originTop = getContext().getResources().getDimensionPixelSize(R.dimen.margin_100);
        moveTrigger = MINI_TRIGGER_MOVE_DISTANCE;
    }

    private void createView() {
        mListView = new LoadMoreListView(getContext());
        mListView.setId(View.NO_ID);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setLoadMorePhaseFinished(true);
        mListView.setLoadMoreView(getLoadMoreView());
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (mLoadingMoreResultView != null) {
                        mListView.setLoadMoreView(getLoadMoreView());
                        mLoadingMoreResultView = null;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mListView.setOnCanLoadMoreListener(new LoadMoreListView.OnCanLoadMoreListener() {

            @Override
            public void onCanLoadMoreChanged(boolean canloadmore) {
                if (mBottomMargin >= 0) {
                    if (canloadmore) {
                        mListView.setViewContentBottomPadding(0);
                    } else {
                        mListView.setViewContentBottomPadding(mBottomMargin);
                    }
                }
            }
        });
        LayoutParams listParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mListView, listParams);

        mLoadingResulView = new TextView(getContext());
        mLoadingResulView.setTextAppearance(getContext(), R.style.on_loadmore_textstyle);
        mLoadingResulView.setVisibility(View.INVISIBLE);
        int top_margin = (int) getResources().getDimension(R.dimen.margin_75);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = top_margin;
        params.addRule(CENTER_HORIZONTAL);
        addView(mLoadingResulView, params);
    }

    public View getLoadMoreView() {
        if (mLoadingMoreView == null) {
            LoadingView loadMoreView = new LoadingView(getContext());
            loadMoreView.setBackText(R.string.get_hint);
            loadMoreView.setInverse(true);
            loadMoreView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            mLoadingMoreView = loadMoreView;
        }
        return mLoadingMoreView;
    }

    public boolean isListViewTop() {
        return mListView.getFirstVisiblePosition() <= 0;
    }

    public boolean isListViewBottom() {
        return (mListView.getLastVisiblePosition() == mListView.getCount() - 1);
    }

    public boolean isNoDataViewShow() {
        return mCustomResultView != null && mCustomResultView.getVisibility() == View.VISIBLE;
    }
}

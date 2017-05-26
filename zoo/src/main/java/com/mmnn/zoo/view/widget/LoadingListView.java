package com.mmnn.zoo.view.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmnn.zoo.R;


public class LoadingListView extends RelativeLayout {

    private static final String TAG = LoadingListView.class.getCanonicalName();

    protected LoadMoreListView mListView;
    protected LoadingBaseView mLoadingView;
    protected TextView mLoadingResultView;
    protected TextView mLoadingMoreResultView;
    protected View mCustomResultView;
    protected int mBottomMargin = -1;
    protected boolean isLoading = false;
    private View mLoadingMoreView;

    public LoadingListView(Context context) {
        super(context);

        createView();
    }

    public LoadingListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        createView();
    }

    public LoadingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        createView();
    }

    public ListView getListView() {
        return mListView;
    }

    private void createView() {

        mListView = new LoadMoreListView(getContext());
        mListView.setId(View.generateViewId());
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setLoadMoreView(getLoadMoreView());
        mListView.setLoadMorePhaseFinished(true);
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (mLoadingMoreResultView != null) {
                        mListView.setLoadMoreView(getLoadMoreView());
                        mLoadingMoreResultView = null;
                        Log.d(TAG, "set loadmore");
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

        mLoadingResultView = new TextView(getContext());
        mLoadingResultView.setTextAppearance(getContext(), R.style.textstyle_40_b80);
        mLoadingResultView.setVisibility(View.INVISIBLE);
        int top_margin = (int) getResources().getDimension(R.dimen.margin_75);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = top_margin;
        params.addRule(CENTER_HORIZONTAL);
        addView(mLoadingResultView, params);
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void addHeaderView(View headerView) {
        mListView.addHeaderView(headerView);
    }

    public void setDivider(Drawable divider) {
        mListView.setDivider(divider);
        mListView.setDividerHeight(1);
    }

    public void setSelector(int selectorId) {
        mListView.setSelector(selectorId);
    }

    public void setCanPullDown(boolean value) {
        mListView.setCanPullDown(value);
    }

    public boolean canPullDown() {
        return mListView.canPullDown();
    }

    public void setContentBottomPadding(int bottom) {
        if (bottom >= 0) {
            mBottomMargin = bottom;
            mListView.setViewContentBottomPadding(bottom);
        }
    }


    public void showLoadMoreFailed(String text) {
        if (mLoadingMoreResultView == null) {
            mLoadingMoreResultView = getFailedResultView(text);

            mLoadingMoreResultView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            mListView.setLoadMoreView(mLoadingMoreResultView);
        } else {
            mLoadingMoreResultView.setText(text);
        }
    }

    public void showLoadMoreFailed() {
        showLoadMoreFailed(getResources().getString(R.string.get_result_failed));
    }

    public void setLoadingView(LoadingBaseView view, LayoutParams lParams) {
        if (view != null) {
            if (mLoadingView == null) {
                removeView(mLoadingView);
            }
            mLoadingView = view;
            //	mLoadingView.setVisibility(GONE);
            if (lParams != null) {
                mLoadingView.setLayoutParams(lParams);
            } else if (view.getLayoutParams() == null) {
                LayoutParams loadParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                loadParams.addRule(CENTER_IN_PARENT);
                mLoadingView.setLayoutParams(loadParams);
            }
            addView(mLoadingView);
        }
    }

    public void setLoadingView(LoadingBaseView view) {
        setLoadingView(view, null);
    }

    public void showLoading() {
        if (!isLoading && mLoadingView != null) {
            mLoadingView.showLoading();
            isLoading = true;
        }
    }

    public void hideLoading() {
        if (isLoading && mLoadingView != null) {
            mLoadingView.hideLoading();
            isLoading = false;
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void showResultText(int resId) {
        showResultText(getResources().getString(resId));
    }

    public void showResultText(String text) {
        mLoadingResultView.setText(text);
        mLoadingResultView.setVisibility(View.VISIBLE);
        if (mCustomResultView != null) {
            mCustomResultView.setVisibility(INVISIBLE);
        }
    }

    public void hideResultText() {
        mLoadingResultView.setVisibility(View.INVISIBLE);
    }

    public void setResultView(View view) {
        if (view != null) {
            mCustomResultView = view;
            if (view.getLayoutParams() == null) {
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(CENTER_IN_PARENT);
                addView(view, params);
            } else {
                addView(mCustomResultView);
            }
            mCustomResultView.setVisibility(INVISIBLE);
        }
    }

    public void showResultView() {
        if (mCustomResultView != null) {
            mCustomResultView.setVisibility(VISIBLE);
        }
    }

    public void hideResultView() {
        if (mCustomResultView != null) {
            mCustomResultView.setVisibility(INVISIBLE);
        }
    }

    public boolean isCanLoadMore() {
        return mListView.isCanLoadMore();
    }

    public void setCanLoadMore(boolean canLoadMore) {
        mListView.setCanLoadMore(canLoadMore);
    }

    public void setOnLoadMoreListener(LoadMoreListView.OnLoadMoreListener listener) {
        mListView.setOnLoadMoreListener(listener);
    }

    public TextView getFailedResultView(int resId) {
        return getFailedResultView(getResources().getString(resId));
    }

    public TextView getFailedResultView(String text) {
        TextView resultView = new TextView(getContext());
        resultView.setTextAppearance(getContext(), R.style.app_list_small_textstyle);
        resultView.setText(text);
        int padding = (int) getResources().getDimension(R.dimen.margin_70);
        resultView.setPadding(0, padding, 0, padding);
        return resultView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
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

    public void setLoadingMoreView(View view) {
        mLoadingMoreView = view;
        if (mLoadingMoreView != null) {
            mLoadingMoreView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            mListView.setLoadMoreView(mLoadingMoreView);
        }
    }

    public void setRefreshListener(PullDownRefreshListView.OnRefreshListener listener) {
        mListView.setRefreshListener(listener);
    }

    public void postRefresh() {
        if (mListView.isRefreshing()) {
            mListView.postRefresh();
        }
    }


}

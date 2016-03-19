package com.mmnn.bonn036.zoo.activity;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mmnn.bonn036.zoo.R;
import com.mmnn.bonn036.zoo.view.widget.BottomLoadingView;


public abstract class LoadingActivity extends Activity {
//	protected final static int MSG_SHOW_BOTTOM_LOADING = 1001;
	protected final static int STATE_HIDE = 0;
	protected final static int STATE_LOADING = 1;
	protected final static int STATE_RETRY = 2;
	protected BottomLoadingView mBottomLoadingView;
	protected ViewGroup mAnchorView;
	protected boolean mIsInitLoading = false;
	protected int mLoadingMarginBottom = -1;
	protected int mState = STATE_HIDE;

//	protected Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (msg.what == MSG_SHOW_BOTTOM_LOADING) {
//				showBottomLoading(msg.arg1);
//			}
//		}
//	};

	protected void setLoadingMargin(int bottomMargin) {
		mLoadingMarginBottom = bottomMargin;
		initView();
	}
	
	private void initView() {
		if (mAnchorView == null) {
			mAnchorView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
			if (mAnchorView != null) {
				mAnchorView = (ViewGroup) mAnchorView.getChildAt(0);
			}
		}
		if (mBottomLoadingView == null) {
			mBottomLoadingView = (BottomLoadingView) View.inflate(this, R.layout.bottom_loading_view, null);
		}
		mAnchorView.removeView(mBottomLoadingView);
		if (mAnchorView instanceof FrameLayout) {

			FrameLayout.LayoutParams lpb = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lpb.gravity = Gravity.BOTTOM;
			mAnchorView.addView(mBottomLoadingView, lpb);

			mIsInitLoading = true;
		} else if (mAnchorView instanceof RelativeLayout) {

			RelativeLayout.LayoutParams lpb = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lpb.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mAnchorView.addView(mBottomLoadingView, lpb);

			mIsInitLoading = true;
		}
		if (mState == STATE_HIDE) {
			hideLoading();
		}
	}

	protected void hideLoading() {
		if (mIsInitLoading) {
			mBottomLoadingView.hide();
			mState = STATE_HIDE;
		}
	}

	protected void showBottomLoading(int textId) {
//		mHandler.removeMessages(MSG_SHOW_BOTTOM_LOADING);
//		Message msg = mHandler.obtainMessage(MSG_SHOW_BOTTOM_LOADING);
//		msg.arg1 = textId;
//		mHandler.sendMessageDelayed(msg, 200);
		if (!mIsInitLoading) {
			initView();
		}
		if (mIsInitLoading) {
			if (mBottomLoadingView.getVisibility() == View.VISIBLE) {
				return;
			}
			mBottomLoadingView.showLoading(textId);
			mBottomLoadingView.bringToFront();
			mState = STATE_LOADING;
		}
	}

	protected void hideBottomLoading() {
//		mHandler.removeMessages(MSG_SHOW_BOTTOM_LOADING);
		if (mBottomLoadingView.getVisibility() == View.GONE) {
			return;
		}
		mBottomLoadingView.hide();
	}

	protected boolean isBottomLoadingVisible() {
		return mBottomLoadingView != null && mBottomLoadingView.getVisibility() == View.VISIBLE;
	}
}

package com.mmnn.zoo.view.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmnn.zoo.R;


public class BottomLoadingView extends FrameLayout {

    protected View mContentGroup;
    protected ImageView mImageView;
    protected TextView mTextView;

    public BottomLoadingView(Context context) {
        super(context);
    }

    public BottomLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomLoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentGroup = findViewById(R.id.content_group);
        mImageView = (ImageView) findViewById(R.id.loading_icon);
        mTextView = (TextView) findViewById(R.id.loading_text);
    }

    public void showLoading(int textId) {
        this.setVisibility(View.VISIBLE);
        mContentGroup.setVisibility(View.INVISIBLE);

        mTextView.setText(textId);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        mImageView.startAnimation(anim);

        TranslateAnimation animPop = new TranslateAnimation(0, 0, mContentGroup.getHeight(), 0);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mContentGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(800);
        mContentGroup.startAnimation(animPop);
    }

    public void hide() {
        if (this.getVisibility() != View.VISIBLE) {
            return;
        }
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, mContentGroup.getHeight());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mImageView.clearAnimation();
                BottomLoadingView.this.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(800);
        mContentGroup.startAnimation(anim);
    }

}

package com.mmnn.zoo.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class LoadingBaseView extends RelativeLayout {

    public LoadingBaseView(Context context) {
        super(context);
    }

    public LoadingBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public abstract void hideLoading();

    public abstract void showLoading();
}

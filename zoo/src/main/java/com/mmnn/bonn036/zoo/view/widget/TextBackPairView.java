package com.mmnn.bonn036.zoo.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public abstract class TextBackPairView extends PairView {

    private TextView mBackTextView;

    public TextBackPairView(Context context) {
        super(context);
    }

    public TextBackPairView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getBackView() {
        if (mBackTextView == null) {
            mBackTextView = new TextView(getContext());
            mBackTextView.setGravity(Gravity.CENTER);
        }
        return mBackTextView;
    }

    public void setBackText(int text) {
        mBackTextView.setText(text);
    }

    public void setBackText(CharSequence text) {
        mBackTextView.setText(text);
    }

    public void setBackTextSize(float size) {
        final float density = getContext().getResources().getDisplayMetrics().scaledDensity;
        mBackTextView.setTextSize(size / density);
    }

    public void setBackTextColor(int color) {
        mBackTextView.setTextColor(color);
    }

}

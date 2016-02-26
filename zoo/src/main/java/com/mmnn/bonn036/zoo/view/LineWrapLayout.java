package com.mmnn.bonn036.zoo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.mmnn.bonn036.zoo.R;


public class LineWrapLayout extends ViewGroup {
    private int mHSpacing = 1;// 子View之间的横向间隔
    private int mVSpacing = 1; // 子View之间的纵向间隔

    public LineWrapLayout(Context context) {
        this(context, null);
    }

    public LineWrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineWrapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineWrapLayout, defStyle, 0);
        // 得到横向间隔
        mHSpacing = a.getDimensionPixelSize(R.styleable.LineWrapLayout_horizontal_spacing, 15);
        // 得到纵向间隔
        mVSpacing = a.getDimensionPixelSize(R.styleable.LineWrapLayout_vertical_spacing, 15);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int paddingLeft = getPaddingLeft();
        int xpos = paddingLeft;
        int height = 0;

        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            final int childw = child.getMeasuredWidth();

            if (i == 0) {
                height += child.getMeasuredHeight();
            }
            if (xpos + childw + mHSpacing > width) {
                xpos = paddingLeft;
                height += mVSpacing + child.getMeasuredHeight();
            } else {
                xpos += childw + mHSpacing;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return (p instanceof LayoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            final int childw = child.getMeasuredWidth();
            final int childh = child.getMeasuredHeight();
            if (xpos + childw > width) {
                xpos = getPaddingLeft();
                ypos += childh + mVSpacing;
            }
            child.layout(xpos, ypos, xpos + childw, ypos + childh);
            xpos += childw + mHSpacing;
        }
    }
}
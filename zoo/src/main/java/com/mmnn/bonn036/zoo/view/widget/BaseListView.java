package com.mmnn.bonn036.zoo.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class BaseListView extends ListView {
    public BaseListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public BaseListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseListView(final Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (final ArrayIndexOutOfBoundsException e) {
            // ignore;
            return false;
        } catch (final IndexOutOfBoundsException e) {
            // ignore;
            return false;
        }
    }
}

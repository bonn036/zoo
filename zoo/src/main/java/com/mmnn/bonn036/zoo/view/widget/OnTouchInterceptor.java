package com.mmnn.bonn036.zoo.view.widget;

import android.view.MotionEvent;

public interface OnTouchInterceptor {

    int SCROLL_LEFT = 0;
    int SCROLL_RIGHT = 1;
    int SCROLL_UP = 2;
    int SCROLL_DOWN = 3;

    boolean onIntercept(int scrollDirection, MotionEvent event);

    void onPreIntercept(MotionEvent event);
}

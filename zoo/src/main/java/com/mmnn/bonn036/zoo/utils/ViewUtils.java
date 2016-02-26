package com.mmnn.bonn036.zoo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class ViewUtils {

    /**
     * if text is null or empty,the view will be gone.
     *
     * @param view
     * @param text
     */
    public static void setText(TextView view, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * set text for text view which its paraent view is container. if text is
     * null or empty,the view will be gone.
     *
     * @param container
     * @param id
     * @param text
     * @see #setText
     */
    public static void setText(ViewGroup container, int id, CharSequence text) {
        TextView textView = (TextView) container.findViewById(id);
        setText(textView, text);
    }

    public static float px2dip(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px / scale;
    }

    public static int dip2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                .getDisplayMetrics());

    }

}

package com.mmnn.zoo.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.mmnn.zoo.R;

/**
 * Created by dz on 2016/8/17.
 */
public class StatView extends RelativeLayout {
    private LineChartView mCharView;

    public StatView(Context context) {
        super(context);
    }

    public StatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCharView = (LineChartView) findViewById(R.id.chart_view);
        String[] xUnits = {"3月1", "2", "3", "4", "5"};
        int[] yUnits = {7, 10, 24, 18, 11};
        setData(xUnits, yUnits);
    }

    public void setData(String[] xUnits, int[] yUnits) {
        mCharView.setData(xUnits, yUnits);
    }

}

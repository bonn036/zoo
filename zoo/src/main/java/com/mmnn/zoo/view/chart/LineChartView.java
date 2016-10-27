package com.mmnn.zoo.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dz on 2016/8/17.
 */
public class LineChartView extends View {
    private Paint paint = new Paint();

    private int xStart = 6, yStart;
    private int width = 240;// X轴长度
    private int height = 320;// Y轴长度
    private int xStep = 5;// X轴一个刻度长度
    private int yStep = 5;// Y轴一个刻度长度

    private String[] xUnits;
    private int[] yUnits, xCords, yCords;

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setData(String[] xUnits, int[] yUnits) {
        this.xUnits = xUnits;
        this.yUnits = yUnits;
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);// 去锯齿
        paint.setColor(0xFFFFFFFF);
        paint.setTextSize(24);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (xUnits == null || xUnits.length == 0) {
            return;
        }
        width = getWidth() - xStart * 4;
        height = getHeight() - 44;
        yStart = height;
        xStep = xUnits.length == 1 ? 0 : width / (xUnits.length - 1);
        yStep = height / 50;
//        canvas.drawRect(0, 0, width, height, paint);

        int x = xStart, y = yStart, count = xUnits.length <= yUnits.length ? xUnits.length : yUnits.length;
        paint.setStrokeWidth(1);
        canvas.drawLine(0, y, getWidth(), y, paint);
        y += 30;
        xCords = new int[xUnits.length];
        yCords = new int[yUnits.length];
        for (int i = 0; i < count; i++) {
            xCords[i] = x;
            yCords[i] = yStart - yUnits[i] * yStep;
            paint.setStrokeWidth(1);
            canvas.drawText(xUnits[i], xCords[i] - xStart, y, paint);
            paint.setStrokeWidth(10);
            canvas.drawPoint(xCords[i], yCords[i], paint);
            if (i > 0) {
                paint.setStrokeWidth(5);
                canvas.drawLine(xCords[i - 1], yCords[i - 1], xCords[i], yCords[i], paint);
//                canvas.drawLine(xCords[i - 1], yCords[i - 1]+1, xCords[i], yCords[i]+1, paint);
            }
            x += xStep;
        }
    }
}

package com.mmnn.zoo.view.tagview;

import java.io.Serializable;

public class Tag implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2684657309332033242L;

    private int backgroundResId;
    private int id;
    private boolean isChecked;
    private int leftDrawableResId;
    private int rightDrawableResId;
    private String title;
    private int mPaddingLeft = -1, mPaddingRight = -1, mPaddingTop = -1, mPaddingBottom = -1;
    private int mTextSize = -1;

    public Tag() {

    }

    public Tag(int paramInt, String paramString) {
        this.id = paramInt;
        this.title = paramString;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        mPaddingTop = top;
        mPaddingBottom = bottom;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public int getPaddingTop() {
        return mPaddingTop;
    }

    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int size) {
        mTextSize = size;
    }

    public int getBackgroundResId() {
        return this.backgroundResId;
    }

    public void setBackgroundResId(int paramInt) {
        this.backgroundResId = paramInt;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int paramInt) {
        this.id = paramInt;
    }

    public int getLeftDrawableResId() {
        return this.leftDrawableResId;
    }

    public void setLeftDrawableResId(int paramInt) {
        this.leftDrawableResId = paramInt;
    }

    public int getRightDrawableResId() {
        return this.rightDrawableResId;
    }

    public void setRightDrawableResId(int paramInt) {
        this.rightDrawableResId = paramInt;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String paramString) {
        this.title = paramString;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean paramBoolean) {
        this.isChecked = paramBoolean;
    }
}

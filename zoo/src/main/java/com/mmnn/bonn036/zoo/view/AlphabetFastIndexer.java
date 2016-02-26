/*
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */

package com.mmnn.bonn036.zoo.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import com.mmnn.bonn036.zoo.R;

import java.lang.ref.WeakReference;
import java.util.Arrays;


/**
 * AlphabetFastIndexer, attach an alphabet index bar, a mask indicating current position and
 * an overlay showing current item title.
 * FastIndexer is expected to be a child of an FrameLayout, who contains an AbsListView.
 * For example, the layout fragment should as follows:
 *     <FrameLayout
 *       android:layout_width="fill_parent"
 *       android:layout_height="fill_parent"
 *     >
 *         <ListView (Any derive class of ListView)
 *             android:id="@+id/list"
 *             android:layout_width="fill_parent"
 *             android:layout_height="fill_parent"
 *             android:scrollbars="none"
 *         />
 *         <AlphabetFastIndexer
 *             android:id="@+id/fast_indexer"
 *             android:layout_width="wrap_content"
 *             android:layout_height="fill_parent"
 *         />
 *     </FrameLayout>
 *
 */

@SuppressLint("NewApi")
public class AlphabetFastIndexer extends ImageView {

    /**
     * set title of thumb to STARRED_TITLE if FastIndexer has StarView
     */
    public static final String STARRED_TITLE = "!";

    /**
     * FastIndexer is not dragging
     */
    public static final int STATE_NONE = 0;

    /**
     * the state of FastIndexer is dragging
     */
    public static final int STATE_DRAGGING = 1;
    private static final String STARRED_LABEL = "\u2605";
    private static final int MSG_FADE = 1;
    private static final int FADE_DELAYED = 1500;
    private AdapterView<?> mListView;
    private TextView mOverlay;
    private int mLastAlphabetIndex;
    private int mVerticalPosition;
    private int mOverlayLeftMargin;
    private int mOverlayTopMargin;
    private int mOverlayTextSize;
    private int mOverlayTextColor;
    private Drawable mOverlayBackground;
    private TextHilighter mTextHilighter;
    private ValueAnimator.AnimatorUpdateListener mTextHilightAnimListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            mTextHilighter.update(getWidth() / 2F, (Float) animator.getAnimatedValue());
            postInvalidate();
        }
    };
    private int mListScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private int mState = STATE_NONE;
    private Runnable mRefreshMaskRunnable = new Runnable() {

        @Override
        public void run() {
            refreshMask();
        }

    };
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FADE:
                    if (mOverlay != null) {
                        mOverlay.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    public AlphabetFastIndexer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphabetFastIndexer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final Resources res = context.getResources();
        final int style = (attrs.getStyleAttribute() == 0) ? defStyle : attrs.getStyleAttribute();
        final TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.AlphabetFastIndexer, 0, style);

        mTextHilighter = new TextHilighter(context, a);

        mOverlayLeftMargin = a.getDimensionPixelOffset(R.styleable.AlphabetFastIndexer_overlayMarginLeft,
                res.getDimensionPixelOffset(R.dimen.margin_60));
        mOverlayTopMargin = a.getDimensionPixelOffset(R.styleable.AlphabetFastIndexer_overlayMarginTop,
                res.getDimensionPixelOffset(0));
        mOverlayTextSize = a.getDimensionPixelSize(R.styleable.AlphabetFastIndexer_overlayTextSize,
                res.getDimensionPixelSize(R.dimen.text_size_84));
        mOverlayTextColor = a.getColor(R.styleable.AlphabetFastIndexer_overlayTextColor,
                res.getColor(android.R.color.white));
        mOverlayBackground = a.getDrawable(R.styleable.AlphabetFastIndexer_overlayBackground);
        if (mOverlayBackground == null) {
            mOverlayBackground = res.getDrawable(R.drawable.ic_list_az_middle_focus_v5);
        }

        Drawable background = a.getDrawable(R.styleable.AlphabetFastIndexer_indexerBackground);
        if (background == null) {
            background = res.getDrawable(R.drawable.alphabet_indexer_bg);
        }
        setBackground(background);

        a.recycle();
        mVerticalPosition = Gravity.RIGHT;
    }

    /**
     * set FastIndexer positon relative to listview
     *
     * @param isRight, right of listview if true
     */
    public void setVerticalPosition(boolean isRight) {
        mVerticalPosition = isRight ? Gravity.RIGHT : Gravity.LEFT;
    }

    /**
     * set custom overlay's offset of FastIndexer
     *
     * @param leftMargin, topMargin
     */
    public void setOverlayOffset(int leftMargin, int topMargin) {
        mOverlayLeftMargin = leftMargin;
        mOverlayTopMargin = topMargin;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLastAlphabetIndex = -1;
        post(mRefreshMaskRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int top = getPaddingTop();
        int height = getHeight() - top - getPaddingBottom();
        if (height <= 0) {
            return;
        }

        final String[] table = mTextHilighter.mIndexes;
        final float alphaHeight = ((float) height) / table.length;
        final float x = getWidth() / 2f;

        mTextHilighter.beginDraw();


        float y = top + alphaHeight/2;
        for (int i = 0; i < table.length; ++i) {

            mTextHilighter.draw(canvas, isPressed(), i, x, y);

            y += alphaHeight;
        }

        mTextHilighter.endDraw(canvas);
    }

    /**
     * Attach FastIndexer to an ListView
     *
     *
     */
    public void attatch(AdapterView<?> lv) {
        if (mListView == lv) {
            return;
        }

        detach();
        if (lv == null) {
            return;
        }

        mLastAlphabetIndex = -1;

        mListView = lv;
        final Context context = getContext();

        final FrameLayout parent = (FrameLayout) getParent();

        // initialize overlay
        mOverlay = new TextView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        params.leftMargin = mOverlayLeftMargin;
        params.topMargin = mOverlayTopMargin;
        mOverlay.setLayoutParams(params);
        mOverlay.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mOverlay.setBackground(mOverlayBackground);
        mOverlay.setGravity(Gravity.CENTER);
        mOverlay.setTextSize(mOverlayTextSize);
        mOverlay.setTextColor(mOverlayTextColor);
        mOverlay.setVisibility(View.GONE);
        parent.addView(mOverlay);

        // configure indexer
        params = (FrameLayout.LayoutParams)getLayoutParams();
        params.gravity = Gravity.TOP | mVerticalPosition;
        setLayoutParams(params);

        refreshMask();
    }

    /**
     * Detach FastIndexer from an ListView
     */
    public void detach() {
        if (mListView != null) {
            stop(0);
            FrameLayout parent = (FrameLayout) getParent();
            parent.removeView(mOverlay);

            setVisibility(View.GONE);
            mListView = null;
        }
    }

    /**
     * Decorate the listener to custom ScrollListener, our custom listener will receive
     * notifications first when list scrolls.
     *
     * @param l the scroll listener
     */
    public OnScrollListener decorateScrollListener(OnScrollListener l) {
        return new OnScrollerDecorator(this, l);
    }

    /**
     * draw the title to overlay when the list scrolling
     *
     * @param title CharSequence to draw
     */
    public void drawThumb(CharSequence title) {
        if (mState == STATE_NONE &&
                mListScrollState == OnScrollListener.SCROLL_STATE_FLING) {
            drawThumbInternal(title);
        }
    }

    private void refreshMask() {
        if (mListView == null) {
            return;
        }

        int newIndex = 0;
        final SectionIndexer indexer = getSectionIndexer();
        if (indexer != null) {
            final int first = mListView.getFirstVisiblePosition() - getListOffset();
            final int section = indexer.getSectionForPosition(first);
            if (section != -1) {
                String name = (String) indexer.getSections()[section];
                if (!TextUtils.isEmpty(name)) {
                    newIndex = Arrays.binarySearch(mTextHilighter.mIndexes, name);
                }
            }
        }

        if (mLastAlphabetIndex != newIndex) {
            mLastAlphabetIndex = newIndex;
            if(STATE_DRAGGING != mState) {
                slidTextHilightBackground(mLastAlphabetIndex);
            }
            invalidate();
        }
    }

    /**
     * get IntrinsicWidth of FastIndexer's background
     */
    public int getIndexerIntrinsicWidth() {
        Drawable background = getBackground();
        return background != null ? background.getIntrinsicWidth() : 0;
    }

    private SectionIndexer getSectionIndexer() {
        if (mListView == null) {
            return null;
        }

        SectionIndexer si = null;
        Object la = mListView.getAdapter();

        while (!(la instanceof SectionIndexer) && la instanceof WrapperListAdapter) {
            la = ((WrapperListAdapter)la).getWrappedAdapter();
        }
        if (la instanceof SectionIndexer) {
            si = (SectionIndexer) la;
        }

        return si;
    }

    private int getListOffset() {
        if (mListView instanceof ListView) {
            return ((ListView) mListView).getHeaderViewsCount();
        }

        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListView == null) {
            stop(0);
            return false;
        }

        SectionIndexer indexer = getSectionIndexer();
        if (indexer == null) {
            stop(0);
            return false;
        }

        final int action = MotionEvent.ACTION_MASK & event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mState = STATE_DRAGGING;
                setPressed(true);
                //$FALL-THROUGH$
            case MotionEvent.ACTION_MOVE:
                int pos = getPostion(event.getY(), indexer);
                if (pos < 0) {
                    mListView.setSelection(0);
                } else {
                    scrollTo(indexer, pos);
                    if(mTextHilighter != null
                            && event.getY() > (getTop() + getPaddingTop())
                            && event.getY() < (getBottom() - getPaddingBottom())){
                        mTextHilighter.update(getWidth() / 2, event.getY());
                        postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                slidTextHilightBackground(mLastAlphabetIndex);
            default:
                stop(FADE_DELAYED);
                break;
        }
        return true;
    }

    void stop(int delay) {
        setPressed(false);
        mState = STATE_NONE;
        postInvalidate();
        mHandler.removeMessages(MSG_FADE);
        if (delay <= 0) {
            if (mOverlay != null) {
                mOverlay.setVisibility(View.GONE);
            }
        } else {
            Message msg = mHandler.obtainMessage(MSG_FADE);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    private int getPostion(float y, SectionIndexer indexer) {
        Object[] sections = indexer.getSections();
        if (sections == null) {
            return -1;
        }

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        if (contentHeight <= 0) {
            return -1;
        }
        float rate = (y - paddingTop) / contentHeight;
        int needlePos = (int) (mTextHilighter.mIndexes.length * rate);
        if (needlePos < 0) {
            return -1;
        } else if (needlePos >= mTextHilighter.mIndexes.length) {
            return sections.length;
        }

        int section = Arrays.binarySearch(sections, mTextHilighter.mIndexes[needlePos]);
        if (section < 0) {
            // the return value is -index-1 when binarySearch can't be found, so
            // it's necessary to convert back to the corresponding position
            section = -section - 2;
        }

        if (section < 0) {
            section = 0;
        }
        return section;
    }

    /**
     * Reference from android.widget.FastScroller of Android2.3
     */
    private void scrollTo(SectionIndexer indexer, int position) {
        int count = mListView.getCount();
        int listOffset = getListOffset();
        float fThreshold = (1.0f / count) / 8;
        final Object[] sections = indexer.getSections();
        int sectionIndex;
        if (sections != null && sections.length > 1) {
            final int nSections = sections.length;
            int section = position;
            if (section >= nSections) {
                section = nSections - 1;
            }
            int exactSection = section;
            sectionIndex = section;
            int index = indexer.getPositionForSection(section);
            // Given the expected section and index, the following code will
            // try to account for missing sections (no names starting with..)
            // It will compute the scroll space of surrounding empty sections
            // and interpolate the currently visible letter's range across the
            // available space, so that there is always some list movement while
            // the user moves the thumb.
            int nextIndex = count;
            int prevIndex = index;
            int prevSection = section;
            int nextSection = section + 1;
            // Assume the next section is unique
            if (section < nSections - 1) {
                nextIndex = indexer.getPositionForSection(section + 1);
            }

            // Find the previous index if we're slicing the previous section
            if (nextIndex == index) {
                // Non-existent letter
                while (section > 0) {
                    section--;
                    prevIndex = indexer.getPositionForSection(section);
                    if (prevIndex != index) {
                        prevSection = section;
                        sectionIndex = section;
                        break;
                    } else if (section == 0) {
                        // When section reaches 0 here, sectionIndex must follow
                        // it.
                        // Assuming mSectionIndexer.getPositionForSection(0) ==
                        // 0.
                        sectionIndex = 0;
                        break;
                    }
                }
            }
            // Find the next index, in case the assumed next index is not
            // unique. For instance, if there is no P, then request for P's
            // position actually returns Q's. So we need to look ahead to make
            // sure that there is really a Q at Q's position. If not, move
            // further down...
            int nextNextSection = nextSection + 1;
            while (nextNextSection < nSections
                    && indexer.getPositionForSection(nextNextSection) == nextIndex) {
                nextNextSection++;
                nextSection++;
            }
            // Compute the beginning and ending scroll range percentage of the
            // currently visible letter. This could be equal to or greater than
            // (1 / nSections).
            float fPrev = (float) prevSection / nSections;
            float fNext = (float) nextSection / nSections;
            float current = (float) position / nSections;
            if (prevSection == exactSection && current - fPrev < fThreshold) {
                index = prevIndex;
            } else {
                index = prevIndex + Math.round((nextIndex - prevIndex) * (current - fPrev) / (fNext - fPrev));
            }
            // Don't overflow
            if (index > count - 1)
                index = count - 1;

            if (mListView instanceof ExpandableListView) {
                ExpandableListView expList = (ExpandableListView) mListView;
                expList.setSelectionFromTop(expList.getFlatListPosition(ExpandableListView
                        .getPackedPositionForGroup(index + listOffset)), 0);
            } else if (mListView instanceof ListView) {
                ((ListView) mListView).setSelectionFromTop(index + listOffset, 0);
            } else {
                mListView.setSelection(index + listOffset);
            }
        } else {
            int index = Math.round(position * count);
            if (mListView instanceof ExpandableListView) {
                ExpandableListView expList = (ExpandableListView) mListView;
                expList.setSelectionFromTop(expList.getFlatListPosition(ExpandableListView
                        .getPackedPositionForGroup(index + listOffset)), 0);
            } else if (mListView instanceof ListView) {
                ((ListView) mListView).setSelectionFromTop(index + listOffset, 0);
            } else {
                mListView.setSelection(index + listOffset);
            }
            sectionIndex = -1;
        }

        if (sectionIndex >= 0 && sections != null) {
            String text = sections[sectionIndex].toString();
            if (!TextUtils.isEmpty(text)) {
                drawThumbInternal(text.subSequence(0, 1));
            }
        }
    }

    private void drawThumbInternal(CharSequence title) {
        if (mListView == null) {
            return;
        }
        // ASC code of "!" is ranked in front of the "#", so it's used
        // instead of "*" in Unicode
        title = TextUtils.equals(title, STARRED_TITLE) ? STARRED_LABEL : title;
        mOverlay.setText(title);
        if (getVisibility() == View.VISIBLE) {
            mOverlay.setVisibility(View.VISIBLE);
            mHandler.removeMessages(MSG_FADE);
            Message msg = mHandler.obtainMessage(MSG_FADE);
            mHandler.sendMessageDelayed(msg, FADE_DELAYED);
        }
    }

    private void slidTextHilightBackground(int pos) {
        if (mTextHilighter == null) {
            return ;
        }

        if (pos < 0) {
            pos = 0;
        }

        int top = getPaddingTop();
        int height = getHeight() - top - getPaddingBottom();
        float alphaHeight = ((float) height) / mTextHilighter.mIndexes.length;

        float ycenter = pos * alphaHeight + top + alphaHeight/2F + 1;
        mTextHilighter.startSlidding(ycenter, mTextHilightAnimListener);
    }

    private static class TextHilighter {
        BitmapDrawable mBackground;
        Paint mPaint = new Paint();
        Bitmap mBmpBuffer;
        Canvas mCanvas;
        ValueAnimator mAnimator;
        Rect mTextBound;
        Rect mTextBoundIntersect;
        Rect mSrcBounds;
        Xfermode mClearMode;
        Xfermode mDstInMode;
        String[] mIndexes;
        int mNormalColor;
        int mActivatedColor;
        int mHilightColor;


        TextHilighter(Context context, TypedArray a) {
            mTextBound = new Rect();
            mSrcBounds = new Rect();
            final Resources res = context.getResources();
            final CharSequence[] table = a.getTextArray(R.styleable.AlphabetFastIndexer_indexerTable);
            if (table != null) {
                mIndexes = new String[table.length];
                int i = 0;
                for (CharSequence cs : table) {
                    mIndexes[i++] = cs.toString();
                }
            } else {
                mIndexes = res.getStringArray(R.array.alphabet_table);
            }

            mNormalColor = a.getColor(R.styleable.AlphabetFastIndexer_indexerTextColor,
                    res.getColor(R.color.black_20_percent));
            mActivatedColor = a.getColor(R.styleable.AlphabetFastIndexer_indexerTextActivatedColor,
                    res.getColor(R.color.black_20_percent));
            mHilightColor = a.getColor(R.styleable.AlphabetFastIndexer_indexerTextHighlightColor,
                    res.getColor(android.R.color.black));

            mPaint.setTextSize(a.getDimension(R.styleable.AlphabetFastIndexer_indexerTextSize,
                    res.getDimension(R.dimen.text_size_30)));
            mPaint.setAntiAlias(true);
            mPaint.setTextAlign(Align.CENTER);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);

            Drawable textHilightBackground = a.getDrawable(R.styleable.AlphabetFastIndexer_indexerTextHighligtBackground);
            Log.e("textHilightBackground", "textHilightBackground: " + textHilightBackground);
            if (textHilightBackground == null) {
                textHilightBackground = res.getDrawable(R.drawable.ic_list_az_focus);
            }
            if (textHilightBackground != null && textHilightBackground instanceof BitmapDrawable) {
                mBackground = (BitmapDrawable) textHilightBackground;

                Bitmap bmp = mBackground.getBitmap();
                mBmpBuffer = bmp.copy(Bitmap.Config.ARGB_8888, true);
                mCanvas = new Canvas(mBmpBuffer);

                mTextBoundIntersect = new Rect();

                mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
                mDstInMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
            }

            update(0, 0);
        }

        void update(float xcenter, float ycenter) {
            float width = mBackground.getIntrinsicWidth() / 2F;
            float height = mBackground.getIntrinsicHeight() / 2F;
            mTextBound.set((int) (xcenter - width + 1),
                    (int) (ycenter - height),
                    (int) (xcenter + width + 1),
                    (int) (ycenter + height));
        }


        void draw(Canvas canvas, boolean isPressed, int pos, float x, float y) {
            final Paint paint = mPaint;

            // asc码"!"排在"#"前面，所以用来代替Unicode中的"*"
            String alpha = TextUtils.equals(mIndexes[pos], STARRED_TITLE) ? STARRED_LABEL : mIndexes[pos];

            paint.getTextBounds(alpha, 0, alpha.length(), mSrcBounds);

            float textWidth = mSrcBounds.width();
            float textHeight = mSrcBounds.height();

            //draw text
            paint.setColor(isPressed ? mActivatedColor : mNormalColor);
            canvas.drawText(alpha, x,
                    y - (mSrcBounds.top + mSrcBounds.bottom) / 2f, paint);

            if (mTextBoundIntersect.intersect((int) (x - textWidth / 2f),
                    (int) (y - textHeight / 2f), (int) (x + textWidth / 2f), (int) (y + textHeight / 2f))) {

                x -= mTextBound.left;
                y -= mTextBound.top;

                paint.setColor(mHilightColor);
                mCanvas.drawText(alpha, x,
                        y - (mSrcBounds.top + mSrcBounds.bottom) / 2f, paint);
                mTextBoundIntersect.set(mTextBound);
            }
        }

        void beginDraw() {
            final Paint paint = mPaint;
            //clear the bmp buffer
            Xfermode xfermode = paint.getXfermode();
            paint.setXfermode(mClearMode);
            mCanvas.drawPaint(paint);
            paint.setXfermode(xfermode);

            //draw background into the bmp buffer
            mBackground.setBounds(0, 0, mTextBound.width(), mTextBound.height());
            mBackground.draw(mCanvas);

            mTextBoundIntersect.set(mTextBound);

        }


        void endDraw(Canvas canvas) {
            //clear the pixels are out of the background in bmp buffer
            Paint bgPaint = mBackground.getPaint();
            Xfermode xfermode = bgPaint.getXfermode();
            bgPaint.setXfermode(mDstInMode);
            mBackground.draw(mCanvas);
            bgPaint.setXfermode(xfermode);

            //draw bmp bufer into the screen
            canvas.drawBitmap(mBmpBuffer, null, mTextBound, null);
        }

        void startSlidding(float ycenter, ValueAnimator.AnimatorUpdateListener listener) {
            if (mAnimator != null) {
                mAnimator.cancel();
            }

            float ystart;

            if (mTextBound == null) {
                ystart = ycenter;
            } else {
                ystart = (mTextBound.top + mTextBound.bottom) / 2F;
            }

            mAnimator = ValueAnimator.ofFloat(ystart, ycenter);
            mAnimator.addUpdateListener(listener);
            mAnimator.setDuration(200);
            mAnimator.start();
        }
    }

    private static class OnScrollerDecorator implements OnScrollListener {
        private final OnScrollListener mListener;
        private final WeakReference<AlphabetFastIndexer> mIndexerRef;

        public OnScrollerDecorator(AlphabetFastIndexer indexer, OnScrollListener l) {
            mIndexerRef = new WeakReference<>(indexer);
            mListener = l;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            final AlphabetFastIndexer indexer = mIndexerRef.get();
            if (indexer != null) {
                indexer.refreshMask();
            }
            if (mListener != null) {
                mListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            final AlphabetFastIndexer indexer = mIndexerRef.get();
            if (indexer != null) {
                indexer.mListScrollState = scrollState;
            }
            if (mListener != null) {
                mListener.onScrollStateChanged(view, scrollState);
            }
        }

    }

}

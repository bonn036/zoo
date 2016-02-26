package com.mmnn.bonn036.zoo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.mmnn.bonn036.zoo.R;
import com.mmnn.bonn036.zoo.utils.MyTextUtils;
import com.mmnn.bonn036.zoo.view.LineWrapLayout;
import com.mmnn.bonn036.zoo.view.widget.MyDialog;

/**
 * @author tony
 * @date 26/12/2015
 */
public class MultiTextInputActivity extends Activity implements OnClickListener {

    private int mPadding;
    private int mTextSize;
    private int mMultiTextHeight;

    private String mMultiText;
    private String mDialogMsg;
    private String mLimitTips;
    private int mLimit = 3;

    private LineWrapLayout mContainer;
    private TextView mBtnAdd;
    private OnLongClickListener mLabelOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100L);
            removeText(v);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_text_input);
        mContainer = (LineWrapLayout) findViewById(R.id.container);
        mPadding = getResources().getDimensionPixelSize(R.dimen.margin_10);
        mTextSize = getResources().getDimensionPixelSize(R.dimen.text_size_36);
        mMultiTextHeight = getResources().getDimensionPixelSize(R.dimen.margin_90);

        Intent intent = getIntent();
        mMultiText = MyTextUtils.getString(intent, "multi_text");
        mDialogMsg = MyTextUtils.getString(intent, "dialog_message");
        mLimitTips = MyTextUtils.getString(intent, "limit_tips");
        mLimit = intent.getIntExtra("limit", 3);

        mBtnAdd = setAddBtn();

        // Set limit
        TextView v = (TextView) findViewById(R.id.limit);
        v.setText(mLimitTips);

        if (!TextUtils.isEmpty(mMultiText)) {
            setText(mMultiText.split(","));
        }
    }

    private TextView setAddBtn() {
        TextView t = new TextView(this);
        t.setBackgroundResource(R.drawable.add);
        t.setOnClickListener(this);
        t.setLayoutParams(new LayoutParams(mMultiTextHeight * 2, mMultiTextHeight));
        mContainer.addView(t);
        return t;
    }

    private void setText(String... texts) {
        if (texts.length > 0) {
            for (String text : texts) {
                TextView t = new TextView(this);
                t.setText(text);
                t.setBackgroundResource(R.drawable.circle_corner_background);
                t.setSingleLine(true);
                t.setTextColor(Color.BLACK);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                t.setPadding(mPadding, mPadding, mPadding, mPadding);
                t.setOnLongClickListener(mLabelOnLongClickListener);
                t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, mMultiTextHeight));
                mContainer.addView(t, mContainer.getChildCount() - 1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnAdd) {
            addText();
        }
    }

    private String getMultiText() {
        int count = mContainer.getChildCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            TextView v = (TextView) mContainer.getChildAt(i);
            sb.append(v.getText()).append(" ");
        }
        int len = sb.length();
        if (len > 0) {
            sb.setLength(len - 1);
            return sb.toString();
        }
        return "";
    }

    // Add text.
    private void addText() {
        int count = mContainer.getChildCount();
        if (count == mLimit + 1) {
            Toast.makeText(this, mLimitTips, Toast.LENGTH_SHORT).show();
            return;
        }

        MyDialog dialog = new MyDialog(this, new MyDialog.AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    String text = bundle.getString("input");
                    if (!TextUtils.isEmpty(text)) {
                        setText(text);
                    }
                }
            }
        }, mDialogMsg, true, 10, "");
        dialog.show();
    }

    protected void removeText(final View v) {
        String tips = getString(R.string.delete_tips);
        MyDialog dialog = new MyDialog(this, new MyDialog.AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    mContainer.removeView(v);
                }
            }
        }, tips);
        dialog.show();
    }
}

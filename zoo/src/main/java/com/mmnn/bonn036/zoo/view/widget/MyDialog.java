package com.mmnn.bonn036.zoo.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mmnn.bonn036.zoo.R;
import com.mmnn.bonn036.zoo.utils.MyTextUtils;

public class MyDialog extends Dialog {

    private EditText mEditText;
    private AlertDialogUser mUser;
    private String mMsg;
    private boolean mNeedInput = false;
    private int mLimit;
    private String mHint;

    public MyDialog(Context context, AlertDialogUser user, String msg) {
        this(context, user, msg, false, Integer.MAX_VALUE, null);
    }

    public MyDialog(Context context, AlertDialogUser user, String msg, boolean needInput, int limit, String hint) {
        super(context);
        this.mUser = user;
        this.mMsg = msg;
        this.mNeedInput = needInput;
        this.mLimit = limit;
        this.mHint = hint;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_dialog);
        setupView();
    }

    private void setupView() {
        TextView text = (TextView) findViewById(R.id.message);
        text.setText(mMsg);

        final Button cancel = (Button) findViewById(R.id.btn_cancel);
        final Button ok = (Button) findViewById(R.id.btn_ok);

        if (mNeedInput) {
            mEditText = (EditText) findViewById(R.id.text);
            mEditText.setVisibility(View.VISIBLE);
            mEditText.setHint(mHint);
            mEditText.setFilters(new InputFilter[]{new LengthFilter(mLimit), new InputFilter() {

                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    String text = source.subSequence(start, end).toString();
                    return MyTextUtils.isLetterDigitOrChinese(text) ? text : "";
                }
            }});
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_ok) {
                    onOk(view);
                } else if (view.getId() == R.id.btn_cancel) {
                    onCancel(view);
                }
            }
        };
        cancel.setOnClickListener(listener);
        ok.setOnClickListener(listener);
    }

    public void onOk(View view) {
        this.dismiss();
        if (this.mUser != null) {
            Bundle b = null;
            if (mNeedInput) {
                b = new Bundle();
                b.putString("input", mEditText.getText().toString());
            }
            this.mUser.onResult(true, b);
        }
    }

    public void onCancel(View view) {
        this.dismiss();
        if (this.mUser != null) {
            this.mUser.onResult(false, null);
        }
    }

    public interface AlertDialogUser {
        void onResult(boolean confirmed, Bundle bundle);
    }

}

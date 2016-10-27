
package com.mmnn.zoo.view.wheel;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mmnn.zoo.R;

public class WheelWindow extends AlertDialog implements View.OnClickListener {

    private View mConentView;
    private NumberPicker mNumberPicker;
    private String[] mDisplayedValues;
    private TextView mLabelView;
    private OnValueSelectedListener mOnValueSelectedListener;
    private String mUnit = "";

    public WheelWindow(Context context) {
        super(context);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        initView();
    }

    public void setLabelVisible(boolean visible) {
        if (visible) {
            mLabelView.setVisibility(View.VISIBLE);
        } else {
            mLabelView.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mConentView);

        initValues();
    }

    private void initView() {
        mConentView = View.inflate(getContext(), R.layout.layout_wheelpicker_window, null);
        mLabelView = (TextView) mConentView.findViewById(R.id.label);
        mConentView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mConentView.findViewById(R.id.btn_ok).setOnClickListener(this);
        mNumberPicker = (NumberPicker) mConentView.findViewById(R.id.numberPicker);
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateValue(mDisplayedValues == null ? String.valueOf(newVal) : mDisplayedValues[newVal]);
            }
        });
    }

    private void updateValue(String value) {
        mLabelView.setText(value + (TextUtils.isEmpty(mUnit) ? "" : mUnit));
    }

    public void setOnValueSelectedListener(OnValueSelectedListener listener) {
        mOnValueSelectedListener = listener;
    }

    private void initValues() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        lp.width = dm.widthPixels;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    public void initViewParam(int defValue, int minValue, int maxValue, int steps, String unit) {
        mUnit = unit;
        if (steps == 1 && TextUtils.isEmpty(unit)) {
            mNumberPicker.setMinValue(minValue);
            mNumberPicker.setMaxValue(maxValue);
            mNumberPicker.setValue(defValue);
            updateValue(String.valueOf(defValue));
        } else {
            int length = (maxValue - minValue) / steps;
            if (length < 1) {
                return;
            }
            mDisplayedValues = new String[length + 1];
            int defIndex = 0;
            for (int i = 0; i < mDisplayedValues.length; i++) {
                int value = minValue + i * steps;
                if (defValue == value) {
                    defIndex = i;
                }
                mDisplayedValues[i] = String.valueOf(value); //+ (TextUtils.isEmpty(unit) ? "" : unit);
            }
            mNumberPicker.setDisplayedValues(mDisplayedValues);
            mNumberPicker.setMinValue(0);
            mNumberPicker.setMaxValue(mDisplayedValues.length - 1);
            mNumberPicker.setValue(defIndex);
            updateValue(mDisplayedValues[defIndex]);
        }
    }

    public void initViewParam(String defValue, String[] valueArray) {
        mDisplayedValues = valueArray;
        int defIndex = 0;
        if (!TextUtils.isEmpty(defValue)) {
            for (int i = 0; i < valueArray.length; i++) {
                if (valueArray[i].equals(defValue)) {
                    defIndex = i;
                    break;
                }
            }
        }
        mNumberPicker.setDisplayedValues(mDisplayedValues);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(mDisplayedValues.length - 1);
        mNumberPicker.setValue(defIndex);
        updateValue(mDisplayedValues[defIndex]);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    dismiss();
                    break;
                case R.id.btn_ok:
                    if (mOnValueSelectedListener != null) {
                        mOnValueSelectedListener.onValueSelected(mLabelView.getText().toString());
                    }
                    dismiss();
                    break;
            }
        }
    }

    public interface OnValueSelectedListener {
        void onValueSelected(String value);
    }
}

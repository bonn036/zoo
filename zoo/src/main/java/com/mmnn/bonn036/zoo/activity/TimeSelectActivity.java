package com.mmnn.bonn036.zoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mmnn.bonn036.zoo.MyApp;
import com.mmnn.bonn036.zoo.R;
import com.mmnn.bonn036.zoo.view.wheel.NumberPicker;
import com.mmnn.bonn036.zoo.view.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeSelectActivity extends Activity implements OnClickListener {
    //    public static final String LABEL_MORNING = "上午 08:00~12:00";
//    public static final String LABEL_NOON = "中午 12:00~14:00";
//    public static final String LABEL_AFTERNOON = "下午 14:00~18:00";
//    public static final String LABEL_NIGHT = "晚上 18:00~22:00";
//    public static final String LABEL_ALL = "均可 08:00~22:00";
    private static final int TYPE_DATE = 0;
    private static final int TYPE_TIME = 1;
    private static final int TYPE_FREE_TIME = 2;
    private boolean mIsAccurateTime;
    private View accurateTimeLayout;
    private View freeTimeLayout;

    private NumberPicker mAccurateDataNumberPick;
    private NumberPicker mAccurateTimeNumberPick;
    private NumberPicker mFreeDateNumberPick;
    private NumberPicker mFreeTimeNumberPick;

    private ToggleButton accurateToggleButton;
    private ToggleButton freeTimeToggleButton;

    private TextView accurateTextView;
    private TextView freeTimeTextView;

    private String mSelectDate;
    private String mSelectTime;

    private String mTextValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_match_time);
        String value = getIntent().getStringExtra("value");
        mIsAccurateTime = value != null && value.equals("1");
        mTextValue = getIntent().getStringExtra("text");
        initNumberPick();
        initUI();
    }

    private void initUI() {
        switchView(mIsAccurateTime, true);

//        mSelectDate = getCurrentDate();
//        mSelectTime = getCurrentTime(mIsAccurateTime);

        if (mTextValue != null) {
            if (mIsAccurateTime) {
                accurateTextView.setText(mTextValue);
            } else {
                freeTimeTextView.setText(mTextValue);
            }
        } else {
            if (mIsAccurateTime) {
                mSelectDate = mAccurateDataNumberPick.getDisplayedValues()[mAccurateDataNumberPick.getValue()];
                mSelectTime = mAccurateTimeNumberPick.getDisplayedValues()[mAccurateTimeNumberPick.getValue() - 8];
            } else {
                mSelectDate = mFreeDateNumberPick.getDisplayedValues()[mFreeDateNumberPick.getValue()];
                mSelectTime = mFreeTimeNumberPick.getDisplayedValues()[mFreeTimeNumberPick.getValue()];
            }
            accurateTextView.setText(mAccurateDataNumberPick.getDisplayedValues()[mAccurateDataNumberPick.getValue()] +
                    mAccurateTimeNumberPick.getDisplayedValues()[mAccurateTimeNumberPick.getValue() - 8]);
            freeTimeTextView.setText(mFreeDateNumberPick.getDisplayedValues()[mFreeDateNumberPick.getValue()] +
                    mFreeTimeNumberPick.getDisplayedValues()[mFreeTimeNumberPick.getValue()]);
//            accurateTextView.setText(mSelectDate + getCurrentTime(true));
//            freeTimeTextView.setText(mSelectDate + getCurrentTime(false));
        }

        accurateToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                switchView(on, false);
                mIsAccurateTime = on;
            }
        });

        freeTimeToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                switchView(!on, false);
                mIsAccurateTime = !on;
            }
        });
    }

    private void initNumberPick() {

        mAccurateDataNumberPick = (NumberPicker) findViewById(R.id.slelect_data_accurate);
        mAccurateTimeNumberPick = (NumberPicker) findViewById(R.id.slelect_time_accurate);

        mFreeDateNumberPick = (NumberPicker) findViewById(R.id.free_data_accurate);
        mFreeTimeNumberPick = (NumberPicker) findViewById(R.id.free_time_accurate);

        initNumberPick(mAccurateDataNumberPick, 0, 365, TYPE_DATE, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == 0 && newVal != 1) {
                    mAccurateDataNumberPick.setValue(0);
                    return;
                }
                mSelectDate = picker.getDisplayedValues()[newVal];
                accurateTextView.setText(mSelectDate + mSelectTime);
            }
        });

        initNumberPick(mAccurateTimeNumberPick, 8, 22, TYPE_TIME, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mSelectTime = picker.getDisplayedValues()[newVal - 8];
                accurateTextView.setText(mSelectDate + mSelectTime);
            }
        });

        initNumberPick(mFreeDateNumberPick, 0, 365, TYPE_DATE, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == 0 && newVal != 1) {
                    mFreeDateNumberPick.setValue(0);
                    return;
                }
                mSelectDate = picker.getDisplayedValues()[newVal];
                freeTimeTextView.setText(mSelectDate + mSelectTime);
            }
        });

        initNumberPick(mFreeTimeNumberPick, FreeTimeType.MORNING.ordinal(), FreeTimeType.ALL.ordinal(),
                TYPE_FREE_TIME,
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mSelectTime = picker.getDisplayedValues()[newVal];
                        freeTimeTextView.setText(mSelectDate + mSelectTime);
                    }
                });
    }

    private void initNumberPick(NumberPicker numberPicker, int min, int max, int type,
                                NumberPicker.OnValueChangeListener
                                        onValueChangeListener) {
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
        String[] displayNames = new String[max - min + 1];
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        String formatter = "yyyy-MM-dd ";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatter);

        if (type == TYPE_DATE) {
            for (int i = min; i <= max; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, i == 0 ? 0 : 1);
                displayNames[i - min] = dateFormat.format(calendar.getTime());
            }
        } else if (type == TYPE_TIME) {
            for (int i = min; i <= max; i++) {
                displayNames[i - min] = String.format("%02d:00", i);
            }
        } else if (type == TYPE_FREE_TIME) {
            for (int i = FreeTimeType.MORNING.ordinal(); i <= FreeTimeType.ALL.ordinal(); i++) {
                displayNames[i] = FreeTimeType.values()[i].getName();
            }
        }

        numberPicker.setDisplayedValues(displayNames);
        if (onValueChangeListener != null) {
            numberPicker.setOnValueChangedListener(onValueChangeListener);
        }
    }

    @Override
    public void onClick(View v) {
        onSure();
    }

    private void onSure() {
        setResult();
        finish();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra("value", mIsAccurateTime ? "0" : "1");
        intent.putExtra("text", mSelectDate + mSelectTime);
        setResult(RESULT_OK, intent);
    }

    private String getCurrentTime(boolean isAccurateTime) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (isAccurateTime) {
            return hour + ":00";
        } else {
            return FreeTimeType.valueOf(hour).getName();
        }
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        String formatter = "yyyy-MM-dd ";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatter);
        return dateFormat.format(calendar.getTime());
    }

    private Date parseDate(String str) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        String formatter = "yyyy-MM-dd HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatter);
        try {
            return dateFormat.parse(str);
        } catch (Exception e) {
            return Calendar.getInstance().getTime();
        }
    }

    private Date parseFreeDate(String str) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        String formatter = "yyyy-MM-dd HH:mm";
        String start = str.split("~")[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatter);
        try {
            return dateFormat.parse(start);
        } catch (Exception e) {
            return Calendar.getInstance().getTime();
        }
    }

    private void switchView(boolean isAccurate, boolean isForce) {
        if (mIsAccurateTime == isAccurate && !isForce) {
            return;
        }
        if (isAccurate) {
            accurateTimeLayout.setVisibility(View.VISIBLE);
            freeTimeLayout.setVisibility(View.GONE);
            freeTimeToggleButton.setToggleOff();
            accurateToggleButton.setToggleOn();
        } else {
            accurateTimeLayout.setVisibility(View.GONE);
            freeTimeLayout.setVisibility(View.VISIBLE);
            accurateToggleButton.setToggleOff();
            freeTimeToggleButton.setToggleOn();
        }
    }

    public enum FreeTimeType {
        MORNING(MyApp.getInstance().getResources().getStringArray(R.array.format_free_time)[0]),
        NOON(MyApp.getInstance().getResources().getStringArray(R.array.format_free_time)[1]),
        AFTERNOON(MyApp.getInstance().getResources().getStringArray(R.array.format_free_time)[2]),
        NIGHT(MyApp.getInstance().getResources().getStringArray(R.array.format_free_time)[3]),
        ALL(MyApp.getInstance().getResources().getStringArray(R.array.format_free_time)[4]);

        private String name;

        FreeTimeType(String name) {
            this.name = name;
        }

        public static int indexOf(String name) {
            for (FreeTimeType type : FreeTimeType.values()) {
                if (type.name.equals(name)) {
                    return type.ordinal();
                }
            }
            return MORNING.ordinal();
        }

        public static FreeTimeType valueOf(int hour) {
            if (hour < 12) {
                return MORNING;
            } else if (hour < 14) {
                return NOON;
            } else if (hour < 18) {
                return AFTERNOON;
            } else if (hour < 22) {
                return NIGHT;
            } else {
                return ALL;
            }
        }

        public String getName() {
            return name;
        }
    }

}

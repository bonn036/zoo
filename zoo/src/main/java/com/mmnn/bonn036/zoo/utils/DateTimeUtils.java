package com.mmnn.bonn036.zoo.utils;

import android.text.TextUtils;

import com.mmnn.bonn036.zoo.MyApp;
import com.mmnn.bonn036.zoo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtils {

    public static int getCurHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static String getFormatedDate(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, offset);
        String format = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }

    public static String getCurFormattedDateTime() {
        Calendar calendar = Calendar.getInstance();
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }

    public static String addWeekDay(String formattedDate) {
        if (TextUtils.isEmpty(formattedDate)) {
            return null;
        }
        int year;
        int month;
        int day;
        String date;
        String time;
        try {
            year = Integer.valueOf(formattedDate.substring(0, 4));
            month = Integer.valueOf(formattedDate.substring(5, 7));
            day = Integer.valueOf(formattedDate.substring(8, 10));
            date = formattedDate.substring(0, 10);
            time = formattedDate.substring(11, 16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int days = (year - 1900) * 365 + (year - 1900) / 4;
        if (year % 4 == 0 && month <= 2) {
            days--;
        }
        switch (month) {
            case 12:
                days += 30; // NO break!!! Fall through to all cases
            case 11:
                days += 31;
            case 10:
                days += 30;
            case 9:
                days += 31;
            case 8:
                days += 31;
            case 7:
                days += 30;
            case 6:
                days += 31;
            case 5:
                days += 30;
            case 4:
                days += 31;
            case 3:
                days += 28;
            case 2:
                days += 31;
        }
        days = (days + day) % 7;
        String[] dayOfWeek = MyApp.getInstance().getResources().getStringArray(R.array.day_of_week);

        return date + " " + dayOfWeek[days] + " " + time;
    }

}

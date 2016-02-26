package com.mmnn.bonn036.zoo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtil {

	public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd HH:mm:ss";

	public static Calendar getCalendarFromString(String time, String format)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(time));
		return calendar;
	}

	public static Calendar getCalendarFromString(String time)
			throws ParseException {
		return getCalendarFromString(time, DATE_FORMAT_SERVER);
	}
	
	public static String getCalendarString(long time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		Date result = new Date(time);
		return sdf.format(result);
	}
	
	public static String getCalendarString(long time) {
		return getCalendarString(time, DATE_FORMAT_SERVER);
	}
	
	public static String getDateString(long time) {
//		Calendar cal = Calendar.getInstance();
		Date date = new Date(time);
//		cal.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		return sdf.format(date);
//		return DateFormat.format("yyyy年MM月dd日 HH:mm", cal).toString();
	}
	
	public static String getDateStringForFileName(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		return sdf.format(date);
	}
}

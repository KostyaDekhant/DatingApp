package com.example.datingappclient.utils;

import android.util.Log;

import com.example.datingappclient.constants.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String format = "yyyy-MM-dd";
    private static final String logTag = Constants.GLOBAL_LOG_TAG + "DATE UTILS";

    public static int dateToAge(String date) {
        try {
            LocalDate birth = LocalDate.parse(date.replace("\"", ""));
            LocalDate today = LocalDate.now();
            return Period.between(birth, today).getYears();
        } catch (Exception e) {
            Log.e(logTag, e.toString());
            return -1;
        }
    }

    public static String formatTime(Timestamp timestamp) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("d MMMM", new Locale("ru"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String day = dayFormat.format(timestamp);
        String time = timeFormat.format(timestamp);

        return String.format("%s, %s", day, time);
    }

    public static String formatTimeRange(Timestamp start, Timestamp end) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("d MMMM", new Locale("ru"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String startDay = dayFormat.format(start);
        String endDay = dayFormat.format(end);
        String startTime = timeFormat.format(start);
        String endTime = timeFormat.format(end);

        if (startDay.equals(endDay)) {
            // Один и тот же день
            return String.format("%s, %s–%s", startDay, startTime, endTime);
        } else {
            // Разные дни
            return String.format("%s, %s – %s, %s", startDay, startTime, endDay, endTime);
        }
    }

    public static int dateToAge(LocalDate date) {
        try {
            LocalDate today = LocalDate.now();
            return Period.between(date, today).getYears();
        } catch (Exception e) {
            Log.e(logTag, e.toString());
            return -1;
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public static LocalDate stringToLocalDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            Log.e(logTag, e.toString());
            return null;
        }
    }

    public static String localDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static String timestampToHoursMins(Timestamp sendtime) {
        if (sendtime == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(sendtime);
    }
}

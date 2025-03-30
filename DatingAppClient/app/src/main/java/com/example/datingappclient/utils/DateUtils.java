package com.example.datingappclient.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    private static final String format = "dd-MM-yyyy";
    public static int dateToAge(String date) {
        try {
            LocalDate birth = LocalDate.parse(date.replace("\"", ""));
            LocalDate today = LocalDate.now();
            return Period.between(birth, today).getYears();
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return -1;
        }
    }

    public static int dateToAge(LocalDate date) {
        try {
            LocalDate today = LocalDate.now();
            return Period.between(date, today).getYears();
        } catch (Exception e) {
            Log.d("Error", e.toString());
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
            return LocalDate.parse(dateString);
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return null; // или выбросьте исключение, если это более уместно
        }
    }

    public static String localDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }
}

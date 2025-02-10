package com.example.datingappclient.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class DateUtils {
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

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date now = new Date();
        return sdfDate.format(now);
    }
}

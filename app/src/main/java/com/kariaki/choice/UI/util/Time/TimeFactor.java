package com.kariaki.choice.UI.util.Time;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFactor {


    public static String getDetailedDate(long milliSeconds, long currentTime) {

        return creator(milliSeconds, currentTime);

    }

    public static String getDetailedDate(long milliSeconds, String format) {

        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ssZ");

        milliSeconds = now(milliSeconds);
        String dateString = formatter.format(new Date(milliSeconds));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date value = formatter.parse(dateString);
            SimpleDateFormat newFormat = new SimpleDateFormat(format);
            newFormat.setTimeZone(TimeZone.getDefault());
            dateString = newFormat.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
            dateString = "";
        }
        return dateString;

    }

    public static String getDetailedDate(long milliSeconds) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd", Locale.getDefault());
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        milliSeconds = now(milliSeconds);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis((int) milliSeconds);
        Date date = calendar.getTime();
        long current_time = System.currentTimeMillis();
        return formatter.format(calendar.getTime());

    }


    public static Date getDateDate(long milliSeconds, String format) {

        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ssZ");

        milliSeconds = now(milliSeconds);
        String dateString = formatter.format(new Date(milliSeconds));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date value = null;
        try {
            value = formatter.parse(dateString);
            SimpleDateFormat newFormat = new SimpleDateFormat(format);
            newFormat.setTimeZone(TimeZone.getDefault());
            dateString = newFormat.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
            dateString = "";
        }
        return value;

    }

    private static String creator(long initial_time, long final_time) {

        String output = "";

        Date initialDate = getDateDate(initial_time, "yyy:MM:dd");
        Date currentDate = getDateDate(final_time, "yyy:MM:dd");
        String initial_dateString = getDetailedDate(initial_time, "yyy:MM:dd");
        String current_dateString = getDetailedDate(final_time, "yyy:MM:dd");
        if (initial_dateString.equals(current_dateString)) {
            String initialMoment = getDetailedDate(initial_time, "HH:mm");
            String currentMomnet = getDetailedDate(final_time, "HH:mm");
            if (initialMoment.equals(currentMomnet)) {
                output = "Just now";
            } else {
                output = getDetailedDate(initial_time, "HH:mm");
            }

        } else {

            int dayDifference = currentDate.getDate() - initialDate.getDate();
            int monthDifference = currentDate.getMonth() - initialDate.getMonth();

            if (monthDifference == 0) {
                int weeks = dayDifference / 7;
                if (weeks == 0) {
                    if (dayDifference == 1) {
                        output = "1d";
                    } else {
                        output = dayDifference + "d";
                    }

                } else {
                    output = weeks + "w";

                    //output = weeks;

                }

            } else {
                Date newInitialDate = getDateDate(initial_time, "dd/MM/yyyy");
                output =DateFormat.getDateInstance(DateFormat.LONG).format(newInitialDate);

                //output = newInitialDate.getDate() + "/" + newInitialDate.getMonth() + "/" + newInitialDate.getYear();
            }


        }
        return output;
    }


    public static String getMessageTime(long timeStamp) {

        return getDetailedDate(timeStamp, "HH:mm");
    }

    public static boolean lifeSpanTimer(long postLifeSpan) {
        boolean output = false;

        // if the post will never delete then the lifespan will be -1.... if the life span is edited or customized ,then
        // the lifespan will be less than 24

        if (postLifeSpan != -1 && postLifeSpan != 24) {
            long now = now(System.currentTimeMillis());
            if (now(postLifeSpan) <= now) {
                output = true;
            }
        }

        return output;
    }

    public static long updateLifeSpan(long currentLifeSpan, int minutes_to_add, int responseCount) {

        currentLifeSpan = now(currentLifeSpan);
        Date initialDate = getDateDate(currentLifeSpan, "yyy:MM:dd");
        long output;

        int addedTime = lifeSpanProcess(minutes_to_add, responseCount);
        initialDate.setMinutes(initialDate.getMinutes() + addedTime);

        output = initialDate.getTime();

        return output;
    }

    public static int lifeSpanProcess(int minutes_to_add, int response_count) {

        int output = minutes_to_add;
        if (response_count >= 100) {
            float lfSpan = (float) (minutes_to_add / (Math.log10(response_count)));
            output = (int) lfSpan;
        }
        return output;

    }

    public static long createDefaultLifeSpan(long currentTime, int hours_to_add) {
        currentTime = now(currentTime);
        Date initialDate = getDateDate(currentTime, "yyy:MM:dd");
        initialDate.setHours(initialDate.getHours() + hours_to_add);
        return initialDate.getTime();

    }


    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }


    public static long undoUpdateLifeSpan(long currentLifeSpan, int minutes_to_add, int responseCount) {

        Date initialDate = getDateDate(currentLifeSpan, "yyy:MM:dd");
        long output;

        int addedTime = lifeSpanProcess(minutes_to_add, responseCount);
        initialDate.setMinutes(initialDate.getMinutes() - addedTime);

        output = initialDate.getTime();

        return output;
    }

    public static long now(long millis) {
        // int offset = android.icu.util.TimeZone.getDefault().getRawOffset() + android.icu.util.TimeZone.getDefault().getDSTSavings();
        //long now = millis + offset;
        return millis;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static long now2(long millis) {
        int offset = android.icu.util.TimeZone.getDefault().getRawOffset() + android.icu.util.TimeZone.getDefault().getDSTSavings();
        long now = millis + offset;
        return millis;
    }

}

package com.example.talking_eye_guide_app.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTimeFormatUtils {


    public static String dateAndTime(Date date){
        date = new Date();
        DateFormat dateAndTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return dateAndTimeFormat.format(date);

    }
    public static String dateForDocumentName (Date date){
        date = new Date();
        DateFormat dateForDocumentName = new SimpleDateFormat("ddMMyyyy");
        return  dateForDocumentName.format(date);
    }

    public static String dateFormat(Date date){
        date =new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
    public static String timeFormat(Date date){
        date = new Date();
        DateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return  timeFormat.format(date);
    }
    public static String wordDateFormat(String date){
        String dateFormatted;

        DateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date1 = inputDateFormat.parse(date);
            DateFormat outputDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy");
            dateFormatted = outputDateFormat.format(date1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return  dateFormatted;
    }
    public static String dateAndTimeConvertedToTimeFormat(String dateAndTime){
        String time;

        DateFormat dateAndTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        try {
            Date date= dateAndTimeFormat.parse(dateAndTime);
            DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            time = timeFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return  time;

    }
}

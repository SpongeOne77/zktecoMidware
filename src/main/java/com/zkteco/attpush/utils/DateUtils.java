package com.zkteco.attpush.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public String Time(){
        String time = convertDate2Str(new Date());
        System.out.println(time);
        return time;
    }
    public static String convertDate2Str(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//honor
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        long str = (((((year-100-1900)*12+month)*31+day-1)*24+hour)*60+minute)*60+second;
        return str+"";
    }


}

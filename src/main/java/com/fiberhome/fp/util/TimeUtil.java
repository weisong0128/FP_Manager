package com.fiberhome.fp.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public class TimeUtil {

    // public static DateFormat format = new SimpleDateFormat("yyyyMMdd");
    private static  final int NUMBER_1000=1000;
    public static long beforeFewDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -days);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date updateDate = calendar.getTime();
        return updateDate.getTime() / NUMBER_1000;
    }

    public static long beforeFewMonthLong(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -month);
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date updateDate = calendar.getTime();
        return updateDate.getTime() / NUMBER_1000;
    }

    public static String beforeFewMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -month);
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date updateDate = calendar.getTime();
        return long2String(updateDate.getTime() / NUMBER_1000, "yyyyMM");
    }

    public static long String2Long(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strDate = format.parse(date, pos);
        return strDate.getTime() / NUMBER_1000;
    }

    public static String long2String(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Timestamp timestamp = new Timestamp(time * NUMBER_1000);
        return simpleDateFormat.format(timestamp);
    }


    public static List<String> partitions(int month) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i <= month; i++) {
            list.add(beforeFewMonth(i));
        }
        return list;
    }

    private static  final int NUMBER_7=7;
    private static  final int NUMBER_15=15;
    public static List<String> partitons(String dateTag) {
        int day = new Date().getDate();
        List<String> list = new ArrayList<>();
        switch (dateTag) {
            case "today":
                list = partitions(0);
                break;
            case "seven":
                if (day >= NUMBER_7) {
                    list = partitions(0);
                } else {
                    list = partitions(1);
                }
                break;
            case "halfMonth":
                if (day >= NUMBER_15) {
                    list = partitions(0);
                } else {
                    list = partitions(1);
                }
                break;
            default:
        }
        return list;
    }
    public static List<String> getMonthByLong(Long start, Long end) {
        start = start * NUMBER_1000;
        end = end * NUMBER_1000;
        ArrayList<String> list = new ArrayList<>();
        try {
            DateFormat format = new SimpleDateFormat("yyyyMM");
            Calendar cal = Calendar.getInstance();
            Date startMonth = format.parse(format.format(new Date(start)));
            Date endMonth = format.parse(format.format(new Date(end)));
            cal.setTime(startMonth);
            while (true) {
                /*int year = cal.get(Calendar.YEAR);
                int momth = cal.get(Calendar.MONTH) + 1;*/
                String s = format.format(startMonth);
                if (startMonth.getTime() > endMonth.getTime()) {
                    break;
                }
                list.add(s);
                cal.add(Calendar.MONTH, 1);
                startMonth = cal.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String Date2String(Date date, String regex) {
        DateFormat dateFormat = new SimpleDateFormat(regex);
        return dateFormat.format(date);
    }

    public static Date String2Date(String dateStr, String regex) {
        DateFormat dateFormat = new SimpleDateFormat(regex);
        Date parse = null;
        try {
            parse = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }
    private static final  int NUMBER_12=12;
    public static  List<String> convertMonth(Long startTime,Long endTime){
       // List<String> list = new ArrayList<>();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        //Calendar calendar = Calendar.getInstance();
        Integer startYear = Integer.valueOf(yearFormat.format(startTime));
        Integer endYear = Integer.valueOf(yearFormat.format(endTime));
        Integer startMonth = Integer.valueOf(monthFormat.format(startTime));
        Integer endMonth = Integer.valueOf(monthFormat.format(endTime));
        int count=0;
        if (startYear.equals(endYear)){
            count=endMonth-startMonth+1;
        }else {
            count=(endYear-startYear-1)*NUMBER_12+(endMonth+NUMBER_12-startMonth+1);
        }
        return  partitions(count);
    }
}

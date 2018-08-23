package com.chuanzhe.chuanzhemap.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by  yingke on 2018-08-05.
 * yingke.github.io
 */
public class C {

    public static String Bmob_APPID="997fece55e856c01c6c83a2e55c64ef5";
    public static String EDIT = "edit";
    public static String ADD ="add";
    public static String ACTION = "action";
    public static String BLUE = "blue";
    public static String YELLOW = "yellow";
    public static String GREEN = "green";
    public static String RED = "red";

    public static long getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day ;
    }
}

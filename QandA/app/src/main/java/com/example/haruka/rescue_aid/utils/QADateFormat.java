package com.example.haruka.rescue_aid.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QADateFormat {

    public final static String simpleDateFormat =  "yyyy.MM.dd kk:mm:ss";
    public final static String simpleDateFormat_ =  "yyyy.MM.dd 00:mm:ss";
    public final static String JapaneseDateFormat =  "yyyy年MM月dd日 kk時mm分";
    public final static String JapaneseDateFormat_ =  "yyyy年MM月dd日 00時mm分";
    public final static String FileDateFormat = "yyyyMMddkkmmss";
    public final static String FileDateFormat_ = "yyyyMMdd00mmss";
    public final static String FileDateFormat2 = "MM/dd kk:mm";
    public final static String FileDateFormat2_ = "MM/dd 00:mm";
    private final static String DateFormatCheck = "kk";

    public static boolean check00(Date date){
        return new SimpleDateFormat(DateFormatCheck).format(date).equals("24");
    }

    public static String getInstance(){
        Date date = new Date();
        return getStringDate(date);
    }

    public static String getStringDate(Date date){
        SimpleDateFormat sdf;
        if(check00(date)){
            sdf = new SimpleDateFormat(simpleDateFormat_);
        } else {
            sdf = new SimpleDateFormat(simpleDateFormat);
        }
        return sdf.format(date);
    }

    public static String getStringDateJapanese(Date date){
        SimpleDateFormat sdf;
        if (check00(date)){
            sdf = new SimpleDateFormat(JapaneseDateFormat_);
        } else {
            sdf = new SimpleDateFormat(JapaneseDateFormat);
        }
        return sdf.format(date);
    }

    public static String getStringDateFilename(Date date){
        SimpleDateFormat sdf;
        if (check00(date)){
            sdf = new SimpleDateFormat(FileDateFormat_);
        } else {
            sdf = new SimpleDateFormat(FileDateFormat);
        }
        return sdf.format(date);
    }

    public static String getStringDateFilename2(Date date){
        SimpleDateFormat sdf;
        if (check00(date)){
            sdf = new SimpleDateFormat(FileDateFormat2_);
        } else {
            sdf = new SimpleDateFormat(FileDateFormat2);
        }
        return sdf.format(date);
    }

    public static Date getDate(String dateFormat) {
        DateFormat df = new SimpleDateFormat(simpleDateFormat);
        Date date = null;
        try {
            date = df.parse(dateFormat);
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }
}

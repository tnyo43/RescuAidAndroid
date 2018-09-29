package com.example.haruka.rescue_aid.utils;

import java.io.Serializable;
import java.util.Date;

/**
 * 回答時刻と質問のtagと回答のセット
 */
public class Record implements Serializable {

    private static final long serialVersionUID = 2L;

    String time;
    String tag;
    String value;

    public Record(String time, String tag, String value){
        this.time = time;
        this.tag = tag;
        this.value = value;
    }

    public Record(String tag, String value){
        time = QADateFormat.getInstance();
        this.tag = tag;
        this.value = value;
    }

    public Record(String line){
        String[] array = line.split(",");
        this.time = array[0];
        this.tag = array[1];
        this.value = "";
    }

    public Record(Date statAt, String line){
        String[] array = line.split(",");
        Date date = new Date(statAt.getTime() + 1000*Integer.parseInt(array[0]));
        time = QADateFormat.getStringDate(date);
        this.tag = array[1];
        this.value = array[2];
    }

    public String getTime(){
        return time;
    }

    public String getTagValue(){
        return tag + "," + value;
    }

    public String getTag(){
        return tag;
    }

    public String getValue(){
        return value;
    }

    public String toString(){
        return time + "," + tag + "," + value;
    }
}


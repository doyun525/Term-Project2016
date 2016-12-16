package com.example.doyun.mylifelogger.DB;

import java.io.Serializable;

/**
 * Created by doyun on 2016-12-06.
 */

public class TimeObject implements Serializable {
    String date;
    String time;
    int year, month, day;
    int hour, min;

    TimeObject(String date, String time){
        this.date = date;
        this.time = time;
    }
    void s2intTime(String date, String time){
        String d[] = date.split("-");
        String t[] = time.split(":");
        year=Integer.parseInt(d[0],10);
        month=Integer.parseInt(d[1],10);
        day=Integer.parseInt(d[2],10);
        hour=Integer.parseInt(t[0],10);
        min = Integer.parseInt(t[1],10);
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }
}

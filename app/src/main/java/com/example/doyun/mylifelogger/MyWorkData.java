package com.example.doyun.mylifelogger;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by doyun on 2016-12-06.
 */

public class MyWorkData {

    private Time StartTime;
    private Time EndTime;

    private LatLng StartLocation;
    private LatLng EndLocation;

    private String workType;
    private String detail;

    public class Time{
        String date;
        String time;
        int year, month, day;
        int hour, min;
        Time(String date, String time){
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
    }

    MyWorkData(String Startdate, String Starttime, String workType){
        StartTime = new Time(Startdate, Starttime);
        this.workType = workType;
    }
    MyWorkData(String Startdate, String Starttime, String workType, String detail){
        StartTime = new Time(Startdate, Starttime);
        this.workType = workType;
        setDetail(detail);
    }
    public void setDetail(String detail){
        this.detail = detail;
    }
    public void setEndTime(String Endate, String Endtime){
        EndTime = new Time(Endate, Endtime);
    }

    public Time getStartTime() {
        return StartTime;
    }

    public Time getEndTime() {
        return EndTime;
    }

    public String getWorkType() {
        return workType;
    }

    public String getDetail() {
        return detail;
    }

}

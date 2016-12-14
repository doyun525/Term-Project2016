package com.example.doyun.mylifelogger.DB;

import android.graphics.Bitmap;
import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by doyun on 2016-12-06.
 */

public class MyWorkData implements Serializable {

    private TimeObject StartTime;
    private TimeObject EndTime;

    private Location location;
    private List<Location> locatinList;

    private String workType;
    private String content=null;
    private String place=null;

    private Bitmap image=null;

    public MyWorkData(){

    }
    public MyWorkData(String Startdate, String Starttime, String workType, Location latLng){
        set(Startdate, Starttime, workType, latLng);
    }
    public MyWorkData(String Startdate, String Starttime, String workType, String place ,String content, Location latLng){
        set(Startdate, Starttime, workType, latLng);
        this.place = place;
        setContent(content);
    }
    public void set(String Startdate, String Starttime, String workType, Location latLng){
        StartTime = new TimeObject(Startdate, Starttime);
        this.workType = workType;
        location = latLng;
    }

    public void addLocationItem(Location location){
        if(locatinList == null) locatinList = new ArrayList<Location>();
        locatinList.add(location);
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setStartTime(String startdate, String starttime) {
        StartTime = new TimeObject(startdate, starttime);
    }

    public Location getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }

    public String getPlace() {
        return place;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setEndTime(String Endate, String Endtime){
        EndTime = new TimeObject(Endate, Endtime);
    }

    public TimeObject getStartTime() {
        return StartTime;
    }

    public TimeObject getEndTime() {
        return EndTime;
    }

    public String getWorkType() {
        return workType;
    }

    public String getDetail() {
        return content;
    }

}

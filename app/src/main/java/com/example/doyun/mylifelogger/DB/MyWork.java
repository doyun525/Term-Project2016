package com.example.doyun.mylifelogger.DB;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by doyun on 2016-12-06.
 */

public class MyWork extends MyData implements Serializable, Parcelable {

    private TimeObject EndTime;

    private List<Location> locatinList = new ArrayList<Location>();

    public MyWork(){
    }
    public MyWork(String Startdate, String Starttime, String workType, Location latLng){
        set(Startdate, Starttime, workType, latLng);
    }
    public MyWork(String Startdate, String Starttime, String workType, String place , String content, Location latLng){
        set(Startdate, Starttime, workType, latLng);
        this.place = place;
        setContent(content);
    }


    protected MyWork(Parcel in) {
        super(in);
        locatinList = in.createTypedArrayList(Location.CREATOR);
    }

    public static final Creator<MyWork> CREATOR = new Creator<MyWork>() {
        @Override
        public MyWork createFromParcel(Parcel in) {
            return new MyWork(in);
        }

        @Override
        public MyWork[] newArray(int size) {
            return new MyWork[size];
        }
    };

    public void set(String Startdate, String Starttime, String workType, Location latLng){
        date = new TimeObject(Startdate, Starttime);
        this.name = workType;
        location = latLng;
    }

    public List<Location> getLocatinList() {
        return locatinList;
    }

    public void addLocationItem(Location location){
        if(locatinList == null) locatinList = new ArrayList<Location>();
        locatinList.add(location);
    }

    public void setStartTime(String startdate, String starttime) {
        date = new TimeObject(startdate, starttime);
    }



    public void setEndTime(String Endate, String Endtime){
        EndTime = new TimeObject(Endate, Endtime);
    }

    public TimeObject getStartTime() {
        return date;
    }

    public TimeObject getEndTime() {
        return EndTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(content);
        dest.writeString(place);
        dest.writeString(image);
        dest.writeParcelable(location, flags);
        dest.writeTypedList(locatinList);
    }

}

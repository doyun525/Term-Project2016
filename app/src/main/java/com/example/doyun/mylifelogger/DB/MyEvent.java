package com.example.doyun.mylifelogger.DB;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by doyun on 2016-11-13.
 */

public class MyEvent implements Serializable{
    TimeObject date;
    String name;
    String place;
    String content;
    Location location;
    Bitmap image;

    public MyEvent(String date, String time, String name, String place, String content, Location location){
        this.date =  new TimeObject(date, time);
        this.name = name;
        this.place = place;
        this.content = content;
        this.location = location;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public TimeObject getDate() {
        return date;
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

    public String getName() {
        return name;
    }
}

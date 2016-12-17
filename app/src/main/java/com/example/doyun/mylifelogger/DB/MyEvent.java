package com.example.doyun.mylifelogger.DB;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by doyun on 2016-11-13.
 */

public class MyEvent extends MyData implements Serializable, Parcelable {




    public MyEvent(String date, String time, String name, String place, String content, Location location){
        this.date =  new TimeObject(date, time);
        this.name = name;
        this.place = place;
        this.content = content;
        this.location = location;

    }


    protected MyEvent(Parcel in) {
        super(in);
    }

    public static final Creator<MyEvent> CREATOR = new Creator<MyEvent>() {
        @Override
        public MyEvent createFromParcel(Parcel in) {
            return new MyEvent(in);
        }

        @Override
        public MyEvent[] newArray(int size) {
            return new MyEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
    }


}

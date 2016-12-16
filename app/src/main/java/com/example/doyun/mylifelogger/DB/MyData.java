package com.example.doyun.mylifelogger.DB;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by doyun on 2016-12-16.
 */

public class MyData implements Serializable, Parcelable{

    protected TimeObject date;

    protected String name;

    protected String content=null;
    protected String place=null;

    protected String image=null;

    protected Location location=null;

    protected MyData(Parcel in) {
        name = in.readString();
        content = in.readString();
        place = in.readString();
        image = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<MyData> CREATOR = new Creator<MyData>() {
        @Override
        public MyData createFromParcel(Parcel in) {
            return new MyData(in);
        }

        @Override
        public MyData[] newArray(int size) {
            return new MyData[size];
        }
    };

    public MyData() {

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
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
    }
}

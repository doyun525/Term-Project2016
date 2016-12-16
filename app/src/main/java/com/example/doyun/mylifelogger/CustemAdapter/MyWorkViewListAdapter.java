package com.example.doyun.mylifelogger.CustemAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doyun.mylifelogger.DB.MyData;
import com.example.doyun.mylifelogger.DB.MyEvent;
import com.example.doyun.mylifelogger.DB.MyWork;
import com.example.doyun.mylifelogger.R;
import com.example.doyun.mylifelogger.TabFragments.ViewdayFragment;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.util.ArrayList;

/**
 * Created by doyun on 2016-12-16.
 */

public class MyWorkViewListAdapter extends BaseAdapter{
    Context mContext = null;
    ArrayList<MyData> mData = null;
    LayoutInflater mLayoutInflater = null;
    public int selected_position;

    public MyWorkViewListAdapter(Context context, ArrayList mData){
        mContext = context;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.item_view_day, null);

        TextView name = (TextView)v.findViewById(R.id.name);
        TextView time = (TextView)v.findViewById(R.id.time);

        Object orj = mData.get(position);
        if(orj instanceof MyWork){
            name.setText(((MyWork) orj).getName());
            time.setText(((MyWork) orj).getStartTime().getTime()+" ~ "+((MyWork) orj).getEndTime().getTime());
        }
        else if(orj instanceof MyEvent){
            name.setText(((MyEvent) orj).getName());
            time.setText(((MyEvent) orj).getDate().getTime());
        }


        return v;
    }


}

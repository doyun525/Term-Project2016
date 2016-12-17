package com.example.doyun.mylifelogger;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.doyun.mylifelogger.DB.DBHelper;
import com.example.doyun.mylifelogger.DB.MyWork;
import com.example.doyun.mylifelogger.TabFragments.SelectWorkFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogRecord;


public class gpsService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final int START_AUTO_SAVE_LOCATION = 0;
    public static final int END_AUTO_SAVE_LOCATION = 1;
    public static final int STATE_MOVE = 2;

    Location lastedLocation;

    Intent intent;

    int LocationCheckCycle;
    long lastedTime;

    DBHelper dbHelper;
    SQLiteDatabase db;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public boolean autoMoveSave;
    public boolean Moveing;

    private MyWork myWork;

    private final Messenger messenger = new Messenger(new IncomingHandler());
    private Messenger FragmentMessenger;

    boolean check = false;

    public gpsService() {

    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();;
            bundle.setClassLoader(getClassLoader());
            switch (msg.what){
                case SelectWorkFragment.CONNECT_FRAGMENT:{
                    FragmentMessenger = msg.replyTo;
                    Log.d("test", "Fragment메신저 : "+FragmentMessenger);
                    if(!mGoogleApiClient.isConnected())
                        mGoogleApiClient.connect();
                    sendtoFragment(STATE_MOVE);
                    check =true;
                    break;
                }
                case SelectWorkFragment.SET_MOVEING:{
                    Log.d("test","state : "+msg.arg1);
                    switch (msg.arg1){
                        case SelectWorkFragment.START_MOVEING:{
                            Log.d("test", "start moving : "+SelectWorkFragment.START_MOVEING);
                            myWork = (MyWork) bundle.get("mywork");
                            Log.d("test","mywork : "+myWork);

                            lastedTime = System.currentTimeMillis();
                            if(!Moveing){
                                SaveStart();
                                Moveing=true;
                            }
                            Moveing = true;
                            break;
                        }
                        case SelectWorkFragment.END_MOVEING:{
                            Moveing=false;
                            sendtoFragment(END_AUTO_SAVE_LOCATION);
                            SaveEnd();
                            break;
                        }
                    }
                    break;
                }
                case SelectWorkFragment.SET_WORK:{
                    MyWork mm = (MyWork) bundle.get("mywork");
                    myWork.setImage(mm.getImage());
                    myWork.setContent(mm.getContent());
                    break;
                }
                case 10:{
                    Log.d("test", "fragment에서 받음 : "+msg.getData().getSerializable("1") + ", "+msg.arg1);
                    break;
                }
            }
        }
    }
    private void sendtoFragment(int state){
        Message msg1 = Message.obtain(null, state);
        Bundle bundle1 = new Bundle();
        if(myWork!=null)
            bundle1.putSerializable("mywork", myWork);
        bundle1.putBoolean("moving",Moveing);
        msg1.setData(bundle1);

        try {
            FragmentMessenger.send(msg1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.d("test", "service onBind");
        this.intent = intent;

        // TODO: Return the communication channel to the service.
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("test", "service onUnbind");
        FragmentMessenger = null;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "service onStartCommand : "+intent);
        this.intent = intent;


        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("test", "service onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {

        Log.d("test", "service ondestroy");
        super.onDestroy();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onCreate() {

        Log.d("test", "service onCreate");
        super.onCreate();
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }

       // startForeground(1, new Notification());

        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = (new SimpleDateFormat("yyyy-MM").format(d));
        dbHelper = new DBHelper(this, date + ".db");

        db = dbHelper.getWritableDatabase();

        //mGoogleApiClient.connect();

        autoMoveSave =true;
        Moveing=false;

        LocationCheckCycle = 1000*60;
        lastedTime = System.currentTimeMillis();


    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("test","googleapi 연결됨");
        Message msg1 = Message.obtain(null, 10);
        if(FragmentMessenger!=null) {
            //msg1.obj = "서비스에서 보냄";
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("1", "서비스에서 보냄");
            msg1.setData(bundle1);
            msg1.arg1 = 111;
            try {
                FragmentMessenger.send(msg1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LocationCheckCycle/2);
        mLocationRequest.setFastestInterval(LocationCheckCycle/4);

        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        }
        startLocationUpdates();
        lastedLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);

            if (locationAvailability.isLocationAvailable()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                Toast.makeText(this, "Location Unavialable", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void SaveStart(){
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
        String time = (new SimpleDateFormat("HH:mm").format(d));

        myWork.setStartTime(date,time);

        //save db

        Gson gson = new Gson();
        String json = gson.toJson(myWork);

        Log.d("test", "save start mywork : "+myWork+ "time : "+myWork.getStartTime().getTime());
        ContentValues values = new ContentValues();
        values.put("date", myWork.getStartTime().getDate());
        values.put("time", myWork.getStartTime().getTime());
        values.put("mywork", json);

        db.insert("work", null, values);
    }
    public void SaveEnd(){
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
        String time = (new SimpleDateFormat("HH:mm").format(d));
        myWork.setEndTime(date, time);
        //save db
        Gson gson = new Gson();
        String json = gson.toJson(myWork);

        Log.d("test", "save end mywork : "+myWork+ "time : "+myWork.getStartTime().getTime());
        ContentValues values = new ContentValues();
        values.put("mywork", json);

        db.update("work", values, "date =? and time = ?", new String[]{myWork.getStartTime().getDate(), myWork.getStartTime().getTime()});
    }

    @Override
    public void onLocationChanged(Location location) {
        double distance = 0;
        long nowtime = System.currentTimeMillis();

        Log.d("test", "로케이션 체인징: " + location.getAccuracy());
        if(FragmentMessenger!=null) {
            Message msg1 = Message.obtain(null, 10);

            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("1", "서비스에서 보냄");
            msg1.setData(bundle1);
            msg1.arg1 = 111;
            try {
                FragmentMessenger.send(msg1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if(location.getAccuracy()<=15) {

            if((lastedLocation.distanceTo(location)) > 20) {
                if (!Moveing) {
                    if (autoMoveSave) {
                        Calendar calendar = Calendar.getInstance();
                        Date d = calendar.getTime();
                        String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
                        String time = (new SimpleDateFormat("HH:mm").format(d));

                        myWork = new MyWork(date, time, "이동", location);
                        lastedTime = nowtime;
                        lastedLocation = location;
                        Moveing = true;
                        if(FragmentMessenger!=null)
                            sendtoFragment(START_AUTO_SAVE_LOCATION);
                        SaveStart();
                        Log.d("test", "이동 자동 저장 시작 location : "+location + "  "+myWork);


                        return;
                    }
                    else return;
                }
                else {
                    myWork.addLocationItem(location);
                    lastedTime = nowtime;
                    lastedLocation = location;

                    Log.d("test", "이동 자동 저장 location : "+location + "  "+myWork);
                    return;
                }
            }else {
                if(!Moveing) return;
                if(autoMoveSave &&  nowtime -lastedTime>=LocationCheckCycle*10){
                    if(FragmentMessenger!=null)
                        sendtoFragment(END_AUTO_SAVE_LOCATION);
                    SaveEnd();
                    Log.d("test", "이동 자동 저장 종료 location : "+location + "  "+myWork);
                    myWork=null;
                    Moveing=false;
                }

            }
        }
    }
}

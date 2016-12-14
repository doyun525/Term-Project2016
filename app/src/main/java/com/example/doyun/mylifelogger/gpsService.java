package com.example.doyun.mylifelogger;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.doyun.mylifelogger.DB.DBHelper;
import com.example.doyun.mylifelogger.DB.MyWorkData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class gpsService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    Location lastedLocation;

    int LocationCheckCycle;
    long lastedTime;

    DBHelper dbHelper;
    SQLiteDatabase db;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public Boolean autoMoveSave;
    public Boolean Moveing;

    private MyWorkData myWorkData;

    public gpsService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!Moveing) {
            myWorkData = (MyWorkData) intent.getSerializableExtra("mywork");

            lastedLocation = myWorkData.getLocation();
        }

        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        else {
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }




    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LocationCheckCycle/2);
        mLocationRequest.setFastestInterval(LocationCheckCycle/4);

        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        }
        startLocationUpdates();
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

    @Override
    public void onLocationChanged(Location location) {
        double distance = 0;
        long time = System.currentTimeMillis();

        if(autoMoveSave){
            if(!Moveing){

            }
            else {

            }
        }


        if(location.getAccuracy()<=20 && time -lastedTime>=LocationCheckCycle) {
            if (lastedLocation == null) {
                lastedLocation = new Location("");
                lastedLocation.setLatitude(location.getLatitude());
                lastedLocation.setLongitude(location.getLongitude());

            } else if ((distance = lastedLocation.distanceTo(location)) > 10) {

                lastedLocation.setLatitude(location.getLatitude());
                lastedLocation.setLongitude(location.getLongitude());
            }
            Log.d("test", "LocationCheckCycle " + LocationCheckCycle);
        }
    }
}

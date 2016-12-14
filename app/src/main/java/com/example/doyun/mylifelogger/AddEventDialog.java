package com.example.doyun.mylifelogger;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.doyun.mylifelogger.DB.MyEvent;
import com.example.doyun.mylifelogger.DB.MyWorkData;
import com.example.doyun.mylifelogger.TabFragments.SelectWorkFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by doyun on 2016-12-06.
 */

public class AddEventDialog extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private static final int GET_LOCATION = 3;

    public static final int CLICK_OK =1;
    public static final int CLICK_CANCLE =2;

    public EditText title;
    public EditText place;
    public EditText content;

    private int mode;

    public Button setLocation;
    public Button edit_date;
    public Button edit_time;
    public Button ok;
    public Button cancle;
    public Button addIamge;

    ImageView imageView;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    Calendar calendar = Calendar.getInstance();

    Date d;
    String date;
    String time;

    MyEvent myEvent;

    MyWorkData myWorkData;

    Bitmap photo;

    public Location location;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    Uri mImageCaptureUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == CLICK_OK) {
            switch (requestCode) {
                case GET_LOCATION: {
                    Log.d("test", "Intent : " + data);
                    Location location1 = (Location) data.getParcelableExtra("location");
                    Log.d("test", "location1 : " + location1);
                    if (location1 != null) location = location1;
                    break;
                }
                case PICK_FROM_ALBUM: {

                }
                case PICK_FROM_CAMERA: {
                    mImageCaptureUri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(mImageCaptureUri, "image/*");

                    intent.putExtra("outputX", 200).putExtra("outputY", 200).putExtra("aspectX", 1)
                            .putExtra("aspextY", 1).putExtra("scale", true).putExtra("return-data", true);
                    startActivityForResult(intent, CROP_FROM_IMAGE);
                    break;
                }
                case CROP_FROM_IMAGE: {
                    if (resultCode != RESULT_OK) return;

                    final Bundle extras = data.getExtras();

                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel/" + System.currentTimeMillis() + ".jpg";

                    if (extras != null) {
                        photo = extras.getParcelable("data");
                        imageView.setImageBitmap(photo);

                        break;
                    }

                    File f = new File(mImageCaptureUri.getPath());
                    //if (f.exists()) f.delete();
                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("test", "add start");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("test", "add stop");
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("test", "add destroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }


        final Intent intent = getIntent();
        mode = intent.getIntExtra("mode",0);



        d = calendar.getTime();
        date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
        time = (new SimpleDateFormat("HH:mm").format(d));

        title = (EditText)findViewById(R.id.event_title);
        place = (EditText)findViewById(R.id.event_place);
        content = (EditText)findViewById(R.id.event_content);

        edit_date = (Button)findViewById(R.id.event_date);
        edit_time = (Button)findViewById(R.id.event_time);
        setLocation = (Button)findViewById(R.id.setlocation);
        ok = (Button)findViewById(R.id.Ok) ;
        cancle = (Button)findViewById(R.id.cancle);
        addIamge = (Button)findViewById(R.id.addImage);

        imageView = (ImageView)findViewById(R.id.imageView);

        photo = null;

        edit_date.setText(date);
        edit_time.setText(time);

        title.setText(intent.getStringExtra("workType"));

        if(mode == SelectWorkFragment.ADD_MODE_EVENT){
            setTitle("사건추가");
        }
        else if(mode==SelectWorkFragment.ADD_MODE_WORK){
            setTitle("내용 추가");
            title.setFocusable(false);
            edit_time.setVisibility(View.GONE);
            edit_date.setVisibility(View.GONE);
            myWorkData = (MyWorkData) intent.getSerializableExtra("mywork");
            if(myWorkData != null){
                title.setText(myWorkData.getWorkType());
                place.setText(myWorkData.getPlace());
                content.setText(myWorkData.getContent());
                if(myWorkData.getImage()!=null) {
                    imageView.setImageBitmap(myWorkData.getImage());

                    addIamge.setText("사진 변경");
                }
            }
        }





        datePickerDialog = new DatePickerDialog(this, mEventDataSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);

        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        edit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });





        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEventDialog.this, MapPopupActivity.class);
                startActivityForResult(intent, GET_LOCATION);

            }
        });

        addIamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddEventDialog.this);

                alert.setTitle("사진선택");
                alert.setNeutralButton("앨범 선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent();
                        // Gallery 호출
                        intent1.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent1.setAction(Intent.ACTION_PICK);


                        startActivityForResult(intent1, PICK_FROM_ALBUM);
                    }
                });
                alert.setNegativeButton("사진 촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

                        startActivityForResult(intent1, PICK_FROM_CAMERA);
                    }
                });
                alert.setPositiveButton("취소", null);

                alert.create().show();
            }
        });


        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = title.getText().toString();
                String s_place = place.getText().toString();
                String s_content = content.getText().toString();

                if(mode == SelectWorkFragment.ADD_MODE_EVENT){
                    Log.d("test", "사건 추가");
                    myEvent = new MyEvent(date, time, name, s_place, s_content, location);
                    if(photo != null){
                        myEvent.setImage(photo);
                    }
                    intent.putExtra("event", myEvent);
                }
                else if(mode == SelectWorkFragment.ADD_MODE_WORK){
                    Log.d("test","상세추가 : "+location);
                    String workType = intent.getStringExtra("workType");

                    myWorkData = new MyWorkData(date, time, workType, s_place, s_content, location);
                    if(photo != null){
                        myWorkData.setImage(photo);
                    }
                    intent.putExtra("work", myWorkData);

                }
                setResult(CLICK_OK, intent);
                finish();
                onDestroy();
            }

        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(CLICK_CANCLE, intent);
                finish();
                onDestroy();
            }
        });

    }


    private  TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(hourOfDay, minute);

            time = (new SimpleDateFormat("HH:mm").format(calendar.getTime()));
            edit_time.setText(time);
        }
    };

    private DatePickerDialog.OnDateSetListener mEventDataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yaerSelect, int monthOfyear, int dayOfmonth) {
            calendar.set(yaerSelect, monthOfyear, dayOfmonth);

            date =(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
            edit_date.setText(date);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // return super.onTouchEvent(event);
        return  false;
    }

    public Object returnObject(Location latLng){
        String name = title.getText().toString();
        String s_place = place.getText().toString();
        String s_content = content.getText().toString();

        myEvent = new MyEvent(date, time, name, s_place, s_content, latLng);

        return myEvent;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치정보 수신 불가", Toast.LENGTH_LONG).show();
            finish();
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Log.d("test", ""+location);
        if(location == null){
            Log.d("test", "location null");
            Toast.makeText(this, "위치정보 수신 불가", Toast.LENGTH_LONG).show();
            //finish();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

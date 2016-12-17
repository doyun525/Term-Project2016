package com.example.doyun.mylifelogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.doyun.mylifelogger.DB.MyEvent;
import com.example.doyun.mylifelogger.DB.MyWork;
import com.example.doyun.mylifelogger.TabFragments.SelectWorkFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private int mYear, mMonth, mDay;

    MyEvent myEvent;

    MyWork myWork;

    String workType;

    Bitmap photo;

    public Location location;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    Uri mImageCaptureUri;

    AddEventDialog(){
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        date = (new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        time = (new SimpleDateFormat("HH:mm").format(calendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case GET_LOCATION: {
                    if(resultCode!=CLICK_OK) break;
                    Log.d("test", "Intent : " + data);
                    Location location1 = (Location) data.getParcelableExtra("location");
                    Log.d("test", "location1 : " + location1);
                    if (location1 != null) location = location1;
                    Log.d("test", "location : " + location);
                    break;
                }
                case PICK_FROM_ALBUM: {

                }
                case PICK_FROM_CAMERA: {
                    mImageCaptureUri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(mImageCaptureUri, "image/*");
                    try {
                        photo = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageCaptureUri);
                        imageView.setImageBitmap(photo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("outputX", 200).putExtra("outputY", 200).putExtra("aspectX", 1)
                            .putExtra("aspextY", 1).putExtra("scale", true).putExtra("return-data", true);
                    //startActivityForResult(intent, CROP_FROM_IMAGE);
                    break;
                }
                case CROP_FROM_IMAGE:{
                    Log.d("test", "이미지 수정");
                    if (resultCode != RESULT_OK) return;

                    final Bundle extras = data.getExtras();

                    if (extras != null) {
                        photo = extras.getParcelable("data");
                        imageView.setImageBitmap(photo);
                        Log.d("test", "이미지 : " + photo);
                        break;
                    }

                    File f = new File(mImageCaptureUri.getPath());
                    //if (f.exists()) f.delete();
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
            myWork = intent.getParcelableExtra("mywork");
            workType = intent.getStringExtra("workType");
            Log.d("test", "workType:"+workType);
            title.setText(workType);
            if(myWork != null){

                place.setText(myWork.getPlace());
                content.setText(myWork.getContent());
                if(myWork.getImage()!=null) {
                    String imgPath = myWork.getImage();

                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);

                    imageView.setImageBitmap(bitmap);

                    addIamge.setText("사진 변경");
                }
                if(workType=="이동") {
                    place.setVisibility(View.GONE);
                    setLocation.setVisibility(View.GONE);
                }
            }
        }





        datePickerDialog = new DatePickerDialog(this, mEventDataSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE) ,false);


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

                Calendar c = Calendar.getInstance();
                Date d = c.getTime();
                String date1 = (new SimpleDateFormat("yyyy-MM-dd").format(d));

                Intent in = new Intent();

                String Path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"MyLifeLogger/image/" + System.currentTimeMillis()+".jpg";

                if(photo!=null){
                    storeCropImage(photo, Path);
                }

                if(mode == SelectWorkFragment.ADD_MODE_EVENT){
                    Log.d("test", "사건 추가");
                    myEvent = new MyEvent(date, time, name, s_place, s_content, location);
                    if(photo != null){
                        myEvent.setImage(Path);
                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(myEvent);
                    in.putExtra("event", json);
                }
                else if(mode == SelectWorkFragment.ADD_MODE_WORK){
                    Log.d("test","상세추가 : "+location);
                    String workType = intent.getStringExtra("workType");
                    Log.d("test", "myWork : " + myWork);
                    if(myWork==null) {

                        myWork = new MyWork(date, time, workType, s_place, s_content, location);
                    }
                    else {
                        myWork.setStartTime(date, time);
                        myWork.setContent(s_content);
                        Log.d("test", "mywork date ; "+myWork.getStartTime().getDate());
                        if (!workType.equals("이동")) {
                            Log.d("test", "이동아님");
                            myWork.setLocation(location);
                            myWork.setPlace(s_place);
                        }
                    }

                    if(photo != null){
                        myWork.setImage(Path);
                    }
                    Log.d("test", "mywork : "+myWork + " location : "+myWork.getLocation());
                    Gson gson = new Gson();
                    String json = gson.toJson(myWork);

                    in.putExtra("mywork", (Parcelable) myWork);

                }
                setResult(CLICK_OK, in);
                finish();
                //onDestroy();
            }

        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(CLICK_CANCLE, intent);
                finish();
                //onDestroy();
            }
        });

    }

    private void storeCropImage(Bitmap bitmap, String filePath){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"MyLifeLogger/image/";
        File directory = new File(dirPath);

        if(!directory.exists()) directory.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private  TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(mYear, mMonth, mDay, hourOfDay, minute);

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

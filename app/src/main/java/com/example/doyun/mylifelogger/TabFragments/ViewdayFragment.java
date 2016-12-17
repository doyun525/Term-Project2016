package com.example.doyun.mylifelogger.TabFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doyun.mylifelogger.CustemAdapter.MyWorkViewListAdapter;
import com.example.doyun.mylifelogger.DB.DBHelper;
import com.example.doyun.mylifelogger.DB.MyData;
import com.example.doyun.mylifelogger.DB.MyEvent;
import com.example.doyun.mylifelogger.DB.MyWork;
import com.example.doyun.mylifelogger.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewdayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewdayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewdayFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int mYear, mMonth, mDay;

    public GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private DatePickerDialog datePickerDialog;

    private Calendar c;
    private Date d;

    private String date;

    private ArrayList<MyData> DataList;
    private ArrayList<Marker> WorkMarkerList;
    private ArrayList<Marker> EventMarkerList;

    DBHelper dbHelper;
    SQLiteDatabase db;

    Button nextday;
    Button previousday;
    TextView viewDate;
    ListView listwork;

    public ViewdayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewdayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewdayFragment newInstance(String param1, String param2) {
        ViewdayFragment fragment = new ViewdayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("test","onStart");
        if(mMap!=null) setMap();
    }

    @Override
    public void onResume() {
        Log.d("test","onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("test","onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //db.close();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser) {
            if(mMap!=null && getActivity()!=null){
                setMap();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        d = c.getTime();
        date = (new SimpleDateFormat("yyyy-MM-dd").format(d));


    }

    public String dateChange(Calendar c, int i) {
        c.add(c.DATE, i);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        d = c.getTime();
        return (new SimpleDateFormat("yyyy-MM-dd").format(d));
    }

    public void LoadData() {
        if (db != null) db.close();
        d = c.getTime();
        String date1 = (new SimpleDateFormat("yyyy-MM").format(d));

        dbHelper = new DBHelper(getActivity(), date1 + ".db");
        Log.d("test", "date : " + date1 + " dbh : " + dbHelper+" getatctivity :" +getActivity());

            db = dbHelper.getWritableDatabase();



        Gson gson = new Gson();

        String sbwork = "SELECT * from " + DBHelper.TABLE_WORK + " WHERE " + DBHelper.COLUMN_NAME_DATE + "=?";
        String sbevent = "SELECT * from " + DBHelper.TABLE_EVENT + " WHERE " + DBHelper.COLUMN_NAME_DATE + "=?";
        Cursor cursorWork = db.rawQuery(sbwork, new String[]{date});
        Cursor cursorEvent = db.rawQuery(sbevent, new String[]{date});

        List<MyData> arrayList = new ArrayList<MyData>();

        while (cursorWork.moveToNext()) {
            String json = cursorWork.getString(3);
            MyWork myWork = gson.fromJson(json, MyWork.class);
            Log.d("test", "mywork : " + myWork.getStartTime().getTime());
            arrayList.add(myWork);
        }
        while (cursorEvent.moveToNext()) {
            String json = cursorEvent.getString(3);
            MyEvent myEvent = gson.fromJson(json, MyEvent.class);
            Log.d("test", "myevent : " + myEvent.getDate().getTime());
            arrayList.add(myEvent);
        }
        Log.d("test", "list : " + arrayList);

        if (arrayList.size() > 0) {
            Comparator<MyData> myComparator = new Comparator<MyData>() {
                private final Collator collator = Collator.getInstance();

                @Override
                public int compare(MyData o1, MyData o2) {
                    //return collator.compare(o1.getDate().getTime().toString(), o2.getDate().getTime().toString());
                    int o1time = o1.getDate().getHour() * 100 + o1.getDate().getMin();
                    int o2time = o2.getDate().getHour() * 100 + o2.getDate().getMin();
                    //return o1.getDate().getTime().compareToIgnoreCase(o2.getDate().getTime());
                    return (o1time < o2time) ? -1 : (o1time > o2time) ? 1 : 0;
                }
            };

            Collections.sort(arrayList, myComparator);
        }
        DataList = (ArrayList<MyData>) arrayList;

        db.close();

    }

    public void setMap() {
        mMap.clear();

        LoadData();
        setList();

        MyData myData;
        LatLng latLng = null;
        WorkMarkerList = new ArrayList<Marker>();
        for (int i = 0; i < DataList.size(); i++) {
            myData = DataList.get(i);
            Log.d("tset", "location: "+myData.getLocation());
            latLng = new LatLng(myData.getLocation().getLatitude(), myData.getLocation().getLongitude());

            if (myData instanceof MyWork) {
                WorkMarkerList.add(mMap.addMarker(new MarkerOptions().position(latLng).snippet("" + i)));
                if (myData.getName().equals("이동")) {
                    List<Location> movelist = ((MyWork) myData).getLocatinList();
                    LatLng latLng1 = latLng;
                    for (int j = 0; j < movelist.size(); j++) {
                        LatLng latLng2 = new LatLng(movelist.get(j).getLatitude(), movelist.get(j).getLongitude());
                        PolylineOptions rectOptions = new PolylineOptions().add(latLng1, latLng2).clickable(true).width(15).color(Color.RED);
                        mMap.addPolyline(rectOptions);
                        if (j == movelist.size() - 1){
                            WorkMarkerList.add(mMap.addMarker(new MarkerOptions().position(latLng2).snippet("" + i)));
                        }
                        else mMap.addCircle(new CircleOptions().center(latLng2).radius(1).strokeColor(Color.RED).fillColor(Color.RED));
                        latLng1 = latLng2;
                    }
                }
            } else if (myData instanceof MyEvent) {
                WorkMarkerList.add(mMap.addMarker(new MarkerOptions().position(latLng).snippet("" + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
            }
        }
        if (latLng != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20), 300, null);

    }

    public void setList() {
        MyWorkViewListAdapter adapter = new MyWorkViewListAdapter(getActivity(), DataList);
        listwork.setAdapter(adapter);
        listwork.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyData orj = (MyData) parent.getItemAtPosition(position);
                showAlert(orj);
            }
        });
    }

    public void showAlert(MyData orj){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_event, null);

        String name, time, place, content;
        String imgPath;

        TextView textName = (TextView)v.findViewById(R.id.view_title);
        TextView textTime = (TextView)v.findViewById(R.id.view_time);
        TextView textPlace = (TextView)v.findViewById(R.id.view_place);
        TextView textContent = (TextView)v.findViewById(R.id.view_content);
        ImageView imageView = (ImageView)v.findViewById(R.id.view_image);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(v);
        alert.setPositiveButton("닫기",null);

        name = orj.getName();
        place =  orj.getPlace();
        content = orj.getContent();
        imgPath = orj.getImage();
        time = orj.getDate().getTime();
        if(orj instanceof MyWork)
            time = ((MyWork) orj).getStartTime().getTime()+" ~ "+((MyWork) orj).getEndTime().getTime();

        textName.setText(name);
        textTime.setText(time);
        textPlace.setText(place);
        textContent.setText(content);
        Log.d("test", "image set : "+imageView);
        if(imgPath==null) imageView.setVisibility(View.GONE);
        else {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            imageView.setImageBitmap(bitmap);
        }




        LatLng latlng = new LatLng(orj.getLocation().getLatitude(),orj.getLocation().getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng), 300, null);

        alert.create().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viewday, container, false);

        viewDate = (TextView) view.findViewById(R.id.textView);
        listwork = (ListView) view.findViewById(R.id.listwork);

        nextday = (Button) view.findViewById(R.id.nextday);
        previousday = (Button) view.findViewById(R.id.previousday);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        return view;
    }

    private DatePickerDialog.OnDateSetListener mDataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yaerSelect, int monthOfyear, int dayOfmonth) {
            c.set(yaerSelect, monthOfyear, dayOfmonth);
            mYear = yaerSelect;
            mMonth = monthOfyear + 1;
            mDay = dayOfmonth;
            d = c.getTime();
            date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
            viewDate.setText(date);
            mMap.clear();
            setMap();
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        datePickerDialog = new DatePickerDialog(getActivity(), mDataSetListener, mYear, mMonth - 1, mDay);

        viewDate.setText(date);

        viewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        nextday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = dateChange(c, 1);
                viewDate.setText(date);
                datePickerDialog = new DatePickerDialog(getActivity(), mDataSetListener, mYear, mMonth - 1, mDay);
                setMap();
            }
        });
        previousday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = dateChange(c, -1);
                viewDate.setText(date);
                datePickerDialog = new DatePickerDialog(getActivity(), mDataSetListener, mYear, mMonth - 1, mDay);
                setMap();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        setMap();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MyData mydata = DataList.get(Integer.parseInt(marker.getSnippet()));
                showAlert(mydata);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, null);
                return true;
            }
        });
    }
// TODO: Rename method, update argument and hook method into UI event

    /*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


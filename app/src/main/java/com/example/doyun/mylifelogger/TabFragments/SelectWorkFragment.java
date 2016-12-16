package com.example.doyun.mylifelogger.TabFragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.doyun.mylifelogger.AddEventDialog;
import com.example.doyun.mylifelogger.DB.DBHelper;
import com.example.doyun.mylifelogger.DB.MyEvent;
import com.example.doyun.mylifelogger.DB.MyWork;
import com.example.doyun.mylifelogger.R;
import com.example.doyun.mylifelogger.gpsService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectWorkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectWorkFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int ADD_MODE_EVENT = 0;
    public static final int ADD_MODE_WORK = 1;

    public static final int CONNECT_FRAGMENT = 0;
    public static final int SET_MOVEING = 1;
    public static final int START_MOVEING = 2;
    public static final int END_MOVEING = 3;
    public static final int SET_WORK = 4;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Boolean move = null;

    private String[] defaultWorkList = {"업무", "공부", "게임", "과제", "운동", "이동"};
    private ArrayList workList;

    private String presentWorkType;

    private MyWork myWork;
    private MyEvent myEvent;

    private String content;

    private Button addEvent;
    private Button addWork;
    private Button addcontent;
    private Button ListReset;

    private TextView workText;

    private ToggleButton worktoggle;

    private LinearLayout selectWorkGroup;

    private ListView workListView;

    SharedPreferences mPrefs;

    private Messenger mService = null;
    private Messenger mMessenger = new Messenger(new IncomingHandler());
    private boolean mBound = false;

    SQLiteDatabase db;
    DBHelper dbHelper;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location location;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            try {
                Message msg = Message.obtain(null, CONNECT_FRAGMENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    public void startServiceMethod() {
        Intent intent = new Intent(getActivity(), gpsService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            ;
            bundle.setClassLoader(getActivity().getClassLoader());
            switch (msg.what) {

                case gpsService.START_AUTO_SAVE_LOCATION: {

                    if (worktoggle.isChecked()) {
                        SaveEnd();
                    }
                    presentWorkType = "이동";
                    workText.setText(presentWorkType);
                    worktoggle.setChecked(true);

                    //myWork = (MyWork) msg.getData().getSerializable("mywork");
                    myWork = (MyWork) bundle.get("mywork");
                    //SaveStart();
                    break;
                }
                case gpsService.END_AUTO_SAVE_LOCATION: {
                    worktoggle.setChecked(false);
                    myWork = (MyWork) bundle.get("mywork");

                    //SaveEnd();
                    break;
                }
                case gpsService.STATE_MOVE: {
                    if (!msg.getData().getBoolean("moving")) break;
                    if (worktoggle.isChecked()) {
                        if (!presentWorkType.equals("이동")) {
                            SaveEnd();
                            presentWorkType = "이동";
                            workText.setText(presentWorkType);
                        }

                    }
                    worktoggle.setChecked(true);
                    myWork = (MyWork) msg.getData().getSerializable("mywork");
                    break;
                }
                case 10: {
                    //Log.d("test", "service에서 받음 : "+msg.getData().getSerializable("1") + ", " + msg.arg1);
                    break;
                }
            }
        }
    }

    ;

    public SelectWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectWorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectWorkFragment newInstance(String param1, String param2) {
        SelectWorkFragment fragment = new SelectWorkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("test", "onCreate : " + savedInstanceState);
        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (savedInstanceState != null) {
            workList = savedInstanceState.getStringArrayList("workList");
            presentWorkType = savedInstanceState.getString("presentWorkType");
        } else {
            Gson gson = new Gson();

            try {
                String json = mPrefs.getString("workList", "");
                workList = gson.fromJson(json, ArrayList.class);
                Log.d("test", "" + json + " " + workList);
            } catch (Exception ex) {

            }
            if (workList != null)
                presentWorkType = workList.get(0).toString();
            else presentWorkType = defaultWorkList[0];
        }
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = (new SimpleDateFormat("yyyy-MM").format(d));
        dbHelper = new DBHelper(getActivity(), date + ".db");

        db = dbHelper.getWritableDatabase();

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (workList != null)
            outState.putStringArrayList("workList", workList);
        outState.putString("presentWorkType", presentWorkType);
        Log.d("test", "onSaveInstanceState");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("test", "onCreateView : " + savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_select_work, container, false);

        addEvent = (Button) view.findViewById(R.id.addevent);
        addWork = (Button) view.findViewById(R.id.addwork);
        workListView = (ListView) view.findViewById(R.id.workList);
        addcontent = (Button) view.findViewById(R.id.add_content);
        ListReset = (Button) view.findViewById(R.id.listreset);

        workText = (TextView) view.findViewById(R.id.workText);

        worktoggle = (ToggleButton) view.findViewById(R.id.worktoggle);
        selectWorkGroup = (LinearLayout) view.findViewById(R.id.selectWorkGroup);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(workList);
        prefsEditor.putString("workList", json);
        prefsEditor.commit();

        db.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        startServiceMethod();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(mConnection);
        mGoogleApiClient.disconnect();
    }

    public void SaveStart() {
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
        String time = (new SimpleDateFormat("HH:mm").format(d));
        if (myWork == null) {
            myWork = new MyWork(date, time, presentWorkType, null);
        } else {
            myWork.setName(presentWorkType);
            myWork.setStartTime(date, time);
        }
        //save db

        Gson gson = new Gson();
        String json = gson.toJson(myWork);

        ContentValues values = new ContentValues();
        values.put("date", myWork.getStartTime().getDate());
        values.put("time", myWork.getStartTime().getTime());
        values.put("mywork", json);
        Log.d("test", "save start mywork : " + myWork + "time : " + myWork.getStartTime().getTime());
        Log.d("test", "save start mywork : " + myWork.getLocation());
        db.insert("work", null, values);
    }

    public void SaveEnd() {
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
        String time = (new SimpleDateFormat("HH:mm").format(d));
        myWork.setEndTime(date, time);
        //save db
        Gson gson = new Gson();
        String json = gson.toJson(myWork);

        ContentValues values = new ContentValues();
        values.put("mywork", json);
        Log.d("test", "save end mywork : " + myWork + "time : " + myWork.getStartTime().getTime());
        Log.d("test", "save end mywork : " + myWork.getLocation());

            db.update("work", values, "date =? and time = ?", new String[]{myWork.getStartTime().getDate(), myWork.getStartTime().getTime()});

        myWork=null;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Gson gson = new Gson();
        if (resultCode == AddEventDialog.CLICK_OK) {
            if (requestCode == ADD_MODE_EVENT) {

                String json = data.getStringExtra("event");
                MyEvent event = gson.fromJson(json, MyEvent.class);
                Log.d("test", "event location:" + event.getLocation());
                ContentValues values = new ContentValues();
                values.put("time", event.getDate().getTime());
                values.put("myevent", json);

                db.insert("event", null, values);

            } else if (requestCode == ADD_MODE_WORK) {

                myWork = (MyWork) data.getSerializableExtra("mywork");
                //myWork = gson.fromJson(data.getStringExtra("mywork"), MyWork.class);
                Log.d("test", "work 데이터 받음 : " + myWork.getLocation());

                if (presentWorkType.equals("이동") && worktoggle.isChecked()) {
                    Message msg = Message.obtain(null, SET_WORK);
                    msg.arg1 = START_MOVEING;
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("mywork", myWork);
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("test", "onActivityCreated : " + savedInstanceState);
        super.onActivityCreated(savedInstanceState);

        if (workList == null)
            workList = new ArrayList<String>(Arrays.asList(defaultWorkList));
        Log.d("test", "worklist : " + workList);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, workList);
        workListView.setAdapter(adapter);


        workText.setText(presentWorkType);

        addEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddEventDialog.class);
                intent.putExtra("mode", ADD_MODE_EVENT);
                startActivityForResult(intent, ADD_MODE_EVENT);
            }
        });

        ListReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workList = new ArrayList<String>(Arrays.asList(defaultWorkList));
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, workList);
                workListView.setAdapter(adapter);
                //workListView.setVisibility(View.GONE);
                //workListView.setVisibility(View.VISIBLE);
                //workListView.setAdapter(adapter);

            }
        });

        worktoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Message msg = Message.obtain(null, 10);
                msg.arg1 = 1111;
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("1","플레그먼트에서 보냄");
                msg.setData(bundle1); ;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
                Log.d("test", "toggle : " + worktoggle.isChecked());
                Calendar c = Calendar.getInstance();
                Date d = c.getTime();
                String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
                String time = (new SimpleDateFormat("HH:mm").format(d));
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
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (worktoggle.isChecked()) {
                    Log.d("test", "toggle 시작 : "+worktoggle.isChecked() +"일 : "+presentWorkType);


                    if (myWork == null) {
                        myWork = new MyWork(date, time, presentWorkType, location);
                    }

                    if(presentWorkType.equals("이동")){
                        Log.d("test", "이동 시작 : ");
                        Message msg = Message.obtain(null, SET_MOVEING);
                        msg.arg1 = START_MOVEING;
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("mywork",myWork);
                        msg.setData(bundle1);
                        try {
                            mService.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    else SaveStart();
                } else {
                    Log.d("test", "toggle 종료 : "+worktoggle.isChecked());
                    if(presentWorkType.equals("이동")){
                        Message msg = Message.obtain(null, SET_MOVEING);
                        msg.arg1 = END_MOVEING;
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("mywork",myWork);
                        msg.setData(bundle1);
                        try {
                            mService.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    SaveEnd();
                }

            }
        });

        addcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(getActivity(), AddEventDialog.class);
                if(myWork ==null)
                    myWork = new MyWork();
                intent.putExtra("mywork", (Parcelable) myWork);
                intent.putExtra("mode", ADD_MODE_WORK).putExtra("workType", presentWorkType);
                startActivityForResult(intent, ADD_MODE_WORK);
            }
        });


        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                alert.setTitle("일 목록 추가");
                alert.setMessage("추가하려는 일의 이름을 적어주세요");

                final EditText input = new EditText(getActivity());
                alert.setView(input);

                alert.setNegativeButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        workList.add(value);
                        ArrayAdapter adapter = (ArrayAdapter) workListView.getAdapter();
                        adapter.notifyDataSetChanged();
                        workListView.setAdapter(adapter);
                        Toast.makeText(getActivity(), value, Toast.LENGTH_LONG).show();
                    }
                });
                alert.setPositiveButton("취소", null);
                alert.create().show();
            }
        });



        workListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position;
                presentWorkType = (String) parent.getItemAtPosition(pos);
                workText.setText(presentWorkType);

            }
        });
        workListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position;
                final String itemValue = (String) parent.getItemAtPosition(pos);
                if(position<defaultWorkList.length) return true;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(itemValue);

                alertDialogBuilder.setNeutralButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                        alert.setTitle("일 목록 수정");
                        alert.setMessage("수정하려는 일의 이름을 적어주세요");

                        final EditText input = new EditText(getActivity());
                        alert.setView(input);

                        alert.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String value = input.getText().toString();
                                int index = workList.indexOf(itemValue);
                                workList.set(index, value);
                                Toast.makeText(getActivity(), value, Toast.LENGTH_LONG).show();
                            }
                        });
                        alert.setPositiveButton("취소", null);
                        alert.create().show();
                    }
                });
                alertDialogBuilder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workList.remove(workList.indexOf(itemValue));
                        ArrayAdapter adapter = (ArrayAdapter) workListView.getAdapter();
                        adapter.notifyDataSetChanged();
                        workListView.setVisibility(View.GONE);
                        workListView.setVisibility(View.VISIBLE);
                    }
                });
                alertDialogBuilder.setPositiveButton("취소", null);
                alertDialogBuilder.create().show();
                return true;
            }
        });

    }



    /*
    // TODO: Rename method, update argument and hook method into UI event
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
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


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
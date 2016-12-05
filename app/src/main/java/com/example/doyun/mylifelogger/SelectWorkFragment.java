package com.example.doyun.mylifelogger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectWorkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectWorkFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String[] defaultWorkList = {"업무","공부","게임","과제","운동","이동"};
    private ArrayList workList;

    private String presentWorkType;

    private MyWorkData myWorkData;
    private String detail;

    private Button selectWork;
    private Button addEvent;
    private Button addWork;
    private Button addDetaile;
    private Button ListReset;

    private TextView workText;

    private ToggleButton worktoggle;

    private LinearLayout selectWorkGroup;

    private ListView workListView;

    SharedPreferences mPrefs;

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
        Log.d("test","onCreate : "+savedInstanceState);
        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(savedInstanceState!=null){
            workList = savedInstanceState.getStringArrayList("workList");
        }
        else {
            Gson gson = new Gson();
            String json = mPrefs.getString("workList", "");
            workList = gson.fromJson(json, ArrayList.class);

        }

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(workList!=null)
            outState.putStringArrayList("workList", workList);
        Log.d("test","onSaveInstanceState");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("test","onCreateView : "+savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_select_work, container, false);

        selectWork = (Button)view.findViewById(R.id.selectwork);
        addEvent = (Button)view.findViewById(R.id.addevent);
        addWork = (Button)view.findViewById(R.id.addwork);
        workListView = (ListView) view.findViewById(R.id.workList);
        addDetaile = (Button)view.findViewById(R.id.add_detaile);
        ListReset = (Button)view.findViewById(R.id.listreset);

        workText = (TextView)view.findViewById(R.id.workText);

        worktoggle = (ToggleButton)view.findViewById(R.id.worktoggle);
        selectWorkGroup = (LinearLayout)view.findViewById(R.id.selectWorkGroup);

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("test","onActivityCreated : "+savedInstanceState);
        super.onActivityCreated(savedInstanceState);
        if(workList==null)
            workList = new ArrayList<String>(Arrays.asList(defaultWorkList));
        presentWorkType = workList.get(0).toString();
        workText.setText(presentWorkType);

        selectWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectWorkGroup.getVisibility()!=View.GONE) {
                    selectWorkGroup.setVisibility(View.GONE);
                }
                else{
                    selectWorkGroup.setVisibility(View.VISIBLE);
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, workList );
                    workListView.setAdapter(adapter);
                }
            }
        });
        ListReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workList = new ArrayList<String>(Arrays.asList(defaultWorkList));
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, workList );
                workListView.setAdapter(adapter);
                //workListView.setVisibility(View.GONE);
                //workListView.setVisibility(View.VISIBLE);
                //workListView.setAdapter(adapter);

            }
        });

        worktoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (worktoggle.isChecked()){
                    Calendar c = Calendar.getInstance();
                    Date d = c.getTime();
                    String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
                    String time = (new SimpleDateFormat("HH:mm").format(d));
                    if(detail!=null)
                        myWorkData = new MyWorkData(date, time, presentWorkType, detail);
                    else
                        myWorkData = new MyWorkData(date, time, presentWorkType);
                    //save db
                }
                else{
                    Calendar c = Calendar.getInstance();
                    Date d = c.getTime();
                    String date = (new SimpleDateFormat("yyyy-MM-dd").format(d));
                    String time = (new SimpleDateFormat("HH:mm").format(d));
                    myWorkData.setEndTime(date, time);
                    //save db
                }
            }
        });

        addDetaile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                alert.setTitle("세부내용 추가");

                final EditText input = new EditText(getActivity());
                //input.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                input.setHorizontallyScrolling(false);
                input.setLines(5);
                input.setGravity(Gravity.TOP);
                alert.setView(input);


                alert.setNegativeButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        detail = input.getText().toString();
                        if(myWorkData!=null)
                            myWorkData.setDetail(detail);
                    }
                });
                alert.setPositiveButton("취소", null);
                alert.create().show();
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
                selectWorkGroup.setVisibility(View.GONE);

            }
        });
        workListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position;
                final String itemValue = (String) parent.getItemAtPosition(pos);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

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
    */
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

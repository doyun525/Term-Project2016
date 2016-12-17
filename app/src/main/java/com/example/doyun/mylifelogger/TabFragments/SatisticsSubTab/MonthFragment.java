package com.example.doyun.mylifelogger.TabFragments.SatisticsSubTab;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doyun.mylifelogger.DB.DBHelper;
import com.example.doyun.mylifelogger.DB.MyData;
import com.example.doyun.mylifelogger.DB.MyWork;
import com.example.doyun.mylifelogger.R;
import com.google.gson.Gson;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.CircleGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraph;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraphVO;

import java.lang.reflect.Field;
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
 * {@link MonthFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int mYear, mMonth, mDay;
    DatePickerDialog datePickerDialog;

    private Calendar c;
    private Date d;
    private String date;

    Button nextday;
    Button previousday;
    TextView viewDate;
    TextView tv_totaltime;
    ListView listView;
    private ViewGroup layoutGraphView;

    DBHelper dbHelper;
    SQLiteDatabase db;

    private ArrayList<MyData> DataList;
    private ArrayList<GraphdataType> graphdataTypeslist;
    private int totalTime;

    public MonthFragment() {
        // Required empty public constructor
    }
    public class GraphdataType {
        String workType = null;
        int time = 0;
        GraphdataType(String s, int i){
            workType = s;
            time = i;
        }
    }
    public void LoadData() {
        if (db != null) db.close();
        d = c.getTime();
        String date1 = (new SimpleDateFormat("yyyy-MM").format(d));
        Log.d("test", "date : " + date1);
        dbHelper = new DBHelper(getActivity(), date1 + ".db");
        db = dbHelper.getWritableDatabase();

        Gson gson = new Gson();

        String sbwork = "SELECT * from " + DBHelper.TABLE_WORK;
        Cursor cursorWork = db.rawQuery(sbwork,null);

        List<MyData> arrayList = new ArrayList<MyData>();

        while (cursorWork.moveToNext()) {
            String json = cursorWork.getString(3);
            MyWork myWork = gson.fromJson(json, MyWork.class);
            Log.d("test", "mywork : " + myWork.getStartTime().getTime());
            arrayList.add(myWork);
        }
        Log.d("test", "list : " + arrayList);

        DataList = (ArrayList<MyData>) arrayList;

        totalTime = 0;

        graphdataTypeslist = new ArrayList<GraphdataType>();

        for(int i=0; i< DataList.size();i++){
            MyWork myWork = (MyWork) DataList.get(i);
            String workType = myWork.getName();
            int starttime = myWork.getStartTime().getHour()*60+ myWork.getStartTime().getMin();
            int endtime = myWork.getEndTime().getHour()*60 + myWork.getEndTime().getMin();
            if(starttime>endtime) endtime+=60*24;
            int time = endtime-starttime;
            Log.d("test", "work : "+workType+ "time : "+time);
            GraphdataType graphdataType = new GraphdataType(workType, time);
            totalTime+=time;
            for(int j =0;j<graphdataTypeslist.size();j++){
                Log.d("test", "list work : "+graphdataTypeslist.get(j).workType + " c work : "+workType);
                if(graphdataTypeslist.get(j).workType.equals(workType)) {
                    graphdataTypeslist.get(j).time += time;
                    graphdataType = null;
                    break;
                }
            }
            if(graphdataType == null) continue;
            graphdataTypeslist.add(graphdataType);
        }
        Comparator<GraphdataType> myComparator = new Comparator<GraphdataType>() {
            private final Collator collator = Collator.getInstance();

            @Override
            public int compare(GraphdataType o1, GraphdataType o2) {
                //return collator.compare(o1.getDate().getTime().toString(), o2.getDate().getTime().toString());
                int o1time = o1.time;
                int o2time = o2.time;
                //return o1.getDate().getTime().compareToIgnoreCase(o2.getDate().getTime());
                return (o1time > o2time) ? -1 : (o1time < o2time) ? 1 : 0;
            }
        };

        Collections.sort(graphdataTypeslist, myComparator);

        db.close();

    }
    private void setCircleGraph() {
        layoutGraphView.removeAllViews();
        CircleGraphVO vo = makeLineGraphAllSetting();

        layoutGraphView.addView(new CircleGraphView(getActivity(),vo));
    }
    private CircleGraphVO makeLineGraphAllSetting() {
        //BASIC LAYOUT SETTING
        //padding
        int paddingBottom 	= CircleGraphVO.DEFAULT_PADDING;
        int paddingTop 		= CircleGraphVO.DEFAULT_PADDING;
        int paddingLeft 	= CircleGraphVO.DEFAULT_PADDING;
        int paddingRight 	= CircleGraphVO.DEFAULT_PADDING;

        //graph margin
        int marginTop 		= CircleGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight 	= CircleGraphVO.DEFAULT_MARGIN_RIGHT;

        // radius setting
        int radius = 130;

        List<CircleGraph> arrGraph 	= new ArrayList<CircleGraph>();

        int[] colors = {Color.BLUE,Color.GREEN,Color.RED, Color.YELLOW, Color.BLACK, Color.GRAY, Color.CYAN, Color.MAGENTA , Color.TRANSPARENT, Color.WHITE , Color.DKGRAY, Color.LTGRAY};

        for (int i=0;i<graphdataTypeslist.size();i++){
            arrGraph.add(new CircleGraph(graphdataTypeslist.get(i).workType, colors[i%colors.length], graphdataTypeslist.get(i).time));
        }
        Log.d("test", "arrgraph : " + arrGraph);
        CircleGraphVO vo = new CircleGraphVO(paddingBottom, paddingTop, paddingLeft, paddingRight,marginTop, marginRight,radius, arrGraph);

        // circle Line
        vo.setLineColor(Color.WHITE);

        // set text setting
        vo.setTextColor(Color.WHITE);
        vo.setTextSize(40);

        // set circle center move X ,Y
        vo.setCenterX(0);
        vo.setCenterY(0);

        //set animation
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, 2000));
        //set graph name box

        GraphNameBox graphNameBox = new GraphNameBox();

        // nameBox
        graphNameBox.setNameboxMarginTop(25);
        graphNameBox.setNameboxMarginRight(25);
        graphNameBox.setNameboxTextSize(30);

        vo.setGraphNameBox(graphNameBox);

        return vo;
    }

    public void setListView(){
        ArrayList arrayList = new ArrayList();
        for(int i =0;i<graphdataTypeslist.size();i++){
            String s = graphdataTypeslist.get(i).workType + " : " + graphdataTypeslist.get(i).time/60 + "시간 "+ graphdataTypeslist.get(i).time%60 + "분";
            arrayList.add(s);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 ,arrayList);
        listView.setAdapter(adapter);
    }

    public void set(){
        LoadData();
        setCircleGraph();
        setListView();
        tv_totaltime.setText(totalTime/60 + "시간 "+totalTime%60 + "분");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthFragment newInstance(String param1, String param2) {
        MonthFragment fragment = new MonthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        date = (new SimpleDateFormat("yyyy-MM").format(d));
    }
    private DatePickerDialog.OnDateSetListener mDataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yaerSelect, int monthOfyear, int dayOfmonth) {
            c.set(yaerSelect, monthOfyear, dayOfmonth);
            mYear = yaerSelect;
            mMonth = monthOfyear + 1;
            mDay = dayOfmonth;
            d = c.getTime();
            date = (new SimpleDateFormat("yyyy-MM").format(d));
            viewDate.setText(date);
            set();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        viewDate = (TextView) view.findViewById(R.id.monthtab_selectDate);
        tv_totaltime = (TextView)view.findViewById(R.id.month_totaltime);

        nextday = (Button) view.findViewById(R.id.monthtab_nextday);
        previousday = (Button) view.findViewById(R.id.monthtab_previousday);

        listView = (ListView)view.findViewById(R.id.monthtab_listview);

        layoutGraphView = (ViewGroup)view.findViewById(R.id.monthlayoutGraphView);
        return view;
    }
    public String dateChange(Calendar c, int i) {
        //c.add(c.DATE, i);
        c.set(c.get(Calendar.YEAR),  c.get(Calendar.MONTH) +i, c.get(Calendar.DAY_OF_MONTH));
        mYear = c.get(Calendar.YEAR) ;
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        d = c.getTime();
        return (new SimpleDateFormat("yyyy-MM").format(d));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth ,mDataSetListener, mYear, mMonth - 1, mDay);


        try {
            Field[] f = datePickerDialog.getClass().getDeclaredFields();
            for (Field dateField : f) {
                if (dateField.getName().equals("mDatePicker")) {
                    dateField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) dateField
                            .get(datePickerDialog);

                    Field datePickerFields[] = dateField.getType()
                            .getDeclaredFields();

                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            try {
                                ((View) dayPicker).setVisibility(View.GONE);
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

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
                set();
            }
        });
        previousday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = dateChange(c, -1);
                viewDate.setText(date);
                datePickerDialog = new DatePickerDialog(getActivity(), mDataSetListener, mYear, mMonth - 1, mDay);
                set();
            }
        });

        set();
    }

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

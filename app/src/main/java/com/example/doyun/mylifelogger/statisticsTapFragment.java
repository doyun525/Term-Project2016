package com.example.doyun.mylifelogger;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link statisticsTapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link statisticsTapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class statisticsTapFragment extends Fragment implements OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private MapView mapView;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Button statistics_day;
    private Button statistics_week;
    private Button statistics_month;

    private TextView statistics_week_view;
    private TextView statistics_month_view;

    public statisticsTapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment statisticsTapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static statisticsTapFragment newInstance(String param1, String param2) {
        statisticsTapFragment fragment = new statisticsTapFragment();
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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics_tap, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        statistics_day = (Button) view.findViewById(R.id.statistics_day);
        statistics_week = (Button) view.findViewById(R.id.statistics_week);
        statistics_month = (Button) view.findViewById(R.id.statistics_month);
        statistics_week_view = (TextView) view.findViewById(R.id.statistics_week_view);
        statistics_month_view = (TextView) view.findViewById(R.id.statistics_month_view);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        statistics_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statistics_week_view.setVisibility(View.GONE);
                statistics_month_view.setVisibility(View.GONE);
                if(mapFragment.getView().getVisibility()!= View.VISIBLE){
                    mapFragment.getView().setVisibility(View.VISIBLE);
                }
                else {
                    mapFragment.getView().setVisibility(View.GONE);
                }
            }
        });

        statistics_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.GONE);
                statistics_month_view.setVisibility(View.GONE);
                if(statistics_week_view.getVisibility()!=View.VISIBLE){
                    statistics_week_view.setVisibility(View.VISIBLE);
                }
                else{
                    statistics_week_view.setVisibility(View.GONE);
                }
            }
        });

        statistics_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.GONE);
                statistics_week_view.setVisibility(View.GONE);
                if(statistics_month_view.getVisibility()!=View.VISIBLE){
                    statistics_month_view.setVisibility(View.VISIBLE);
                }
                else{
                    statistics_month_view.setVisibility(View.GONE);
                }
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
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }
    */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mapFragment.getView().setVisibility(View.GONE);
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

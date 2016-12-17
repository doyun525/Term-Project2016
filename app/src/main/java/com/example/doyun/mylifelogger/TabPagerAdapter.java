package com.example.doyun.mylifelogger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.doyun.mylifelogger.TabFragments.ObjectiveFragment;
import com.example.doyun.mylifelogger.TabFragments.SelectWorkFragment;
import com.example.doyun.mylifelogger.TabFragments.ViewdayFragment;
import com.example.doyun.mylifelogger.TabFragments.statisticsTapFragment;

import java.io.Serializable;

/**
 * Created by doyun on 2016-11-28.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    SelectWorkFragment selectWorkFragment;
    com.example.doyun.mylifelogger.TabFragments.statisticsTapFragment statisticsTapFragment;
    ObjectiveFragment objectiveFragment;
    ViewdayFragment viewdayFragment;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount=tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        Log.d("test","getItemPosition 불림");
        if(object instanceof ViewdayFragment) {
            Log.d("test", "getItemPosition viewday 불림");

            ((ViewdayFragment) object).setMap();
        }
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                //if(selectWorkFragment==null)
                    selectWorkFragment = new SelectWorkFragment();
                return selectWorkFragment;
            case 1:
                Log.d("test","viewday 불림");
                if(viewdayFragment==null)
                    viewdayFragment = new ViewdayFragment();
                return viewdayFragment;
            case 2:
                //if(statisticsTapFragment==null)
                    statisticsTapFragment = new statisticsTapFragment();
                return statisticsTapFragment;
            case 3:
                //if(objectiveFragment==null)
                     objectiveFragment = new ObjectiveFragment();
                return objectiveFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public  void putData(int position, Object object){
        switch (position){
            case 0:
                if(selectWorkFragment==null)
                    selectWorkFragment = new SelectWorkFragment();
                Bundle args = new Bundle();
                args.putSerializable("db", (Serializable) object);
                selectWorkFragment.setArguments(args);

            case 1:
                if(statisticsTapFragment==null)
                    statisticsTapFragment = new statisticsTapFragment();

            case 2:
                if(objectiveFragment==null)
                    objectiveFragment = new ObjectiveFragment();

        }
    }
}

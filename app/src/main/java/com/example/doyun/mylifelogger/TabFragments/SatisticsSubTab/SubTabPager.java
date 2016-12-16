package com.example.doyun.mylifelogger.TabFragments.SatisticsSubTab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.doyun.mylifelogger.TabFragments.ObjectiveFragment;
import com.example.doyun.mylifelogger.TabFragments.SelectWorkFragment;
import com.example.doyun.mylifelogger.TabFragments.ViewdayFragment;
import com.example.doyun.mylifelogger.TabFragments.statisticsTapFragment;

import java.io.Serializable;

/**
 * Created by doyun on 2016-12-17.
 */

public class SubTabPager extends FragmentStatePagerAdapter {

    private int tabCount;

    public SubTabPager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount=tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                //if(selectWorkFragment==null)
                DayFragment dayFragment = new DayFragment();
                return dayFragment;
            case 1:
                //if(viewdayFragment==null)
                MonthFragment monthFragment = new MonthFragment();
                return monthFragment;

        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

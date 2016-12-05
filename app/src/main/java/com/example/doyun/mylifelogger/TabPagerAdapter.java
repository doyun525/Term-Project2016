package com.example.doyun.mylifelogger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by doyun on 2016-11-28.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount=tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                SelectWorkFragment selectWorkFragment = new SelectWorkFragment();
                return selectWorkFragment;
            case 1:
                statisticsTapFragment statisticsTapFragment = new statisticsTapFragment();
                return statisticsTapFragment;
            case 2:
                ObjectiveFragment objectiveFragment = new ObjectiveFragment();
                return objectiveFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

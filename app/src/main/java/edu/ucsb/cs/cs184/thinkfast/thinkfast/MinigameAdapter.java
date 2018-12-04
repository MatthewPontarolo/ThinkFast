package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MinigameAdapter extends FragmentStatePagerAdapter  {

    private final List<Fragment> customFragmentList = new ArrayList<>();
    private final List<String> customFragmentTitles = new ArrayList<>();

    public MinigameAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title)
    {
        customFragmentList.add(fragment);
        customFragmentTitles.add(title);
        notifyDataSetChanged();

        Log.d("debuglog", "len is " + customFragmentList.size());
    }

    public void clear() {
        for (Fragment f : customFragmentList) {
            
        }
        customFragmentList.clear();
        customFragmentTitles.clear();
    }

    @Override
    public Fragment getItem(int i) {
        return customFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return customFragmentList.size();
    }
}


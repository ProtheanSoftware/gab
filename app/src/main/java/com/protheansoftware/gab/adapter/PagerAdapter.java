package com.protheansoftware.gab.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.protheansoftware.gab.activities.MainActivity;
import com.protheansoftware.gab.fragments.MatchScreenFragment;
import com.protheansoftware.gab.fragments.MatchesListFragment;
import com.protheansoftware.gab.fragments.MessagingFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    boolean hasMatches = false;
    MainActivity main;
    MatchScreenFragment matchScreenFragment;
    MessagingFragment messagingFragment;
    MatchesListFragment matchesListFragment;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, MainActivity main) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.main = main;
    }
    public void setHasMatches(boolean value) {
        hasMatches = value;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(matchScreenFragment == null) {
                    matchScreenFragment = new MatchScreenFragment();
                    matchScreenFragment.setMain(main);
                }
                return matchScreenFragment;
            case 1:
                if(matchesListFragment == null) {
                    matchesListFragment = new MatchesListFragment();
                    matchesListFragment.setMain(main);
                }
                return matchesListFragment;
            case 2:
                if(messagingFragment == null){
                    messagingFragment = new MessagingFragment();
                    messagingFragment.setMain(main);
                }
                return messagingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    public void setCount(int i){
        this.mNumOfTabs = i;
        notifyDataSetChanged();
    }
}
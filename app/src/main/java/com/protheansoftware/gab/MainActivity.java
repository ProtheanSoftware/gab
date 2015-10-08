package com.protheansoftware.gab;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.util.ArrayList;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.protheansoftware.gab.adapter.TabsPagerAdapter;


/**
 * This is the main activity, it holds the chat, the list of matches and the matchscreen fragments.
 * @author Tobias Allden
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsAdapter;
    private ActionBar actionBar;
    private ArrayList<Match> matches;

    //Reference to matchscreen to be able to build with user profile
    private MatchScreenFragment match;





    //Tab titles
    private String[] tabs = {"Match Screen","Matches","Chat"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        //Fix for mysql(jdbc)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Initialize
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabsAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        matches = new ArrayList<Match>();

        //add tabs
        for(String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }


    /**
     * on swiping the viewpager make respective tab selected
     * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        if(AccessToken.getCurrentAccessToken() != null) {
            Toast.makeText(this,"Hello,world!",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


    /**
     * Searches the database for nearby matches
     */
    private void searchForMatches() {
        //SEARCH DATABASE FOR MATCHES
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    //Returns the fragment for the specified tag
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(tab.getPosition() == 0) {
            if(matches.isEmpty()) {
                //returns searchformatches
                viewPager.setCurrentItem(11);
                searchForMatches();
            } else {
                viewPager.setCurrentItem(tab.getPosition());
                this.match = (MatchScreenFragment)tabsAdapter.getItem(0);
                match.setmatches(matches);
            }
        }
            viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

}
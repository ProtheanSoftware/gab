package com.protheansoftware.gab;

import android.os.StrictMode;
import android.util.Log;

import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.Profile;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class handles the bulk of the data the application uses
 * @author Tobias Alldén
 */
public class DataHandler {
    private static final String TAG = "DATAHANDLER";
    private ArrayList<Profile> matches = new ArrayList<Profile>();
    private Profile me;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Initializes the DataHandler
     */
    public void init() {
        //Fix for mysql(jdbc)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            me = JdbcDatabaseHandler.getInstance().getUser(getMyDbId());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not generate me match");
        }

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    /**
     * Starts a thread that search for matches and then returns an arraylist with matches
     */
    public void searchForMatches() {
        Log.d(TAG,"Searching for matches...");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

        //Start session and search for matches
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (matches) {
                        matches.add(new Profile(7,1,"Karl",new ArrayList<String>()));

                }
            }
        });
        thread.run();
        synchronized (matches) {
            while (thread.isAlive()) {
                try {
                    matches.wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(matches != null || matches.isEmpty()) {
                sortMatches(matches);
                pcs.firePropertyChange("MatchList",null,matches);
            }

        }
            }
        });
        t.start();

    }

    /**
     * Returns the matches
     */
    public ArrayList<Profile> getMatches() {
        return this.matches;
    }

    /**
     * Sorts the matchlist after number of simular interests between you and the match
     * @param unsortedMatches
     * @return
     */
    private void sortMatches(ArrayList<Profile> unsortedMatches) {
        boolean flag = true;
        Profile temp;
        ArrayList<Profile> matchesWithnoSimularInterests = new ArrayList<Profile>();

        while (flag) {
            flag = false;
            for (int i = 0; i < unsortedMatches.size() - 1; i++) {
                if (me.getNumberOfSimularInterests(unsortedMatches.get(i).getInterests()) != 0) {
                    if (me.getNumberOfSimularInterests(unsortedMatches.get(i).getInterests()) <
                            me.getNumberOfSimularInterests(unsortedMatches.get(i + 1).getInterests())) {
                        temp = unsortedMatches.get(i);
                        unsortedMatches.set(i, unsortedMatches.get(i + 1));
                        unsortedMatches.set(i + 1, temp);
                        flag = true;
                    }
                } else {
                    matchesWithnoSimularInterests.add(unsortedMatches.get(i));
                }
            }
            if (!matchesWithnoSimularInterests.isEmpty()) {
                for (Profile m : matchesWithnoSimularInterests) {
                    unsortedMatches.add(m);
                }
            }
        }
    }




    /**
     * Returns the current users db id.
     * @return
     */
    public int getMyDbId() throws SQLException {
        return JdbcDatabaseHandler.getInstance().getMyId();
    }



}

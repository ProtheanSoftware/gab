package com.protheansoftware.gab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.protheansoftware.gab.model.BusHandler;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.Match;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Shows the different matches retrieved
 */
public class MatchScreenFragment extends Fragment implements View.OnClickListener {
    private JdbcDatabaseHandler jdb = JdbcDatabaseHandler.getInstance();
    private BusHandler bh = BusHandler.getInstance();
    private TelephonyManager telephonyManager;
    // list of matches
    private ArrayList<Match> matches;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setPotentialMatches();
        View rootView = inflater.inflate(R.layout.fragment_match_screen,container,false);
        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();

        bh.startSessionIfNeeded(this.getContext(), (GsmCellLocation) telephonyManager.getCellLocation());
    }

    //Fills out the fragment with the match.
    public void setMatch(final Match match){
        ((TextView)getActivity().findViewById(R.id.nameTag)).setText(match.getName());
        ((Button)getActivity().findViewById(R.id.dislikeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike(match.getDatabaseId(), match.getName());
            }
        });
        ((Button)getActivity().findViewById(R.id.likeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(match.getDatabaseId(), match.getName());
            }
        });
    }

    /**
     * Dislike the match
     */
    public void dislike(int id, String name) {
        try {
            JdbcDatabaseHandler.getInstance().addDislike(JdbcDatabaseHandler.getInstance().getUserFromFBID(id).getId(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Likes the match
     */
    public void like(int id, String name){
        try {
            JdbcDatabaseHandler.getInstance().addLike(JdbcDatabaseHandler.getInstance().getUserFromFBID(id).getId(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets  the list of potential matches
     */
    public void setPotentialMatches() {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        
    }

    public void init(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }
}

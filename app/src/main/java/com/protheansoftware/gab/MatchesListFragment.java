package com.protheansoftware.gab;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.protheansoftware.gab.model.IDatabaseHandler;
import com.protheansoftware.gab.model.Profile;
import com.protheansoftware.gab.adapter.MatchesListAdapter;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private IDatabaseHandler dbh;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        setDbh(new JdbcDatabaseHandler());
        List<Profile> matches = new ArrayList<Profile>();
        try {
            matches = dbh.getMatches();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListAdapter matchesListAdapter = new MatchesListAdapter(getActivity(), matches);
        setListAdapter(matchesListAdapter);

        getListView().setOnItemClickListener(this);
    }

    public void setDbh(IDatabaseHandler dbh){
        this.dbh = dbh;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), String.valueOf(parent.getItemAtPosition(position)), Toast.LENGTH_SHORT).show();
    }


}
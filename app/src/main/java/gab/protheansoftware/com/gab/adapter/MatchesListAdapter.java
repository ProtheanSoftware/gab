package gab.protheansoftware.com.gab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gab.protheansoftware.com.gab.R;
import gab.protheansoftware.com.gab.model.Profile;

/**
 * Created by Oscar Hall on 01/10/15.
 */
public class MatchesListAdapter extends ArrayAdapter<Profile>{




    public MatchesListAdapter(Context context, Profile[] profiles)
    {
        super(context, R.layout.matches_list_row_template , profiles);
    }

    //TODO replace the inflater with view holder pattern
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View custom_row = inflater.inflate(R.layout.matches_list_row_template, parent, false);

        Profile SingleMatchItem = getItem(position);

        TextView matchedNameText = (TextView) custom_row.findViewById(R.id.matchedNameText);
        ImageView matchedPicture = (ImageView) custom_row.findViewById(R.id.matchedPicture);

        matchedNameText.setText(SingleMatchItem.getName());

        matchedPicture.setImageResource(R.drawable.oskar);

        return custom_row;
    }
}

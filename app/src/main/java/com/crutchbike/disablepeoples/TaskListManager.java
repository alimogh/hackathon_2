package com.crutchbike.disablepeoples;


import android.app.Activity;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by андрей on 18.10.2014.
 */
public class TaskListManager extends ArrayAdapter<JSONObject> {

    private final Activity context;
    private final JSONObject[] web;
    //private final Integer[] imageId;

    public TaskListManager(Activity context,
                           JSONObject[] web) {
        super(context, R.layout.list_singleton, web);
        this.context = context;
        this.web = web;
        //this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_singleton, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView snippet = (TextView) rowView.findViewById(R.id.tsnippet);
        SmartImageView imageView = (SmartImageView) rowView.findViewById(R.id.img);
        try {
            txtTitle.setText(web[position].getString("about"));
            snippet.setText(web[position].getString("address"));

            if (web[position].getString("date").length() == 0) {
                txtTitle.setTextColor(Color.RED);
            }


            JSONObject user = web[position].getJSONObject("user");
            if (user.getString("avatar_url").length() == 0) {
                imageView.setImageResource(R.drawable.noavatar);

            } else {
                imageView.setImageUrl(user.getString("avatar_url"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }


}

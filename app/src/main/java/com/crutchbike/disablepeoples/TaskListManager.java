package com.crutchbike.disablepeoples;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by андрей on 18.10.2014.
 */
public class TaskListManager extends ArrayAdapter<JSONObject> {

    private final Activity context;
    private final JSONObject[] web;
    private final Integer[] imageId;

    public TaskListManager(Activity context,
                           JSONObject[] web, Integer[] imageId) {
        super(context, R.layout.list_singleton, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_singleton, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        try {
            txtTitle.setText(web[position].getString("about"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imageView.setImageResource(imageId[position]);
        return rowView;
    }


}

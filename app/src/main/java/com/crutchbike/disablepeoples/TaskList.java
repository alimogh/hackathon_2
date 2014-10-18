package com.crutchbike.disablepeoples;

/**
 * Created by андрей on 18.10.2014.
 */

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskList extends Activity {
    ListView list;
    String JSONData;


    List<JSONObject> Titles = new ArrayList<JSONObject>();
    JSONObject[] TaskArray;

    //Integer[] imageId = {
    //        R.drawable.a,
    //       R.drawable.abc_cab_background_bottom_holo_dark,
    //};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        JSONData = getIntent().getStringExtra("JSONData");

        Titles.clear();
        ParseTasks(JSONData);

        TaskArray = new JSONObject[Titles.size()];
        Titles.toArray(TaskArray);

        TaskListManager adapter = new
                TaskListManager(TaskList.this, TaskArray);
        list = (ListView) findViewById(R.id.task_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TaskDetailDialog Detail = new TaskDetailDialog();
                Detail.task = TaskArray[+position];
                Detail.HTTPConnector = Globals.HTTPApi;
                Detail.show(getFragmentManager(), null);

            }
        });
    }


    public void ParseTasks(String jsonStr) {
        try {

            JSONObject ServerObject = new JSONObject(jsonStr);
            JSONArray tasks = ServerObject.getJSONArray("tasks");
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);
                Titles.add(task);
            }
        } catch (JSONException e) {
        }

    }
}

package com.crutchbike.disablepeoples;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crutchbike.disablepeoples.ApiHTTPConnector;
import com.crutchbike.disablepeoples.R;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by андрей on 18.10.2014.
 */


public class TaskListDialog extends DialogFragment {

    public JSONObject task;
    public ApiHTTPConnector HTTPConnector;
    public String TaskId;
    LayoutInflater inflater;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        inflater = getActivity().getLayoutInflater();
        View ActivityHandle = inflater.inflate(R.layout.activity_task_list, null);


        builder.setView(ActivityHandle)
                // Add action buttons
                .setPositiveButton(R.string.AcceptTask, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AcceptTask();
                    }
                })
                .setNegativeButton(R.string.BackToMap, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    public void AcceptTask() {

    }
}


package com.crutchbike.disablepeoples;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by андрей on 18.10.2014.
 */
public class AddTaskDialog extends DialogFragment {

    public JSONObject task;
    public ApiHTTPConnector HTTPConnector;
    public String TaskId;
    public LatLng point;
    LayoutInflater inflater;
    TextView TAbout;
    DatePicker TDate;
    TimePicker TTime;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        inflater = getActivity().getLayoutInflater();
        View ActivityHandle = inflater.inflate(R.layout.activity_add_task, null);


        // Toast.makeText(inflater.getContext(), task.getString("date"), Toast.LENGTH_SHORT).show();

        // TextView Title = (TextView) ActivityHandle.findViewById(R.id.Dtitle);
        //  Title.setText(task.getString("about"));


        TAbout = (TextView) ActivityHandle.findViewById(R.id.About);
        TDate = (DatePicker) ActivityHandle.findViewById(R.id.Date);
        TTime = (TimePicker) ActivityHandle.findViewById(R.id.Time);

        builder.setView(ActivityHandle)
                // Add action buttons
                .setPositiveButton(R.string.TaskCreateAccept, new DialogInterface.OnClickListener() {
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


    public String MakeTaskJSON(String about, String date, String lat, String lng) {
        //TODO:Add JSON generator (Maybe (It's fastest way!))
        String result = "{ \"task\": { \"about\": \"" + about + "\", \"date\": \"" + date + "\", \"lat\": \"" + lat + "\", \"lng\": \"" + lng + "\" } }";
        return result;
    }


    public void AcceptTask() {

        //TODO: Destroy THIS Crutch
        int year = TDate.getYear();
        int month = TDate.getMonth();
        int day = TDate.getDayOfMonth();

        String rdate = Integer.toString(day) + "-";

        if (month < 10) {

            rdate += "0" + Integer.toString(month);
        } else {
            rdate += Integer.toString(month);
        }
        rdate += "-";
        if (day < 10) {

            rdate += "0" + Integer.toString(day);
        } else {
            rdate += Integer.toString(day);
        }
        //Crutch

        String ReadyRequest = MakeTaskJSON(TAbout.getText().toString(), rdate, Double.toString(point.latitude), Double.toString(point.longitude));

        HTTPConnector.json("/tasks", ReadyRequest, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Toast.makeText(inflater.getContext(), responseBody, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                Toast.makeText(inflater.getContext(), responseBody, Toast.LENGTH_SHORT).show();
            }

        });
    }
}

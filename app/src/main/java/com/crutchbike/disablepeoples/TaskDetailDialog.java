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

import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class TaskDetailDialog extends DialogFragment {

    public JSONObject task;
    public ApiHTTPConnector HTTPConnector;
    public String TaskId;
    LayoutInflater inflater;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        inflater = getActivity().getLayoutInflater();
        View ActivityHandle = inflater.inflate(R.layout.activity_task_details, null);

        try {

            TextView Title = (TextView) ActivityHandle.findViewById(R.id.Dtitle);
            Title.setText(task.getString("about"));

            JSONObject user = task.getJSONObject("user");
            JSONObject city = user.getJSONObject("city");

            TextView Snippet = (TextView) ActivityHandle.findViewById(R.id.Dsnippet);
            Snippet.setText(user.getString("name") + " " + user.getString("surname") + "\r\n" + city.getString("name") + ", " + task.getString("address") + "\n\r" + task.getString("date"));

            TaskId = task.getString("id");

            SmartImageView imageView = (SmartImageView) ActivityHandle.findViewById(R.id.DetailsImg);
            if (user.getString("avatar_url").length() == 0) {
                imageView.setImageResource(R.drawable.noavatar);

            } else {
                imageView.setImageUrl(user.getString("avatar_url"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


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
        HTTPConnector.get("/tasks/" + TaskId + "/apply.json", new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Toast.makeText(inflater.getContext(), inflater.getContext().getString(R.string.TaskAccepted), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                Toast.makeText(inflater.getContext(), inflater.getContext().getString(R.string.ConnectionError), Toast.LENGTH_SHORT).show();
            }

        });
    }
}

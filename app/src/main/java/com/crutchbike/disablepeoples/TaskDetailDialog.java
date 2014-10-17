package com.crutchbike.disablepeoples;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by андрей on 18.10.2014.
 */
public class TaskDetailDialog extends DialogFragment {

    public JSONObject task;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View ActivityHandle = inflater.inflate(R.layout.activity_task_details, null);

        try {
            // Toast.makeText(inflater.getContext(), task.getString("date"), Toast.LENGTH_SHORT).show();

            TextView Title = (TextView) ActivityHandle.findViewById(R.id.Dtitle);
            Title.setText(task.getString("about"));

            TextView Snippet = (TextView) ActivityHandle.findViewById(R.id.Dsnippet);
            Snippet.setText(task.getString("address") + "\n\r" + task.getString("date"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        builder.setView(ActivityHandle)
                // Add action buttons
                .setPositiveButton(R.string.AcceptTask, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.BackToMap, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}

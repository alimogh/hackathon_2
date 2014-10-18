package com.crutchbike.disablepeoples;

import android.content.Context;
import android.widget.Toast;

import com.crutchbike.disablepeoples.Globals;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by андрей on 18.10.2014.
 */
public class EmergencyTask {

    public LatLng point;
    public Context BaseContext;


    public void SendEmergency(LatLng point, Context BaseContext) {
        this.point = point;
        this.BaseContext = BaseContext;
        AcceptTask();
    }

    public void AcceptTask() {

        String ReadyRequest = MakeTaskJSON(Globals.EmergencyTemplate, "null", Double.toString(point.latitude), Double.toString(point.longitude));

        Globals.HTTPApi.json("/tasks.json", ReadyRequest, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Toast.makeText(BaseContext, responseBody, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                Toast.makeText(BaseContext, responseBody, Toast.LENGTH_SHORT).show();
            }

        });
    }

    public String MakeTaskJSON(String about, String date, String lat, String lng) {
        //TODO:Add JSON generator (Maybe (It's fastest way!))
        String result = "{ \"task\": { \"about\": \"" + about + "\", \"date\": \"" + date + "\", \"lat\": \"" + lat + "\", \"lng\": \"" + lng + "\" } }";
        return result;
    }
}

package com.crutchbike.disablepeoples;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;


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
        //It's not a crunch, it's a future!
        String CombinedString = "{ 'task': { 'about': '" + about + "', 'date': '" + date + "', 'lat': '" + lat + "', 'lng': '" + lng + "' } }";
        Toast.makeText(BaseContext, CombinedString, Toast.LENGTH_SHORT).show();
        return CombinedString;
    }
}

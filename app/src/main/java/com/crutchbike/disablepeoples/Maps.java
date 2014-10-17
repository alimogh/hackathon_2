package com.crutchbike.disablepeoples;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Maps extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available
    Marker CurrentPos, LastClicked;
    Map<String, Marker> Markers = new HashMap<String, Marker>();
    Map<Marker, JSONObject> MarkersTasks = new HashMap<Marker, JSONObject>();


    Boolean TrackLocation = true;

    String LFUserLogin = "";
    String LFUserPassword = "";
    String LFUserSession = "";

    int FailCount = 3;

    public ApiHTTPConnector HTTPConnector = new ApiHTTPConnector();


    //Start marker update timer
    Timer UpdateTimer = new Timer();

    class UpdateTimeTask extends TimerTask {
        public void run() {
            mHandler.obtainMessage(1).sendToTarget();
        }
    }

    //Markers update timer handler
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mMap.getMyLocation() != null) {

                LatLng CurrentLLPos = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());

                CurrentPos.setPosition(CurrentLLPos);

                if (TrackLocation) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentLLPos, 15.5f), 500, null);
                }

                UpdateTasks();
            }
        }
    };


    public void UpdateTasks() {
        HTTPConnector.get("/tasks", new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                ParseTasks(responseBody);
                FailCount = 3;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                FailCount--;
                if (FailCount < 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.ConnectionError), Toast.LENGTH_SHORT).show();
                    goToLogin();
                    UpdateTimer.cancel();
                }
            }

        });
    }

    public void goToLogin() {
        finish();
    }

    public void ParseTasks(String jsonStr) {
        try {
            String temp = "";

            JSONObject ServerObject = new JSONObject(jsonStr);
            JSONArray tasks = ServerObject.getJSONArray("tasks");
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);

                Marker M = Markers.get(task.getString("id"));

                if (M == null) {
                    M = mMap.addMarker(new MarkerOptions().position(new LatLng(task.getDouble("lat"), task.getDouble("lng"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    Markers.put(task.getString("id"), M);
                    MarkersTasks.put(M, task);
                } else {
                    M.setPosition(new LatLng(task.getDouble("lat"), task.getDouble("lng")));
                    M.setTitle(task.getString("about"));
                    MarkersTasks.put(M, task);
                }
            }

        } catch (JSONException e) {
            //We want ignore corrupted data
            //e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get user info
        LFUserLogin = getIntent().getStringExtra("LFUserLogin");
        LFUserPassword = getIntent().getStringExtra("LFUserPassword");
        LFUserSession = getIntent().getStringExtra("LFUserSession");

        Toast.makeText(getBaseContext(), LFUserLogin, Toast.LENGTH_SHORT).show();


        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        UpdateTimer.schedule(new UpdateTimeTask(), 0, 1000);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                TrackLocation = true;
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                TrackLocation = false;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (MarkersTasks.get(marker) != null) {
                        TaskDetailDialog Detail = new TaskDetailDialog();
                        Detail.task = MarkersTasks.get(marker);
                    Detail.HTTPConnector = HTTPConnector;

                        Detail.show(getFragmentManager(), null);
                        return true;
                    }
                return false;
            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                Marker TaskPos = mMap.addMarker(new MarkerOptions().position(point).title(getString(R.string.CurrentMarkerTitle)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                AddTaskDialog AddTask = new AddTaskDialog();
                //Detail.task = MarkersTasks.get(marker);
                AddTask.point = point;
                AddTask.HTTPConnector = HTTPConnector;

                AddTask.show(getFragmentManager(), null);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    //Setup map settings
    private void setUpMap() {
        CurrentPos = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(getString(R.string.CurrentMarkerTitle)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.setMyLocationEnabled(true);

        //CurrentPos.setPosition(new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getAltitude()));

        // System.console().printf("%s",mMap.getMyLocation().toString());
    }


}

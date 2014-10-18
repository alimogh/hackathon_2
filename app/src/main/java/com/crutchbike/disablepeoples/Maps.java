package com.crutchbike.disablepeoples;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private ProgressDialog progressBar;


    Boolean TrackLocation = true;
    Boolean FirstStart = true;

    String LFUserLogin = "";
    String LFUserPassword = "";
    String LFUserSession = "";
    String LastJSON = "";

    int FailCount = 3;

    public ApiHTTPConnector HTTPConnector;//= new ApiHTTPConnector();


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

                if (FirstStart) {
                    TrackLocation = false;
                    FirstStart = false;
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
                FailCount = 8;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                FailCount--;
                if (FailCount < 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.ConnectionError), Toast.LENGTH_SHORT).show();
                    UpdateTimer.cancel();
                    goToLogin();
                }
            }

        });
    }

    public void goToLogin() {
        finish();
    }

    public void ParseTasks(String jsonStr) {
        try {
            LastJSON = jsonStr;

            JSONObject ServerObject = new JSONObject(jsonStr);
            JSONArray tasks = ServerObject.getJSONArray("tasks");
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);

                Marker M = Markers.get(task.getString("id"));


                if (M == null) {

                    float BeaconColor = BitmapDescriptorFactory.HUE_GREEN;
                    if (task.getString("date").length() == 0)
                        BeaconColor = BitmapDescriptorFactory.HUE_RED;

                    M = mMap.addMarker(new MarkerOptions().position(new LatLng(task.getDouble("lat"), task.getDouble("lng"))).icon(BitmapDescriptorFactory.defaultMarker(BeaconColor)));
                    Markers.put(task.getString("id"), M);
                    MarkersTasks.put(M, task);
                } else {
                    M.setPosition(new LatLng(task.getDouble("lat"), task.getDouble("lng")));
                    M.setTitle(task.getString("about"));
                    MarkersTasks.put(M, task);
                }
            }

        } catch (JSONException e) {
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HTTPConnector = Globals.HTTPApi;

        Toast.makeText(getBaseContext(), getString(R.string.Loading), Toast.LENGTH_SHORT).show();


        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        UpdateTimer.schedule(new UpdateTimeTask(), 0, 1000);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                TrackLocation = true;
                Toast.makeText(getBaseContext(), getString(R.string.MapHelp2), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (TrackLocation) {
                    Toast.makeText(getBaseContext(), getString(R.string.MapHelp3), Toast.LENGTH_LONG).show();
                    TrackLocation = false;
                }
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
                if (marker.equals(CurrentPos)) {
                    //Marker TaskPos = mMap.addMarker(new MarkerOptions().position(point).title(getString(R.string.CurrentMarkerTitle)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    AddTaskDialog AddTask = new AddTaskDialog();
                    AddTask.point = CurrentPos.getPosition();
                    AddTask.HTTPConnector = HTTPConnector;
                    AddTask.show(getFragmentManager(), null);
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
                AddTask.point = point;
                AddTask.HTTPConnector = HTTPConnector;
                AddTask.show(getFragmentManager(), null);
                TaskPos.remove();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onExitClick(MenuItem item) {
        finish();
    }

    public void onHelpClick(MenuItem item) {
        ShowMapHelp();
    }

    public void ShowMapHelp() {
        Toast.makeText(getBaseContext(), getString(R.string.MapHelp1), Toast.LENGTH_LONG).show();
    }


    public void onEmergencyClick(MenuItem item) {
        EmergencyTask AddTask = new EmergencyTask();
        AddTask.SendEmergency(CurrentPos.getPosition(), getBaseContext());
        // AddTask.show(getFragmentManager(), null);
        Marker EmergencyPos = mMap.addMarker(new MarkerOptions().position(CurrentPos.getPosition()).title(getString(R.string.EmergencyTemplate)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }


    public void onTaskListClick(MenuItem item) {
        Intent intent = new Intent(Maps.this, TaskList.class);
        intent.putExtra("JSONData", LastJSON);
        startActivity(intent);
    }

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
    }


}

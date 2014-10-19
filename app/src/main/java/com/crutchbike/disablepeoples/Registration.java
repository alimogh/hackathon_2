package com.crutchbike.disablepeoples;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;


public class Registration extends Activity {


    ApiHTTPConnector HTTPClient = new ApiHTTPConnector();

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //Setup global API connector
        Globals.HTTPApi = HTTPClient;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void goToMap() {
        Intent intent = new Intent(Registration.this, Maps.class);
        startActivity(intent);
    }

    //TODO:Registration
    public void onLoginButton(View v) {
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage(getString(R.string.LoginSpinner));
        progressBar.show();

        HTTPClient.get("/tasks", new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                //Toast.makeText(getBaseContext(), responseBody, Toast.LENGTH_SHORT).show();
                progressBar.hide();

                EditText UserLogin = (EditText) findViewById(R.id.Login);
                EditText UserPassword = (EditText) findViewById(R.id.Password);
                Globals.Login = UserLogin.getText().toString();
                Globals.Password = UserPassword.getText().toString();
                goToMap();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                progressBar.hide();
                Toast.makeText(getBaseContext(), getString(R.string.LoginConnectionError), Toast.LENGTH_SHORT).show();
            }

        });

    }
}

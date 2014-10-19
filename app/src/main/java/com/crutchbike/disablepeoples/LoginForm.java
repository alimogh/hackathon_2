package com.crutchbike.disablepeoples;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;


public class LoginForm extends Activity {


    ApiHTTPConnector HTTPClient = new ApiHTTPConnector();

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        //Setup global API connector
        Globals.HTTPApi = HTTPClient;
        Globals.CookManager = new PersistentCookieStore(getBaseContext());
        Globals.HTTPApi.setCookManager(Globals.CookManager);
        //Init storage
        Globals.Settings = getSharedPreferences(Globals.StorageName, Context.MODE_PRIVATE);
        Globals.Login = Globals.Settings.getString("login", "");
        Globals.Password = Globals.Settings.getString("password", "");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void goToMap() {
        Intent intent = new Intent(LoginForm.this, Maps.class);
        startActivity(intent);
    }


    public void onRegisterButton(View v) {
        Intent intent = new Intent(LoginForm.this, Registration.class);
        startActivity(intent);
    }

    public void onLoginButton(View v)
    {
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage(getString(R.string.LoginSpinner));
        progressBar.show();


        EditText UserLogin = (EditText) findViewById(R.id.Login);
        EditText UserPassword = (EditText) findViewById(R.id.Password);
        Globals.Login = UserLogin.getText().toString();
        Globals.Password = UserPassword.getText().toString();

        //TODO: Delete second line, uncomment first
        //HTTPClient.json("/users/sign_in.json","{'user': { 'email': '"+Globals.Login+"', 'password': '"+Globals.Password+"' ,'remember_me':'0'} }", new TextHttpResponseHandler() {
        HTTPClient.json("/users/sign_in.json", "{'user': { 'email': 'alexey2141@mail.ru', 'password': '12345678' ,'remember_me':'0'} }", new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {


                Globals.Settings.edit().putString("login", Globals.Login).putString("password", Globals.Password).apply();

                //TODO: Delete this line
                Toast.makeText(getBaseContext(), Integer.toString(statusCode) + ":" + responseBody, Toast.LENGTH_SHORT).show();
                progressBar.hide();

                Globals.EmergencyTemplate = getString(R.string.EmergencyTemplate);

                goToMap();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                progressBar.hide();
                Toast.makeText(getBaseContext(), getString(R.string.LoginConnectionError), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getBaseContext(), responseBody, Toast.LENGTH_SHORT).show();
            }

        });

    }
}

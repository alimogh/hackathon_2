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

import com.loopj.android.http.*;

import org.apache.http.*;


public class LoginForm extends Activity {


    ApiHTTPConnector HTTPClient = new ApiHTTPConnector();
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToMap(String session) {
        Intent intent = new Intent(LoginForm.this, Maps.class);
        EditText UserLogin = (EditText) findViewById(R.id.Login);
        EditText UserPassword = (EditText) findViewById(R.id.Password);

        intent.putExtra("UserLogin", UserLogin.getText());
        intent.putExtra("UserPassword", UserPassword.getText());
        intent.putExtra("UserSession", session);

        startActivity(intent);
    }

    public void onLoginButton(View v)
    {
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage(getString(R.string.LoginSpinner));
        progressBar.show();

        HTTPClient.get("/tasks", new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Toast.makeText(getBaseContext(), responseBody,
                        Toast.LENGTH_SHORT).show();
                progressBar.hide();
                goToMap("123");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                progressBar.hide();
                Toast.makeText(getBaseContext(), getString(R.string.LoginConnectionError), Toast.LENGTH_SHORT).show();
            }

        });

    }
}

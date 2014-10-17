package com.crutchbike.disablepeoples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.*;


public class LoginForm extends Activity {

    public class LoginHTTPClient {
        private static final String BASE_URL = "http://private-anon-0bd7be832-hackathon4.apiary-mock.com/";

        private AsyncHttpClient client = new AsyncHttpClient();

        public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public void get(String url, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), responseHandler);
        }

        public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(getAbsoluteUrl(url), params, responseHandler);
        }

        private String getAbsoluteUrl(String relativeUrl) {
            return BASE_URL + relativeUrl;
        }
    }

    LoginHTTPClient HTTPClient = new LoginHTTPClient();

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


    public void onLoginButton(View v)
    {
        HTTPClient.get("/tasks", new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Toast.makeText(getBaseContext(), responseBody,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

        });

        Intent intent = new Intent(LoginForm.this, Maps.class);
        startActivity(intent);
    }
}

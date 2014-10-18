package com.crutchbike.disablepeoples;

import android.app.DownloadManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.ByteArrayEntity;

import java.io.UnsupportedEncodingException;

/**
 * Created by андрей on 18.10.2014.
 */
public class ApiHTTPConnector {
    private static final String BASE_URL = "http://hackathon2.herokuapp.com/";//"http://private-anon-0bd7be832-hackathon4.apiary-mock.com/";

    private AsyncHttpClient client = new AsyncHttpClient();


    public void json(String url, String bodyAsJson, AsyncHttpResponseHandler responseHandler) {

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(bodyAsJson.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        //client.post(getAbsoluteUrl(url), params, responseHandler);
    }

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

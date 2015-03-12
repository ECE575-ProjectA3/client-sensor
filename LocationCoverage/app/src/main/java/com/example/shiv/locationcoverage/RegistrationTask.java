package com.example.shiv.locationcoverage;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivu on 3/10/2015.
 */
public class RegistrationTask extends AsyncTask<String,Void,Void> {

    String TAG = RegistrationTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {

        Log.d(TAG, "Started processing background task");
        /*HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.1.153:8080/registration");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            Log.d(TAG, "Executing http post");
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }*/

        JSONObject json = new JSONObject();


        HttpClient httpclient = new DefaultHttpClient();

        try {
            json.put("someKey", "someValue");
            HttpPost request = new HttpPost("http://192.168.1.153:8080/registration");
            StringEntity se = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(se);
            httpclient.execute(request);
        } catch (Exception ex) {
            // handle exception here
        } finally {
            //httpclient.close();
        }
        return null;
    }
}

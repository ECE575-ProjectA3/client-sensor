package com.example.shiv.locationcoverage;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;

/**
 * Created by shivu on 3/22/2015.
 */
public class ChangeOfStateHandler extends Handler {

    class CoverageParams {

        private double longitude;
        private double latitude;
        private int    signalLevel;

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setSignalLevel(int signalLevel) {
            this.signalLevel = signalLevel;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public int getSignalLevel() {
            return signalLevel;
        }
    }

    private static final String TAG = ChangeOfStateHandler.class.getSimpleName();

    private LocationInfo m_locationInfo = null;
    private CoverageInfo m_coverageInfo = null;

    public ChangeOfStateHandler(Looper l) {
        super(l);
    }

    private void postToServer(CoverageParams cParams) {

        HttpClient httpclient = new DefaultHttpClient();
        Gson gson = new Gson();
        try {
            HttpPost request = new HttpPost("http://192.168.1.153:8080/update");
            StringEntity se = new StringEntity(gson.toJson(cParams));
            request.addHeader("content-type", "application/json");
            request.setEntity(se);
            httpclient.execute(request);
        } catch (Exception ex) {
            // handle exception here
        } finally {
            //httpclient.close();
        }

    }

    public void handleMessage(Message msg) {

        ChangeOfStateMessage chngMsg = (ChangeOfStateMessage)msg.obj;
        if (chngMsg.isLocationChanged() == true) {
            LocationInfo lInfo = chngMsg.getLocationInfo();
            if (lInfo.isChnaged(m_locationInfo) && m_coverageInfo!=null) {
                Log.d(TAG, "Location has changed");
                Log.d(TAG,"Longitude" + lInfo.getLongitude() + ", latitude" + lInfo.getLatitude());
                Log.d(TAG,"signal strength " + m_coverageInfo.getSignalStrengthLevel());

                CoverageParams cParams = new CoverageParams();
                cParams.setLongitude(lInfo.getLongitude());
                cParams.setLatitude(lInfo.getLatitude());
                cParams.setSignalLevel(m_coverageInfo.getSignalStrengthLevel());
                postToServer(cParams);
            }
            m_locationInfo = lInfo;
        } else {
            CoverageInfo cInfo = chngMsg.getCoverageInfo();
            if (cInfo.isChanged(m_coverageInfo) && m_locationInfo != null) {
                Log.d(TAG, "Coverage has changed");
                Log.d(TAG,"Longitude" + m_locationInfo.getLongitude() + ", latitude" + m_locationInfo.getLatitude());
                Log.d(TAG,"Signal strength=" + cInfo.getSignalStrengthLevel());

                CoverageParams cParams = new CoverageParams();
                cParams.setLongitude(m_locationInfo.getLongitude());
                cParams.setLatitude(m_locationInfo.getLatitude());
                cParams.setSignalLevel(cInfo.getSignalStrengthLevel());
                postToServer(cParams);
            }
            m_coverageInfo = cInfo;
        }
    }
}

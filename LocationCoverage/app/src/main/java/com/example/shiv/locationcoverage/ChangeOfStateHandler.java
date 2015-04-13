package com.example.shiv.locationcoverage;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shivu on 3/22/2015.
 */
public class ChangeOfStateHandler extends Handler {

    class CoverageParams {

        private Double 	longitude;
        private Double 	latitude;
        private Integer signalLevel;
        private String  networkProviderName;
        private Double  dataSpeed;
        private Double  dataUploadSpeed;
        private Integer wifiSignalLevel;
        private Double  wifiDownloadSpeed;
        private Double  wifiUploadSpeed;
        private String 	dateTime;

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public void setSignalLevel(Integer signalLevel) {
            this.signalLevel = signalLevel;
        }

        public void setNetworkProviderName(String networkProviderName) { this.networkProviderName = networkProviderName;}

        public void setDataSpeed(Double dataSpeed) { this.dataSpeed = dataSpeed;}

        public void setDataUploadSpeed(Double dataUploadSpeed) { this.dataUploadSpeed = dataUploadSpeed;}

        public void setWifiSignalLevel(Integer wifiSignalLevel) { this.wifiSignalLevel = wifiSignalLevel;}

        public void setWifiDownloadSpeed(Double wifiDownloadSpeed) { this.wifiDownloadSpeed = wifiDownloadSpeed;}

        public void setWifiUploadSpeed(Double wifiUploadSpeed) { this.wifiUploadSpeed = wifiUploadSpeed;}

        public void setDateTime(String dateTime) {this.dateTime = dateTime;}

        public Double getLongitude() {
            return longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Integer getSignalLevel() {
            return signalLevel;
        }

        public String getNetworkProviderName() {return networkProviderName;}

        public Double getDataSpeed() { return dataSpeed;}

        public Double getDataUploadSpeed() { return dataUploadSpeed; }

        public Integer getWifiSignalLevel() { return wifiSignalLevel; }

        public Double getWifiDownloadSpeed() { return wifiDownloadSpeed;}

        public Double getWifiUploadSpeed() { return wifiUploadSpeed;}

        public String getDateTime() { return dateTime;}
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
            String url = "http://192.168.1.153:8080/"+cParams.getNetworkProviderName();
            HttpPost request = new HttpPost(url);
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

    private double measureSpeed() {
        double speed = 0.0;
        try {
            URL url = new URL("http://crevisio.com/photos/193-PiQA9tpp3/Crevisio-193-PiQA9tpp3.jpg");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            byte[] buffer = new byte[4096];
            int bufferLength = 0;
            int downloadSize = 0;
            long start= SystemClock.uptimeMillis();
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) { downloadSize += bufferLength;}
            if (downloadSize != totalSize) {
                Log.d(TAG, "size="+totalSize + "actual="+downloadSize);
            }
            long end=SystemClock.uptimeMillis();
            double downloadTime = (end - start) * 1000.00;
            speed = (totalSize * 8.00) / downloadTime;
            Log.d(TAG, "speed="+speed + "downloadtime="+downloadTime);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Error while opening url");
        } catch (IOException e) {
            Log.d(TAG, "Error while downloading file");
        }
        return speed;
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
                cParams.setNetworkProviderName(m_coverageInfo.getNetworkProviderName());
                cParams.setDataSpeed(measureSpeed());
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                cParams.setDateTime(dateFormat.format(date));
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
                cParams.setNetworkProviderName(cInfo.getNetworkProviderName());
                cParams.setDataSpeed(measureSpeed());
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                cParams.setDateTime(dateFormat.format(date));
                postToServer(cParams);
            }
            m_coverageInfo = cInfo;
        }
    }
}

package com.example.shiv.locationcoverage;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
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
public class StateChangeHandler extends Handler {

    private static final String TAG = "APP_DEBUG" + StateChangeHandler.class.getSimpleName();
    private LocationInfo m_locationInfo = null;
    private CoverageInfo m_coverageInfo = null;

    /* This class has to be in sync with server code */
    public class CoverageParams {
        private double latitude;
        private double longitude;
        private String carrierName;
        private String dateTime;

        private int    signalStrength;
        private double downloadSpeed;
        private double uploadSpeed;
        private int    wifiSignalStrength;
        private double wifiDownloadSpeed;
        private double wifiUploadSpeed;

        CoverageParams(double latitude, double longitude, String carrierName, String dateTime,
                       int signalStrength, double downloadSpeed, double uploadSpeed,
                       int wifiSignalStrength, double wifiDownloadSpeed, double wifiUploadSpeed) {

            setLatitude(latitude);
            setLongitude(longitude);
            setCarrierName(carrierName);
            setDateTime(dateTime);

            setSignalStrength(signalStrength);
            setDownloadSpeed(downloadSpeed);
            setUploadSpeed(uploadSpeed);

            setWifiSignalStrength(wifiSignalStrength);
            setWifiDownloadSpeed(wifiDownloadSpeed);
            setWifiUploadSpeed(wifiUploadSpeed);
        }
        CoverageParams(double latitude, double longitude, String carrierName, String dateTime,
                       int signalStrength, double downloadSpeed, double uploadSpeed) {

            setLatitude(latitude);
            setLongitude(longitude);
            setCarrierName(carrierName);
            setDateTime(dateTime);

            setSignalStrength(signalStrength);
            setDownloadSpeed(downloadSpeed);
            setUploadSpeed(uploadSpeed);
        }
        CoverageParams(double latitude, double longitude, String carrierName, String dateTime) {
            setLatitude(latitude);
            setLongitude(longitude);
            setCarrierName(carrierName);
            setDateTime(dateTime);
        }
        CoverageParams() {}

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        public void setCarrierName(String carrierName) {
            this.carrierName = carrierName;
        }
        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }
        public void setSignalStrength(int signalStrength) {
            this.signalStrength = signalStrength;
        }
        public void setDownloadSpeed(double downloadSpeed) {
            this.downloadSpeed = downloadSpeed;
        }
        public void setUploadSpeed(double uploadSpeed) {
            this.uploadSpeed = uploadSpeed;
        }
        public void setWifiSignalStrength(Integer wifiSignalStrength) {
            this.wifiSignalStrength = wifiSignalStrength;
        }
        public void setWifiDownloadSpeed(Double wifiDownloadSpeed) {
            this.wifiDownloadSpeed = wifiDownloadSpeed;
        }
        public void setWifiUploadSpeed(Double wifiUploadSpeed) {
            this.wifiUploadSpeed = wifiUploadSpeed;
        }

        public double getLongitude() {
            return this.longitude;
        }
        public double getLatitude() {
            return this.latitude;
        }
        public String getCarrierName() {
            return this.carrierName;
        }
        public String getDateTime() {
            return this.dateTime;
        }
        public int getSignalStrength() {
            return this.signalStrength;
        }
        public Double getDownloadSpeed() {
            return this.downloadSpeed;
        }
        public Double getUploadSpeed() {
            return this.uploadSpeed;
        }
        public Integer getWifiSignalStrength() {
            return wifiSignalStrength;
        }
        public Double getWifiDownloadSpeed() {
            return wifiDownloadSpeed;
        }
        public Double getWifiUploadSpeed() {
            return wifiUploadSpeed;
        }
    }

    public StateChangeHandler(Looper l) {
        super(l);
    }

    private void postToServer(CoverageParams cParams) {

        HttpClient httpclient = new DefaultHttpClient();
        Gson gson = new Gson();
        try {
            //String url = "http://192.168.1.153:8080/update";
            String url = "http://ece575a3.ddns.net:8080/update";
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
        // We measure speed by downloading huge image file and checking how much time it takes
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

        // We have received change of state message
        StateChangeMsg chngMsg = (StateChangeMsg)msg.obj;
        if (chngMsg.isLocationChanged() == true) {
            // Location has changed
            LocationInfo lInfo = chngMsg.getLocationInfo();
            // Post only if data has changed significantly
            if (lInfo.isChnaged(m_locationInfo) && m_coverageInfo!=null) {
                Log.d(TAG, "Location has changed");
                Log.d(TAG,"Longitude" + lInfo.getLongitude() + ", latitude" + lInfo.getLatitude());
                Log.d(TAG,"signal strength " + m_coverageInfo.getSignalStrengthLevel());

                CoverageParams cParams = new CoverageParams();
                cParams.setLongitude(lInfo.getLongitude());
                cParams.setLatitude(lInfo.getLatitude());
                cParams.setSignalStrength(m_coverageInfo.getSignalStrengthLevel());
                cParams.setCarrierName(m_coverageInfo.getNetworkProviderName());
                cParams.setDownloadSpeed(measureSpeed());
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                cParams.setDateTime(dateFormat.format(date));
                postToServer(cParams); // post the data to server
            }
            m_locationInfo = lInfo;
        } else {
            CoverageInfo cInfo = chngMsg.getCoverageInfo();
            // Post only if data has changed significantly
            if (cInfo.isChanged(m_coverageInfo) && m_locationInfo != null) {
                Log.d(TAG, "Coverage has changed");
                Log.d(TAG,"Longitude" + m_locationInfo.getLongitude() + ", latitude" + m_locationInfo.getLatitude());
                Log.d(TAG,"Signal strength=" + cInfo.getSignalStrengthLevel());

                CoverageParams cParams = new CoverageParams();
                cParams.setLongitude(m_locationInfo.getLongitude());
                cParams.setLatitude(m_locationInfo.getLatitude());
                cParams.setSignalStrength(cInfo.getSignalStrengthLevel());
                cParams.setCarrierName(cInfo.getNetworkProviderName());
                cParams.setDownloadSpeed(measureSpeed());
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

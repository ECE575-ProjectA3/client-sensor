package com.example.shiv.locationcoverage;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by shivu on 3/22/2015.
 */
public class ChangeOfStateHandler extends Handler {

    private static final String TAG = ChangeOfStateHandler.class.getSimpleName();

    private LocationInfo m_locationInfo = null;
    private CoverageInfo m_coverageInfo = null;

    public ChangeOfStateHandler(Looper l) {
        super(l);
    }

    public void handleMessage(Message msg) {
        ChangeOfStateMessage chngMsg = (ChangeOfStateMessage)msg.obj;
        if (chngMsg.isLocationChanged() == true) {
            LocationInfo lInfo = chngMsg.getLocationInfo();
            if (lInfo.isChnaged(m_locationInfo) && m_coverageInfo!=null) {
                Log.d(TAG, "Location has changed");
                Log.d(TAG,"Longitude" + lInfo.getLongitude() + ", latitude" + lInfo.getLatitude());
                Log.d(TAG,"signal strength " + m_coverageInfo.getSignalStrengthLevel());
            }
            m_locationInfo = lInfo;
        } else {
            CoverageInfo cInfo = chngMsg.getCoverageInfo();
            if (cInfo.isChanged(m_coverageInfo) && m_locationInfo != null) {
                Log.d(TAG, "Coverage has changed");
                Log.d(TAG,"Longitude" + m_locationInfo.getLongitude() + ", latitude" + m_locationInfo.getLatitude());
                Log.d(TAG,"Signal strength=" + cInfo.getSignalStrengthLevel());
            }
            m_coverageInfo = cInfo;
        }
    }
}

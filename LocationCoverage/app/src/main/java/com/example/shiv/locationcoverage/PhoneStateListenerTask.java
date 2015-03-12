package com.example.shiv.locationcoverage;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shivu on 3/11/2015.
 */
public class PhoneStateListenerTask extends PhoneStateListener {

    TelephonyManager mTmngr;
    private static final String TAG = PhoneStateListenerTask.class.getSimpleName();

    PhoneStateListenerTask(TelephonyManager tMngr) {
        mTmngr = tMngr;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        if (signalStrength.isGsm()) {
            super.onSignalStrengthsChanged(signalStrength);
            int nt = mTmngr.getNetworkType ();
            Log.d(TAG, "Network type = " + nt);
            // Reflection code starts from here
            // copy pasted from http://www.truiton.com/2014/08/android-onsignalstrengthschanged-lte-strength-measurement/
            try {
                Method[] methods = android.telephony.SignalStrength.class
                        .getMethods();
                for (Method mthd : methods) {
                    if (mthd.getName().equals("getLteSignalStrength")
                            || mthd.getName().equals("getLteRsrp")
                            || mthd.getName().equals("getLteRsrq")
                            || mthd.getName().equals("getLteRssnr")
                            || mthd.getName().equals("getLteCqi")) {
                        Log.d(TAG,
                                "onSignalStrengthsChanged: " + mthd.getName() + " "
                                        + mthd.invoke(signalStrength));
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //Log.d(TAG, "GSM Signal strength = " + ss);
        }
    }
}

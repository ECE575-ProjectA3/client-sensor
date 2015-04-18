package com.example.shiv.locationcoverage;

import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shivu on 3/11/2015.
 */
public class PhoneStateListenerTask extends PhoneStateListener {

    TelephonyManager mTmngr;
    StateChangeHandler m_chngHdlr;
    private static final String TAG = "APP_DEBUG" + PhoneStateListenerTask.class.getSimpleName();

    PhoneStateListenerTask(TelephonyManager tMngr,StateChangeHandler chngHdlr) {
        mTmngr = tMngr;
        m_chngHdlr = chngHdlr;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);

        //Log.d(TAG, "Coverage has changed");
        CoverageInfo cInfo = new CoverageInfo();
        cInfo.setNetworkProviderName(mTmngr.getNetworkOperatorName());
        StateChangeMsg chngMsg = new StateChangeMsg(false);
        chngMsg.setCoverageInfo(cInfo);
        Message msg = m_chngHdlr.obtainMessage();
        msg.obj = (Object)chngMsg;

        if (mTmngr.getNetworkType() == 13) {
            // Reflection code starts from here
            // copy pasted from http://www.truiton.com/2014/08/android-onsignalstrengthschanged-lte-strength-measurement/
            try {
                Method[] methods = android.telephony.SignalStrength.class.getMethods();
                for (Method mthd : methods) {
                    /*if (mthd.getName().equals("getLteSignalStrength")
                            || mthd.getName().equals("getLteRsrp")
                            || mthd.getName().equals("getLteRsrq")
                            || mthd.getName().equals("getLteRssnr")
                            || mthd.getName().equals("getLteCqi")) {
                        Log.d(TAG,
                                "onSignalStrengthsChanged: " + mthd.getName() + " "
                                        + mthd.invoke(signalStrength));
                    }*/
                    if (mthd.getName().equals("getLteSignalStrength")) {
                        int signalLevel = (Integer)mthd.invoke(signalStrength);
                        cInfo.setSignalStrengthLevel(signalLevel/8);
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
        }
        m_chngHdlr.sendMessage(msg);
    }
}

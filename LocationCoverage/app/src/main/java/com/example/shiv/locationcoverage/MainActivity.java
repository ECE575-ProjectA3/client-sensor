package com.example.shiv.locationcoverage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ToggleButton;
import android.util.Log;

import static android.app.PendingIntent.getActivity;


public class MainActivity extends Activity  {

    private HandlerThread myThread = null;
    private TelephonyManager tm = null;
    private LocationManager lm = null;
    private LocationListenerTask llTask = null;
    private PhoneStateListenerTask pslTask = null;
    private static final String TAG = "APP_DEBUG" + MainActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Creating telephony manager");
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Log.d(TAG,"Creating location manager");
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG,"Initialization complete...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            Log.d(TAG, "Starting worker thread");
            myThread = new HandlerThread("Worker Thread");
            myThread.start();

            // Get looper, handler, telephony manager and location manager
            Looper mLooper = myThread.getLooper();
            StateChangeHandler mHandler = new StateChangeHandler(mLooper);

            // Track signal strength changes
            Log.d(TAG, "Starting phone state tracking");
            pslTask = new PhoneStateListenerTask(tm,mHandler);
            tm.listen(pslTask,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

            // Track location change through GPS
            // Get location updates for every 2 minutes or distance changes by 10m
            Log.d(TAG, "Starting location tracking");
            llTask = new LocationListenerTask(mHandler);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000*2, 10,llTask );
        } else {
            Log.d(TAG, "Stopping location updates");
            lm.removeUpdates(llTask);
            llTask = null;

            Log.d(TAG, "Stopping phone state updates");
            tm.listen(pslTask, PhoneStateListener.LISTEN_NONE);
            pslTask = null;

            Log.d(TAG, "Stopping thread");
            if (myThread != null) {
                myThread.quitSafely();
                myThread = null;
            }
        }
    }
}

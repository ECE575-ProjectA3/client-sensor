package com.example.shiv.locationcoverage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;
import android.util.Log;

import static android.app.PendingIntent.getActivity;


public class MainActivity extends Activity  {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    private boolean isFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFirstRun()) {
            Log.d(TAG, "It is the first run");
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        }
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

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessage.class);
        intent.putExtra(EXTRA_MESSAGE, "Tracking location and signal in background");
    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            Log.d(TAG, "Starting async task");
            HandlerThread myThread = new HandlerThread("Worker Thread");
            myThread.start();
            Looper mLooper = myThread.getLooper();
            ChangeOfStateHandler mHandler = new ChangeOfStateHandler(mLooper);
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            tm.listen(new PhoneStateListenerTask(tm,mHandler), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000*2, 10, new LocationListenerTask(mHandler));
        } else {
            Log.d(TAG, "Stopping async task");
            //t.cancel(false);
        }
    }
}

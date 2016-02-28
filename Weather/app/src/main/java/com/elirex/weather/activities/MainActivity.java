package com.elirex.weather.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.elirex.weather.Utility;
import com.elirex.weather.fragments.DetailFragment;
import com.elirex.weather.fragments.ForecastFragment;
import com.elirex.weather.R;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // private final String FORECASTFARGMEN_TAG = "FFTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;
    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        // if(savedInstanceState == null) {
        //     getFragmentManager().beginTransaction()
        //             .add(R.id.container, new ForecastFragment(), FORECASTFARGMEN_TAG)
        //             .commit();
        // }
        if(findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
            getFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, new DetailFragment(),
                            DETAILFRAGMENT_TAG).commit();
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        if(location != null && !location.equals(mLocation)) {
            // ForecastFragment ff = (ForecastFragment) getFragmentManager()
            //         .findFragmentByTag(FORECASTFARGMEN_TAG);
            ForecastFragment ff = (ForecastFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);
            if(null != ff) {
                ff.onLocationChange();
            }
            mLocation = location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // String location = prefs.getString(
        //         getString(R.string.pref_location_key),
        //         getString(R.string.pref_location_default));
        String location = Utility.getPreferredLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }

    }

}

package com.elirex.weather.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.elirex.weather.Utility;
import com.elirex.weather.fragments.DetailFragment;
import com.elirex.weather.fragments.ForecastFragment;
import com.elirex.weather.R;
import com.elirex.weather.syncs.WeatherSyncAdapter;

public class MainActivity extends AppCompatActivity implements
        ForecastFragment.Callback {

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
        getSupportActionBar().setElevation(0f);
        ForecastFragment forecastFragment = ((ForecastFragment) getFragmentManager()
            .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);
        WeatherSyncAdapter.initializeSyncAdapter(this);
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
            DetailFragment df = (DetailFragment) getFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);
            if(null != df) {
                df.onLocationChange(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri dataUri) {
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.EXTRA_FORECAST, dataUri);
        if(mTwoPane) {
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_BUNDLE, args);
            startActivity(intent);
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
        return super.onOptionsItemSelected(item);
    }


}

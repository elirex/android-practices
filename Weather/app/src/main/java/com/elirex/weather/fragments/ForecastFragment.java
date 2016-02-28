package com.elirex.weather.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.elirex.weather.ForecastAdapter;
import com.elirex.weather.Utility;
import com.elirex.weather.activities.DetailActivity;
import com.elirex.weather.data.WeatherContract;
import com.elirex.weather.networks.FetchWeatherTask;
import com.elirex.weather.R;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class ForecastFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();


    private static final int FORECAST_LOADER = 0;

    private static final String FORECAST_COLUMNS[] = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG

    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;

    private View mRootView;
    private ListView mListView;
    // private ArrayAdapter<String> mForecastAdapter;
    private ForecastAdapter mForecastAdapter;
    private SwipeRefreshLayout mRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        setupUIComponents();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    public void onLocationChange() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    // @Override
    // public void onStart() {
    //     super.onStart();
    //     updateWeather();
    // }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry
                .buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        mRefresh.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    private void updateWeather() {
        // FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), this);
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        // weatherTask.execute(getLocation());
        weatherTask.execute(location);
    }

    private void setupUIComponents() {
        mListView = (ListView) mRootView.findViewById(R.id.listview_forecast);
        mListView.setOnItemClickListener(onItemClickListener);
        mRefresh = (SwipeRefreshLayout) mRootView
                .findViewById(R.id.refresh_listview_forecast);
        mRefresh.setOnRefreshListener(onRefreshForecastListListener);
        // mForecastAdapter = new ArrayAdapter<String>(getActivity(),
        //         R.layout.list_item_forecast,  R.id.list_item_forecast_textview);

        // String locationSetting = Utility.getPreferredLocation(getActivity());
        // String sortOrder = WeatherContract.WeatherEntry.CONTENT_URI + " ASC";
        // Uri weatherForLocationUri = WeatherContract.WeatherEntry
        //         .buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());
        // Cursor cur = getActivity().getContentResolver().query(
        //         weatherForLocationUri,
        //         null,
        //         null,
        //         null,
        //         sortOrder
        // );
        // mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        mListView.setAdapter(mForecastAdapter);
    }

    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // String forecast = mForecastAdapter.getItem(position);
                    // Bundle args = new Bundle();
                    // args.putString(DetailFragment.EXTRA_FORECAST, forecast);
                    // Intent intent = new Intent(getActivity(), DetailActivity.class);
                    // intent.putExtra(DetailActivity.EXTRA_BUNDLE, args);
                    // startActivity(intent);

                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    if(cursor != null) {
                        String locationSetting = Utility.getPreferredLocation(getActivity());

                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.setData(WeatherContract.WeatherEntry
                                .buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                        // intent.putExtra(DetailActivity.EXTRA_BUNDLE);
                        startActivity(intent);

                    }

                }
            };



    private SwipeRefreshLayout.OnRefreshListener onRefreshForecastListListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateWeather();
                }
            };

    private String getLocation() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        return prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.title_activity_detail));
    }

    // @Override
    // public void onData(List<String> list) {
    //     mRefresh.setRefreshing(false);
    //     if(list == null) return;
    //     mForecastAdapter.clear();
    //     mForecastAdapter.addAll(list);
    //     mForecastAdapter.notifyDataSetChanged();
    // }

    /* Class and Interface */
    public interface Callback {
        // DetailFragmentCallback for when an itemhas been selected.
        public void onItemSelected(Uri dataUri);
    }
}

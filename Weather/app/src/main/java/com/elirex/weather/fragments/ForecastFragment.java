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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.elirex.weather.ForecastAdapter;
import com.elirex.weather.Utility;
import com.elirex.weather.data.WeatherContract;
import com.elirex.weather.R;
import com.elirex.weather.syncs.WeatherSyncAdapter;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class ForecastFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

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

    private static final String SELECTED_KEY = "selected_position";

    private View mRootView;
    private ListView mListView;
    // private ArrayAdapter<String> mForecastAdapter;
    private ForecastAdapter mForecastAdapter;
    private SwipeRefreshLayout mRefresh;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        setupUIComponents();
        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void onLocationChange() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
        if(null != mForecastAdapter) {
            Cursor c = mForecastAdapter.getCursor();
            if(null != c) {
                c.moveToPosition(0);
                String posLat = c.getString(COL_COORD_LAT);
                String posLong = c.getString(COL_COORD_LONG);
                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString()
                            + ", no receiving apps installed!");
                }
            }
        }
    }

    // @Override
    // public void onStart() {
    //     super.onStart();
    //     updateWeather();
    // }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        String locationSetting = Utility.getPreferredLocation(getActivity());
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
        if(mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
        mRefresh.setRefreshing(false);
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if(mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_enable_notifications_key))) {
            updateEmptyView();
        }
    }

    private void updateWeather() {
        // FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), this);
        // FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        // String location = Utility.getPreferredLocation(getActivity());
        // weatherTask.execute(getLocation());
        // weatherTask.execute(location);

        // Intent intent = new Intent(getActivity(), WeatherService.class);
        // intent.putExtra(WeatherService.LOCATION_QUERY_EXTRA,
        //         Utility.getPreferredLocation(getActivity()));
        // getActivity().startService(intent);

        // Intent alarmIntent = new Intent(getActivity(), WeatherService.AlarmReceiver.class);
        // alarmIntent.putExtra(WeatherService.LOCATION_QUERY_EXTRA,
        //         Utility.getPreferredLocation(getActivity()));

        // Wrap in a pending intent which only fires once.
        // PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,
        //         alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        // AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        // Set the AlarmManager to wake up the system.
        // am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);

        WeatherSyncAdapter.syncImmediately(getActivity());
    }

    private void setupUIComponents() {
        View emptyView = mRootView.findViewById(R.id.textview_listview_forecast_empty);
        mListView = (ListView) mRootView.findViewById(R.id.listview_forecast);
        mListView.setEmptyView(emptyView);
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

                        // Intent intent = new Intent(getActivity(), DetailActivity.class);
                        //intent.setData(WeatherContract.WeatherEntry
                        //        .buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                        // intent.putExtra(DetailActivity.EXTRA_BUNDLE);
                        // startActivity(intent);
                        ((Callback) getActivity()).onItemSelected(
                                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                        locationSetting, cursor.getLong(COL_WEATHER_DATE)
                                ));

                    }
                    mPosition = position;

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

    private void updateEmptyView() {
        if(mForecastAdapter.getCount() == 0) {
            TextView textView = (TextView) getView().findViewById(R.id.textview_listview_forecast_empty);
            if(null != textView) {
                // If the cursor is empty,  why do we have an invalid location
                int message = R.string.empty_forecast_list;

                @WeatherSyncAdapter.LocationStatus int location =
                        Utility.getLocationStatus(getActivity());
                switch (location) {
                    case WeatherSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                        message = R.string.empty_forecast_list_server_down;
                        break;
                    case WeatherSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                        message = R.string.empty_forecast_list_server_error;
                        break;
                    case WeatherSyncAdapter.LOCATION_STATUS_INVALID:
                        message = R.string.empty_forecast_list_invalid_location;
                        break;
                    default:
                        if(!Utility.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_forecast_list_no_network;
                        }
                }
                textView.setText(message);
            }

        }
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

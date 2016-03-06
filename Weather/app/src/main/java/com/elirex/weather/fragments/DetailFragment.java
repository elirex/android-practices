package com.elirex.weather.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elirex.weather.R;
import com.elirex.weather.Utility;
import com.elirex.weather.data.WeatherContract;


/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class DetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = "#WeatherApp";
    public static final String EXTRA_FORECAST = "extra_weather_detail";

    private View mRootView;

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final int DETAIL_LOADER = 0;

    private static final String DETAIL_COLUMNS[] = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    private Uri mUri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if(args != null) {
            mUri = args.getParcelable(EXTRA_FORECAST);
        }

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);


        mIconView = (ImageView) mRootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) mRootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) mRootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) mRootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) mRootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) mRootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) mRootView.findViewById(R.id.detail_humidity_textview);
        mPressureView = (TextView) mRootView.findViewById(R.id.detail_pressure_textview);
        mWindView = (TextView) mRootView.findViewById(R.id.detail_wind_textview);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        // Bundle a = getArguments();
        // if(a == null) {
        //     return null;
        // }


        // Uri uri = Uri.parse(a.getString(EXTRA_FORECAST));

        // return new CursorLoader(
        //         getActivity(),
        //         uri,
        //         DETAIL_COLUMNS,
        //         null,
        //         null,
        //         null
        // );
        if(mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if(data == null || !data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);

        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(date);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);


        String description = data.getString(COL_WEATHER_DESC);
        mDescriptionView.setText(description);

        boolean isMetric = Utility.isMetric(getActivity());

        double high = data.getDouble(COL_WEATHER_MAX_TEMP);
        // String highString = Utility.formatTemperature(getActivity(), high, isMetric);
        String highString = Utility.formatTemperature(getActivity(), high);
        mHighTempView.setText(highString);

        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        // String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
        String lowString = Utility.formatTemperature(getActivity(), low);
        mLowTempView.setText(lowString);

        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDir = data.getFloat(COL_WEATHER_DEGREES);
        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDir));

        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // ShareActionProvider shareActionProvide = (ShareActionProvider)
        //         MenuItemCompat.getActionProvider(menuItem);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat
                .getActionProvider(menuItem);

        if(mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        // if(shareActionProvide != null) {
        //     shareActionProvide.setShareIntent(createShareForecastIntent());
        // } else {
        //     Log.d(LOG_TAG, "Share Action Provider is null?");
        // }
    }

    public void onLocationChange(String newLocation) {
        // Replace the uri, since the location has changed
        Uri uri = mUri;
        if(null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updateUri = WeatherContract.WeatherEntry
                    .buildWeatherLocationWithDate(newLocation, date);
            mUri = updateUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    private Intent createShareForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        intent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return intent;
    }

}

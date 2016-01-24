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
    // private String mForecastStr;

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final int DETAIL_LOADER = 0;

    private static final String FORECAST_COLUMNS[] = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // Bundle args = getArguments();
        // if(args != null) {
        //     mForecastStr = args.getString(EXTRA_FORECAST);
        // }

        // if(null != mForecastStr) {
        //     detail.setText(mForecastStr);
        // }
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
        Bundle a = getArguments();
        if(a == null) {
            return null;
        }


        Uri uri = Uri.parse(a.getString(EXTRA_FORECAST));

        return new CursorLoader(
                getActivity(),
                uri,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if(!data.moveToFirst()) {
            return;
        }

        String dataString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String weatherDescription = data.getString(COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        mForecast = String.format("%s - %s - %s/%s",
                dataString, weatherDescription, high, low);

        TextView detail = (TextView) mRootView.findViewById(R.id.detail);
        detail.setText(mForecast);

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

    private Intent createShareForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        intent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return intent;
    }

}

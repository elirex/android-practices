package com.elirex.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.elirex.weather.data.WeatherContract;
import com.elirex.weather.fragments.ForecastFragment;

import org.w3c.dom.Text;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/1/24.
 */
public class ForecastAdapter extends CursorAdapter {

    private Context mContext;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_forecast, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }

    private String formatHighLows(double hight, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String hightLowStr = Utility.formatTemperature(hight, isMetric) +
                "/" + Utility.formatTemperature(low, isMetric);
        return hightLowStr;
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        // int idx_max_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        // int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        // int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        // int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        // return Utility.formatDate(cursor.getLong(idx_date)) +
        //         " - " + cursor.getString(idx_short_desc) +
        //         " - " + highAndLow;
        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }



}

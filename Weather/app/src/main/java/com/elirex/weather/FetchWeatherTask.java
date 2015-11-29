package com.elirex.weather;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private Context mContext;
    private OnWeatherDataListener mDataListener;

    public FetchWeatherTask(Context context, OnWeatherDataListener listener) {
        mContext = context;
        mDataListener = listener;
    }

    @Override
    protected String[] doInBackground(String... params) {
        try {
            return getWeatherDataFromJson(retrieveWeatherData(params[0]), 7);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Parse JSON error", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response[]) {
        for(String str : response) {
            Log.d(LOG_TAG, "Response data:" + str);
        }

        List<String> list = null;
        if(response != null && response.length > 0) {
            list = Arrays.asList(response);
        }
        mDataListener.onData(list);
    }

    private String retrieveWeatherData(String location) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastBaseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        String queryParam = "q";
        String formatParam = "mode";
        String unitsParam = "units";
        String daysParam = "cnt";
        String appidParam = "APPID";

        String forecastJsonStr = null;
        try {
            Uri buildUri = Uri.parse(forecastBaseUrl).buildUpon()
                    .appendQueryParameter(queryParam, location)
                    .appendQueryParameter(formatParam, "json")
                    .appendQueryParameter(unitsParam, "metric")
                    .appendQueryParameter(daysParam, Integer.toString(7))
                    .appendQueryParameter(appidParam, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();
            URL url = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) return null;
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (buffer.length() == 0) {
                forecastJsonStr = null;
            } else {
                forecastJsonStr = buffer.toString();
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Request URL error", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Open url connection error", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);

                }
            }
            return forecastJsonStr;
        }

    }

    private String getReadableDateString(long time) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String formatHighLows(double high, double low, String unitType) {
        if(unitType.equals(mContext.getString(R.string.pref_units_imperial))){
            Log.d(LOG_TAG, " Imperial");
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        }


        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        return roundedHigh + "/" + roundedLow;
    }

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";
        final String OWM_DATE = "dt";

        JSONObject json = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = json.getJSONArray(OWM_LIST);


        String resultStrs[] = new String[numDays];
        int length = weatherArray.length();
        for(int i = 0; i < length; ++i) {
            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            day = getReadableDateString(dayForecast.getLong(OWM_DATE) * 1000);
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER)
                    .getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String unitType = prefs.getString(
                    mContext.getString(R.string.pref_units_key),
                    mContext.getString(R.string.pref_units_metric));
            Log.d(LOG_TAG, "Units:" + unitType);

            highAndLow = formatHighLows(high, low, unitType);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }


        return resultStrs;
    }

    public interface OnWeatherDataListener {

        public void onData(List<String> list);

    }

}


package com.elirex.weather.networks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.elirex.weather.BuildConfig;
import com.elirex.weather.R;
import com.elirex.weather.data.WeatherContract;

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
import java.util.Vector;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private Context mContext;
    private OnWeatherDataListener mDataListener;

    public FetchWeatherTask(Context context) {
        this(context, null);
    }

    public FetchWeatherTask(Context context, OnWeatherDataListener listener) {
        mContext = context;
        mDataListener = listener;
    }


    public long addLocation(String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        // First, check if the location with the city name exists int the db
        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[] {WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[] {locationSetting},
                null
        );

        if(locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pratty simple.
            // Fist create a ContentValues object to hold the data you want to insert
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows wht kind of value is being inserted.
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

            // Finally, insert location data into the database
            Uri insertedUri = mContext.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.
            // Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        return locationId;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            // return getWeatherDataFromJson(retrieveWeatherData(params[0]), params[0]);
            getWeatherDataFromJson(retrieveWeatherData(params[0]), params[0]);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Parse JSON error", e);
            // return null;
        }
        return null;
    }

    // @Override
    // protected void onPostExecute(String response[]) {
    //     for(String str : response) {
    //         Log.d(LOG_TAG, "Response data:" + str);
    //     }

    //     List<String> list = null;
    //     if(response != null && response.length > 0) {
    //         list = Arrays.asList(response);
    //     }
    //     mDataListener.onData(list);
    // }

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

    // private String getReadableDateString(long time) {
    //     SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
    //     return shortenedDateFormat.format(time);
    // }

    // private String formatHighLows(double high, double low) {

    //     SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    //     String unitType = sharedPrefs.getString(
    //             mContext.getString(R.string.pref_units_key),
    //             mContext.getString(R.string.pref_units_metric));



    //     if(unitType.equals(mContext.getString(R.string.pref_units_imperial))){
    //         Log.d(LOG_TAG, " Imperial");
    //         high = (high * 1.8) + 32;
    //         low = (low * 1.8) + 32;
    //     }


    //     long roundedHigh = Math.round(high);
    //     long roundedLow = Math.round(low);
    //     return roundedHigh + "/" + roundedLow;
    // }


    // String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
    //     String resultStrs[] = new String[cvv.size()];
    //     for(int i = 0; i < cvv.size(); ++i) {
    //         ContentValues weatherValues = cvv.elementAt(i);
    //         String highAndLow = formatHighLows(
    //                 weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP),
    //                 weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
    //         resultStrs[i] = getReadableDateString(
    //                 weatherValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)) +
    //                 " - " + weatherValues.getAsString(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC) +
    //                 " - " + highAndLow;
    //     }
    //     return resultStrs;
    // }

    private void getWeatherDataFromJson(String forecastJsonStr,
                                            String locationSetting) throws JSONException {
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        final String OWM_LATIUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        final String OWM_LIST = "list";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        final String OWM_TEMPEATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord .getDouble(OWM_LATIUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            dayTime = new Time();

            for(int i = 0; i < weatherArray.length(); ++i) {
                long dateTime;
                double presure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;

                JSONObject dayForecast = weatherArray.getJSONObject(i);
                dateTime = dayTime.setJulianDay(julianStartDay + i);

                presure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

                description = weatherObject.getString(OWM_DESCRIPTION);
                weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPEATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);

                ContentValues weatherValues = new ContentValues();
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, presure);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                cVVector.add(weatherValues);
            }

            int inserted = 0;
            if(cVVector.size() > 0) {
                ContentValues cvArray[] = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
            //     mContext.getContentResolver()
            //             .bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray);
            // }

            // String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
            // Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
            //         locationSetting, System.currentTimeMillis()
            // );

            // Cursor cur = mContext.getContentResolver().query(
            //         weatherForLocationUri,
            //         null,
            //         null,
            //         null,
            //         sortOrder);

            // cVVector = new Vector<ContentValues>(cur.getCount());
            // if(cur.moveToFirst()) {
            //     do {
            //         ContentValues cv = new ContentValues();
            //         DatabaseUtils.cursorRowToContentValues(cur, cv);
            //         cVVector.add(cv);
            //     } while (cur.moveToNext());
                inserted = mContext.getContentResolver().bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI, cvArray);
            }

            // Log.d(LOG_TAG, "FetcWeatherTask Complete. " + cVVector.size() + " Inserted");
            // String resultStrs[] = convertContentValuesToUXFormat(cVVector);
            // / return resultStrs;
            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // return null;
    }

    public interface OnWeatherDataListener {

        public void onData(List<String> list);

    }

}


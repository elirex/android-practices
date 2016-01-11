package com.elirex.weather.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by sheng on 1/10/16.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    private static final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertWeatherValues(long locationRowId) {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondInADay = 1000*60*60*24;
        ContentValues returnContentValues[] =
                new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for(int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; ++i,
                currentTestDate += millisecondInADay) {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, currentTestDate);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2 + 0.01 * (float) i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3 - 0.01 * (float) i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75 + i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65 - i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5 + 0.2 * (float) i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);
            returnContentValues[i] = weatherValues;
        }
        return  returnContentValues;
    }

    /* === Public Methods === */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecordsFromDB() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(WeatherContract.WeatherEntry.TABLE_NAME, null, null);
        db.delete(WeatherContract.LocationEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    /* === Protected Methods === */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }



}

package com.elirex.weather.data;

import android.test.AndroidTestCase;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/12/20.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public long insertLocation() {
        return -1L;
    }

}

package com.elirex.weather.syncs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/3/6.
 */
public class WeatherSyncService extends Service {

    private static final String LOG_TAG = WeatherSyncService.class.getSimpleName();

    private static final Object SYNC_ADAPTER_LOC = new Object();
    private static WeatherSyncAdapter sWeatherSyncAdapter = null;

    @Override
    public void onCreate() {
        // super.onCreate();
        Log.d(LOG_TAG, "onCreate - WeatehrSyncService");
        synchronized (SYNC_ADAPTER_LOC) {
            if(sWeatherSyncAdapter == null) {
                sWeatherSyncAdapter = new WeatherSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sWeatherSyncAdapter.getSyncAdapterBinder();
    }
}

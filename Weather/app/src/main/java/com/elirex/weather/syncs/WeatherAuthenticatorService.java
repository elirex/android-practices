package com.elirex.weather.syncs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/3/6.
 */
public class WeatherAuthenticatorService extends Service {

    private WeatherAuthenticator mAuthenticator;


    @Override
    public void onCreate() {
        // super.onCreate();
        mAuthenticator = new WeatherAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}

package com.elirex.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.Time;

import com.elirex.weather.syncs.WeatherSyncAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/1/24.
 */
public class Utility {

    private static final String DATE_FORMAT = "yyyyMMdd";

    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat;
        if(Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        String direction = "Unknown";
        if(degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if(degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if(degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if(degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if(degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if(degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if(degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if(degrees >= 292.5 && degrees <337.5) {
            direction = "NW";
        }
        return String.format(context.getString(windFormat), windSpeed, direction);

    }

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(Context context, double temperature) {
    // public static String formatTemperature(Context context, double temperature, boolean isMetric) {
        // double temp;
        // if(!isMetric) {
        //     temp = 9 * temperature / 5 + 32;
        // } else {
        //     temp = temperature;
        // }
        String suffix = "\u00B0";
        if(!isMetric(context)) {
            temperature = (temperature * 1.8) + 32;
        }
        // return context.getString(R.string.format_temperature, temp);
        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    public static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    public static String getFriendlyDayString(Context context, long dateInMillis) {
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        if(julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(formatId, today, getFormattedMonthDay(dateInMillis)));
        } else if(julianDay < currentJulianDay + 1) {
            return getDayName(context, dateInMillis);
        } else {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
        }
    }

    public static String getDayName(Context context, long dateInMillis) {
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if(julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if(julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static String getFormattedMonthDay(long dateInMillis) {
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    public static int getIconResourceForWeatherCondition(int weatherId) {
        if(weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if(weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if(weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if(weatherId == 511) {
            return R.drawable.ic_snow;
        } else if(weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if(weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if(weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if(weatherId == 781 ) {
            return R.drawable.ic_storm;
        } else if(weatherId == 800) {
            return R.drawable.ic_clear;
        } else if(weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if(weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return R.mipmap.ic_launcher;
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        if(weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if(weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if(weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if(weatherId == 511) {
            return R.drawable.art_snow;
        } else if(weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if(weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if(weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if(weatherId == 781 ) {
            return R.drawable.art_storm;
        } else if(weatherId == 800) {
            return R.drawable.art_clear;
        } else if(weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if(weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return R.mipmap.ic_launcher;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     *
      * @param context Context used to get the SharedPreferences
     * @return the location status integer type
     */
    @SuppressWarnings("ResourceType")
    public static @WeatherSyncAdapter.LocationStatus int getLocationStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_location_status_key),
                WeatherSyncAdapter.LOCATION_STATUS_UNKNOWN);
    }

    public static void resetLocationStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(context.getString(R.string.pref_location_status_key),
                WeatherSyncAdapter.LOCATION_STATUS_UNKNOWN).apply();
    }


}

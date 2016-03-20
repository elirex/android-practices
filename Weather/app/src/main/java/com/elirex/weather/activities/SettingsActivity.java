package com.elirex.weather.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.elirex.weather.R;
import com.elirex.weather.Utility;
import com.elirex.weather.data.WeatherContract;
import com.elirex.weather.syncs.WeatherSyncAdapter;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class SettingsActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_settings);
                if(savedInstanceState == null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, new SettingsFragment())
                                .commit();
                }
        }

        public static class SettingsFragment extends PreferenceFragment
                implements Preference.OnPreferenceChangeListener,
                SharedPreferences.OnSharedPreferenceChangeListener {

                @Override
                public void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        addPreferencesFromResource(R.xml.pref_general);
                        bindPreferenceSummaryToValue(findPreference(
                                getString(R.string.pref_location_key)));
                        bindPreferenceSummaryToValue(findPreference(
                                getString(R.string.pref_units_key)));
                }

                private void bindPreferenceSummaryToValue(Preference preference) {
                        preference.setOnPreferenceChangeListener(this);
                        onPreferenceChange(preference, PreferenceManager
                                        .getDefaultSharedPreferences(preference.getContext())
                                        .getString(preference.getKey(), "")
                        );
                }

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                        setPreferenceSummary(preference, newValue);
                        return true;
                }

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if(key.equals(getString(R.string.pref_location_key))) {
                                Utility.resetLocationStatus(getActivity());
                                WeatherSyncAdapter.syncImmediately(getActivity());
                        } else if(key.equals(getString(R.string.pref_units_key))) {
                                getActivity().getContentResolver().notifyChange(
                                        WeatherContract.WeatherEntry.CONTENT_URI,
                                        null
                                );
                        }
                }

                private void setPreferenceSummary(Preference preference, Object value) {
                        String stringValue = value.toString();
                        String key = preference.getKey();
                        if(preference instanceof ListPreference) {
                                ListPreference listPreference = (ListPreference) preference;
                                int prefIndex = listPreference.findIndexOfValue(stringValue);
                                if(prefIndex >= 0) {
                                        preference.setSummary(listPreference.getEntries()[prefIndex]);
                                }
                        } else {
                                preference.setSummary(stringValue);
                        }
                }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Nullable
        @Override
        public Intent getParentActivityIntent() {
                return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
}

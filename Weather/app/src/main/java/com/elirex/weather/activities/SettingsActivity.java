package com.elirex.weather.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.elirex.weather.R;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class SettingsActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                if(savedInstanceState == null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_forecast, new SettingsFragment())
                                .commit();
                }
        }

        public static class SettingsFragment extends PreferenceFragment
                implements Preference.OnPreferenceChangeListener {

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
                        String stringValue = newValue.toString();
                        if(preference instanceof ListPreference) {
                                ListPreference listPreference = (ListPreference) preference;
                                int prefIndex = listPreference.findIndexOfValue(stringValue);
                                if(prefIndex >= 0) {
                                        preference.setSummary(listPreference.getEntries()[prefIndex]);
                                }
                        } else {
                                preference.setSummary(stringValue);
                        }
                        return true;
                }
        }

}

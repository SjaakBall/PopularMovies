package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private final static String FAVORITES_STATE = "favorites_state";
    private final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private boolean favoritesAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "FLOW SettingsActivity.onCreate");

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(FAVORITES_STATE)) {
            favoritesAvailable = intent.getBooleanExtra(FAVORITES_STATE, false);
            Log.v(LOG_TAG, "FLOW SettingsActivity.onCreate intent.hasExtra(FAVORITES_STATE: " + favoritesAvailable);
        }

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));

        if (!favoritesAvailable) {
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.pref_sort_key));
            listPreference.setEntries(new String[]{"Highest rated","Most popular"});
            listPreference.setEntryValues(new String[]{"vote_average.desc", "popularity.desc"});
        }
    }

    private void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.v(LOG_TAG, "FLOW SettingsActivity.onPreferenceChange newValue: " + newValue);

        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }

        return true;
    }
}

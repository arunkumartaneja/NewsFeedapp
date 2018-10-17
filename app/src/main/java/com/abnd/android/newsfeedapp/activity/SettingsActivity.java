package com.abnd.android.newsfeedapp.activity;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

import com.abnd.android.newsfeedapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(sharedPrefs);
    }

    private void setTheme(SharedPreferences sharedPrefs) {
        boolean isNightModeOn = sharedPrefs.getBoolean(
                getString(R.string.setting_night_mode_key),
                Boolean.valueOf(getString(R.string.setting_night_mode_default_value)));

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            boolean isAutoNightModeOn = sharedPrefs.getBoolean(
                    getString(R.string.setting_auto_night_mode_key),
                    Boolean.valueOf(getString(R.string.setting_auto_night_mode_default_value)));
            if (isAutoNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    public static class NewsFeedPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference pageSize = findPreference(getString(R.string.settings_page_size_key));
            bindPreferenceSummaryToValue(pageSize);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference enableNightMode = findPreference(getString(R.string.setting_night_mode_key));
            bindPreferenceSummaryToValue(enableNightMode);

            Preference autoNightMode = findPreference(getString(R.string.setting_auto_night_mode_key));
            bindPreferenceSummaryToValue(autoNightMode);

            Preference showImage = findPreference(getString(R.string.settings_show_image_key));
            bindPreferenceSummaryToValue(showImage);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof SwitchPreference) {
                if (preference.getTitle().equals(getString(R.string.setting_night_mode_title))) {
                    updateNightMode(((SwitchPreference) preference).isChecked());
                }
            } else {
                onPreferenceChange(preference, preferences.getString(preference.getKey(), ""));
            }
        }

        private void updateNightMode(Boolean isChecked) {
            Preference autoNightMode = findPreference(getString(R.string.setting_auto_night_mode_key));
            if (isChecked) {
                autoNightMode.setEnabled(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                autoNightMode.setEnabled(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(newValue.toString());
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else if (preference.isEnabled() && preference.getTitle().equals(getString(R.string.setting_auto_night_mode_title))) {
                if (Boolean.valueOf(newValue.toString())) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                getActivity().recreate();
            } else if (preference.getTitle().equals(getString(R.string.setting_night_mode_title))) {
                updateNightMode(Boolean.valueOf(newValue.toString()));
                getActivity().recreate();
            } else {
                preference.setSummary(newValue.toString());
            }
            return true;
        }
    }
}

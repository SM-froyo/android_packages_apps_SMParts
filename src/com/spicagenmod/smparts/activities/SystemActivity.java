package com.spicagenmod.smparts.activities;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.spicagenmod.smparts.R;


public class SystemActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.system_settings_title_subhead);
        addPreferencesFromResource(R.xml.system_settings);
        findPreference("changelog").setSummary(getString(R.string.changelog_version) + ": " +
            SystemProperties.get("ro.modversion", getResources().getString(R.string.changelog_unknown)));
    }
}
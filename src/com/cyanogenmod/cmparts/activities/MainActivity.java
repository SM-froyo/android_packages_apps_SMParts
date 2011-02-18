package com.cyanogenmod.cmparts.activities;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.cyanogenmod.cmparts.R;


public class MainActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.cmparts);
/*        findPreference("changelog").setSummary(getString(R.string.changelog_version) + ": " +
            SystemProperties.get("ro.modversion", getResources().getString(R.string.changelog_unknown)));*/
    }
}
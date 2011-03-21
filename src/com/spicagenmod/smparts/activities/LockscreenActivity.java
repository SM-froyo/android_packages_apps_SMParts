package com.spicagenmod.smparts.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.spicagenmod.smparts.R;

public class LockscreenActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_settings_title);
        addPreferencesFromResource(R.xml.lockscreen_settings);
    }
}

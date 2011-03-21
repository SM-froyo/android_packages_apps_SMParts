package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class LockscreenTimeoutActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    /* Screen Lock */
    private static final String LOCKSCREEN_TIMEOUT_DELAY_PREF = "lockscreen_timeout_delay";
    private static final String LOCKSCREEN_SCREENOFF_DELAY_PREF = "lockscreen_screenoff_delay";

    private ListPreference mScreenLockTimeoutDelayPref;
    private ListPreference mScreenLockScreenOffDelayPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_timeout_title);
        addPreferencesFromResource(R.xml.lockscreen_timeout_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Screen Lock */
        mScreenLockTimeoutDelayPref = (ListPreference) prefSet
                .findPreference(LOCKSCREEN_TIMEOUT_DELAY_PREF);
        int timeoutDelay = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_LOCK_TIMEOUT_DELAY, 5000);
        mScreenLockTimeoutDelayPref.setValue(String.valueOf(timeoutDelay));
        mScreenLockTimeoutDelayPref.setOnPreferenceChangeListener(this);

        mScreenLockScreenOffDelayPref = (ListPreference) prefSet
                .findPreference(LOCKSCREEN_SCREENOFF_DELAY_PREF);
        int screenOffDelay = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_LOCK_SCREENOFF_DELAY, 0);
        mScreenLockScreenOffDelayPref.setValue(String.valueOf(screenOffDelay));
        mScreenLockScreenOffDelayPref.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mScreenLockTimeoutDelayPref) {
            int timeoutDelay = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_LOCK_TIMEOUT_DELAY,
                    timeoutDelay);
            return true;
        } else if (preference == mScreenLockScreenOffDelayPref) {
            int screenOffDelay = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_LOCK_SCREENOFF_DELAY, screenOffDelay);
            return true;
        }
        return false;
    }

}

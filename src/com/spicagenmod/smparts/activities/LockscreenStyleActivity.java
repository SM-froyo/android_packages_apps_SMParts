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

public class LockscreenStyleActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String LOCKSCREEN_MODE = "lockscreen_mode";
    private static final String LOCKSCREEN_CUSTOM_APP_TOGGLE = "lockscreen_custom_app_toggle";
    private static final String LOCKSCREEN_CUSTOM_APP_ACTIVITY = "lockscreen_custom_app_activity";
    private static final String LOCKSCREEN_ROTARY_UNLOCK_DOWN_TOGGLE = "lockscreen_rotary_unlock_down_toggle"; 
    private static final String LOCKSCREEN_ROTARY_HIDE_ARROWS_TOGGLE = "lockscreen_rotary_hide_arrows_toggle";

    private CheckBoxPreference mCustomAppTogglePref;
    private CheckBoxPreference mRotaryUnlockDownToggle;
    private CheckBoxPreference mRotaryHideArrowsToggle;

    private ListPreference mLockscreenModePref;

    private Preference mCustomAppActivityPref;

	private PackageManager mPackageManager;

    private static final int REQUEST_PICK_SHORTCUT = 1;
    private static final int REQUEST_PICK_APPLICATION = 2;
    private static final int REQUEST_CREATE_SHORTCUT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_style_title);
        addPreferencesFromResource(R.xml.lockscreen_style_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Lockscreen Custom App Toggle */
        mCustomAppTogglePref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_CUSTOM_APP_TOGGLE);
        mCustomAppTogglePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_CUSTOM_APP_TOGGLE, 0) == 1);

        mRotaryUnlockDownToggle = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ROTARY_UNLOCK_DOWN_TOGGLE);
        mRotaryUnlockDownToggle.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_ROTARY_UNLOCK_DOWN, 0) == 1);

        mRotaryHideArrowsToggle = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ROTARY_HIDE_ARROWS_TOGGLE);
        mRotaryHideArrowsToggle.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_ROTARY_HIDE_ARROWS, 0) == 1);

        /* Lockscreen Mode */
        mLockscreenModePref = (ListPreference) prefSet.findPreference(LOCKSCREEN_MODE);
        int lockscreenMode = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_MODE, 1);
        mLockscreenModePref.setValue(String.valueOf(lockscreenMode));
        mLockscreenModePref.setOnPreferenceChangeListener(this);

        mCustomAppActivityPref = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_APP_ACTIVITY);

    }

    @Override
    public void onResume() {
        super.onResume();

        mCustomAppActivityPref.setSummary(Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_CUSTOM_APP_TITLE));

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mCustomAppTogglePref) {
            value = mCustomAppTogglePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_APP_TOGGLE, value ? 1 : 0);
            return true;
        } else if (preference == mRotaryUnlockDownToggle) {
            value = mRotaryUnlockDownToggle.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ROTARY_UNLOCK_DOWN, value ? 1 : 0);
            return true;
        } else if (preference == mRotaryHideArrowsToggle) {
            value = mRotaryHideArrowsToggle.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ROTARY_HIDE_ARROWS, value ? 1 : 0);
            return true;
        } else if (preference == mCustomAppActivityPref) {
            pickShortcut();
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenModePref) {
            int lockscreenMode = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_MODE,
                    lockscreenMode);
            return true;
        }
        return false;
    }

    private void pickShortcut() {
        Intent picker=new Intent();
        picker.setClass(this, ActivityPickerActivity.class);
        startActivityForResult(picker, REQUEST_PICK_APPLICATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent activityIntent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
            String title = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
            if ((Settings.System.putString(getContentResolver(), 
                    Settings.System.LOCKSCREEN_CUSTOM_APP_ACTIVITY, 
                    activityIntent.toUri(0))) && 
                (Settings.System.putString(getContentResolver(), 
                    Settings.System.LOCKSCREEN_CUSTOM_APP_TITLE, 
                    title))) {
                mCustomAppActivityPref.setSummary(title);
            }
        }
    }
}

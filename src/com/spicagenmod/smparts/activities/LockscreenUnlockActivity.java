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

public class LockscreenUnlockActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String LOCKSCREEN_MENU_UNLOCK_PREF = "lockscreen_menu_unlock";
    private static final String LOCKSCREEN_QUICK_UNLOCK_CONTROL = "lockscreen_quick_unlock";
    private static final String LOCKSCREEN_HOLD_UNLOCK_PREF = "lockscreen_hold_unlock";
    private static final String LOCKSCREEN_DISABLE_UNLOCK_TAB = "lockscreen_disable_unlock_tab";

    private CheckBoxPreference mMenuUnlockPref;
    private CheckBoxPreference mQuickUnlockScreenPref;
    private CheckBoxPreference mHoldUnlockPref;
    private CheckBoxPreference mDisableUnlockTab;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_unlock_title);
        addPreferencesFromResource(R.xml.lockscreen_unlock_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Menu Unlock */
        mMenuUnlockPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_MENU_UNLOCK_PREF);
        mMenuUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.MENU_UNLOCK_SCREEN, 0) == 1);
        /* Hold Unlock */
        mHoldUnlockPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_HOLD_UNLOCK_PREF);
        mHoldUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.HOLD_UNLOCK_SCREEN, 0) == 1);

        /* Disabling of unlock tab on lockscreen */
        mDisableUnlockTab = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_DISABLE_UNLOCK_TAB);
        mDisableUnlockTab.setChecked(Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCKSCREEN_DISABLE_UNLOCK, 0) == 1);


        refreshDisableUnlock();

    }

    @Override
    public void onResume() {
        super.onResume();

        refreshDisableUnlock();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mMenuUnlockPref) {
            value = mMenuUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MENU_UNLOCK_SCREEN, value ? 1 : 0);
            refreshDisableUnlock();
            return true;
        } else if (preference == mHoldUnlockPref) {
            value = mHoldUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.HOLD_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mDisableUnlockTab) {
            value = mDisableUnlockTab.isChecked();
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_DISABLE_UNLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mQuickUnlockScreenPref) {
            value = mQuickUnlockScreenPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, value ? 1 : 0);
            return true;
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    void refreshDisableUnlock() {
        if (!doesUnlockAbilityExist()) {
            mDisableUnlockTab.setEnabled(false);
            mDisableUnlockTab.setChecked(false);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_DISABLE_UNLOCK, 0);
        } else {
            mDisableUnlockTab.setEnabled(true);
        }
    }

    private boolean doesUnlockAbilityExist() {
        final File mStoreFile = new File(Environment.getDataDirectory(), "/misc/lockscreen_gestures");
        boolean GestureCanUnlock = false;
        boolean menuCanUnlock = Settings.System.getInt(getContentResolver(),
                Settings.System.MENU_UNLOCK_SCREEN, 0) == 1;
        boolean holdCanUnlock = Settings.System.getInt(getContentResolver(),
                Settings.System.HOLD_UNLOCK_SCREEN, 0) == 1;
        GestureLibrary gl = GestureLibraries.fromFile(mStoreFile);
        if (gl.load()) {
            for (String name : gl.getGestureEntries()) {
                if ("UNLOCK___UNLOCK".equals(name)) {
                    GestureCanUnlock = true;
                    break;
                }
            }
        }
        if (GestureCanUnlock ||  menuCanUnlock || holdCanUnlock) {
            return true;
        } else {
            return false;
        }
    }

}

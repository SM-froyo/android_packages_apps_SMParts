package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

public class InputActivity extends PreferenceActivity {

    private static final String LOCKSCREEN_MUSIC_CONTROLS = "lockscreen_music_controls";
    private static final String TRACKBALL_WAKE_PREF = "pref_trackball_wake";
    private static final String TRACKBALL_UNLOCK_PREF = "pref_trackball_unlock";
    private static final String MENU_UNLOCK_PREF = "pref_menu_unlock";
    private static final String TRACKBALL_SCREEN_PREF = "pref_trackball_screen";

    private CheckBoxPreference mMusicControlPref;
    private CheckBoxPreference mTrackballWakePref;
    private CheckBoxPreference mTrackballUnlockPref;
    private CheckBoxPreference mMenuUnlockPref;
    private CheckBoxPreference mTrackballScreenPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.input_settings_title);
        addPreferencesFromResource(R.xml.input_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        Boolean mCanEnableTrackball = (getResources().getBoolean(R.bool.has_trackball) == true);

        /* Music Controls */
        mMusicControlPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_MUSIC_CONTROLS);
        mMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_MUSIC_CONTROLS, 0) == 1);

        /* Trackball Wake */
        mTrackballWakePref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_WAKE_PREF);
        mTrackballWakePref.setEnabled(mCanEnableTrackball);
        mTrackballWakePref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.TRACKBALL_WAKE_SCREEN, 0) == 1);

        /* Trackball Unlock */
        mTrackballUnlockPref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_UNLOCK_PREF);
        mTrackballUnlockPref.setEnabled(mCanEnableTrackball);
        mTrackballUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_UNLOCK_SCREEN, 0) == 1);

        /* Menu Unlock */
        mMenuUnlockPref = (CheckBoxPreference) prefSet.findPreference(MENU_UNLOCK_PREF);
        mMenuUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.MENU_UNLOCK_SCREEN, 0) == 1);

        /* Trackball While Screen On */
        mTrackballScreenPref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_SCREEN_PREF);
        mTrackballScreenPref.setEnabled(mCanEnableTrackball);
        mTrackballScreenPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_SCREEN_ON, 0) == 1);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mMusicControlPref) {
            value = mMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mTrackballWakePref) {
            value = mTrackballWakePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_WAKE_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mTrackballUnlockPref) {
            value = mTrackballUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mMenuUnlockPref) {
            value = mMenuUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MENU_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mTrackballScreenPref) {
            value = mTrackballScreenPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_SCREEN_ON, value ? 1 : 0);
        }

        return false;
    }
}

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

public class LockscreenActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String LOCKSCREEN_MUSIC_CONTROLS = "lockscreen_music_controls";
    private static final String LOCKSCREEN_ALWAYS_MUSIC_CONTROLS = "lockscreen_always_music_controls";
    private static final String DPAD_MUSIC_CONTROLS = "dpad_music_controls";
    private static final String LOCKSCREEN_NOW_PLAYING = "lockscreen_now_playing";
    private static final String LOCKSCREEN_ALBUM_ART = "lockscreen_album_art";
    private static final String MENU_UNLOCK_PREF = "pref_menu_unlock";
    private static final String BUTTON_CATEGORY = "pref_category_button_settings";
    private static final String LOCKSCREEN_STYLE_PREF = "pref_lockscreen_style";
    private static final String LOCKSCREEN_QUICK_UNLOCK_CONTROL = "lockscreen_quick_unlock_control";
    private static final String LOCKSCREEN_CUSTOM_APP_TOGGLE = "pref_lockscreen_custom_app_toggle";
    private static final String LOCKSCREEN_CUSTOM_APP_ACTIVITY = "pref_lockscreen_custom_app_activity";
    private static final String LOCKSCREEN_ROTARY_UNLOCK_DOWN_TOGGLE = "pref_lockscreen_rotary_unlock_down_toggle"; 
    private static final String LOCKSCREEN_ROTARY_HIDE_ARROWS_TOGGLE = "pref_lockscreen_rotary_hide_arrows_toggle";
    private static final String LOCKSCREEN_DISABLE_UNLOCK_TAB = "lockscreen_disable_unlock_tab";
    private static final String LOCKSCREEN_ALWAYS_BATTERY_INFO = "lockscreen_always_battery_info";

    private static final String HOLD_UNLOCK_PREF = "pref_hold_unlock";
    private static final String EXTENDED_LOCKSCREEN_PREF = "ext_ls_settings";

    private CheckBoxPreference mMusicControlPref;
    private CheckBoxPreference mAlwaysMusicControlPref;
    private CheckBoxPreference mDpadMusicControlPref;
    private CheckBoxPreference mNowPlayingPref;
    private CheckBoxPreference mAlbumArtPref;
    private CheckBoxPreference mLockAlwaysBatteryInfoPref;
    private CheckBoxPreference mMenuUnlockPref;
    private CheckBoxPreference mQuickUnlockScreenPref;
    private CheckBoxPreference mCustomAppTogglePref;
    private CheckBoxPreference mRotaryUnlockDownToggle;
    private CheckBoxPreference mRotaryHideArrowsToggle;
    private CheckBoxPreference mDisableUnlockTab;

    private CheckBoxPreference mHoldUnlockPref;
    private PreferenceScreen mExtendedLockscreenPref;
    private AlertDialog alertDialog;

    private ListPreference mLockscreenStylePref;

    private Preference mCustomAppActivityPref;

	private PackageManager mPackageManager;

    private static final int REQUEST_PICK_SHORTCUT = 1;
    private static final int REQUEST_PICK_APPLICATION = 2;
    private static final int REQUEST_CREATE_SHORTCUT = 3;
    
    /* Screen Lock */
    private static final String LOCKSCREEN_TIMEOUT_DELAY_PREF = "pref_lockscreen_timeout_delay";
    private static final String LOCKSCREEN_SCREENOFF_DELAY_PREF = "pref_lockscreen_screenoff_delay";

    private ListPreference mScreenLockTimeoutDelayPref;
    private ListPreference mScreenLockScreenOffDelayPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_settings_title_subhead);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
                
        /* Music Controls */
        mMusicControlPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_MUSIC_CONTROLS);
        mMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_MUSIC_CONTROLS, 1) == 1);

        /* Always Display Music Controls */
        mAlwaysMusicControlPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALWAYS_MUSIC_CONTROLS);
        mAlwaysMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_ALWAYS_MUSIC_CONTROLS, 0) == 1);

        /* D-Pad Music Controls */
        mDpadMusicControlPref = (CheckBoxPreference) prefSet.findPreference(DPAD_MUSIC_CONTROLS);
        mDpadMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_DPAD_MUSIC_CONTROLS, 0) == 1);

        /* Now Playing */
        mNowPlayingPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_NOW_PLAYING);
        mNowPlayingPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_NOW_PLAYING, 1) == 1);

        /* Album Art */
        mAlbumArtPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALBUM_ART);
        mAlbumArtPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_ALBUM_ART, 0) == 1);

        /* Always display battery stats */
        mLockAlwaysBatteryInfoPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALWAYS_BATTERY_INFO);
        mLockAlwaysBatteryInfoPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_ALWAYS_BATTERY_INFO, 0) == 1);

        /* Quick Unlock Screen Control */
        mQuickUnlockScreenPref = (CheckBoxPreference)
                prefSet.findPreference(LOCKSCREEN_QUICK_UNLOCK_CONTROL);
        mQuickUnlockScreenPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, 0) == 1);

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

        /* Extended Lockscreen preferences */
        mExtendedLockscreenPref = (PreferenceScreen) prefSet.findPreference(EXTENDED_LOCKSCREEN_PREF);

        /* Lockscreen Style */
        mLockscreenStylePref = (ListPreference) prefSet.findPreference(LOCKSCREEN_STYLE_PREF);
        int lockscreenStyle = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_STYLE_PREF, 1);
        mLockscreenStylePref.setValue(String.valueOf(lockscreenStyle));
        mLockscreenStylePref.setOnPreferenceChangeListener(this);
        if (lockscreenStyle==1) {
            mCustomAppTogglePref.setEnabled(true);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==2) {
            mCustomAppTogglePref.setEnabled(true);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==9) {
            mCustomAppTogglePref.setEnabled(false);
            mCustomAppTogglePref.setChecked(false);
            mExtendedLockscreenPref.setEnabled(true);
        }

        /* Menu Unlock */
        mMenuUnlockPref = (CheckBoxPreference) prefSet.findPreference(MENU_UNLOCK_PREF);
        mMenuUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.MENU_UNLOCK_SCREEN, 0) == 1);
        /* Hold Unlock */
        mHoldUnlockPref = (CheckBoxPreference) prefSet.findPreference(HOLD_UNLOCK_PREF);
        mHoldUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.HOLD_UNLOCK_SCREEN, 0) == 1);

        /* Disabling of unlock tab on lockscreen */
        mDisableUnlockTab = (CheckBoxPreference)
        prefSet.findPreference(LOCKSCREEN_DISABLE_UNLOCK_TAB);
        refreshDisableUnlock();

        PreferenceCategory buttonCategory = (PreferenceCategory)prefSet.findPreference(BUTTON_CATEGORY);

        mCustomAppActivityPref = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_APP_ACTIVITY);
        
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

    @Override
    public void onResume() {
        super.onResume();

        int lockscreenStyle = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_STYLE_PREF, 1);

        mCustomAppActivityPref.setSummary(Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_CUSTOM_APP_TITLE));


        refreshDisableUnlock();

        if (lockscreenStyle==1) {
            mCustomAppTogglePref.setEnabled(true);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==2) {
            mCustomAppTogglePref.setEnabled(true);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==9) {
            mCustomAppTogglePref.setEnabled(false);
            mCustomAppTogglePref.setChecked(false);
            mExtendedLockscreenPref.setEnabled(true);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mMusicControlPref) {
            value = mMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mDpadMusicControlPref) {
            value = mDpadMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_DPAD_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mLockAlwaysBatteryInfoPref) {
            value = mLockAlwaysBatteryInfoPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ALWAYS_BATTERY_INFO, value ? 1 : 0);
            return true;
        } else if (preference == mAlwaysMusicControlPref) {
            value = mAlwaysMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ALWAYS_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mNowPlayingPref) {
            value = mNowPlayingPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_NOW_PLAYING, value ? 1 : 0);
            return true;
        } else if (preference == mAlbumArtPref) {
            value = mAlbumArtPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ALBUM_ART, value ? 1 : 0);
            return true;
        } else if (preference == mQuickUnlockScreenPref) {
            value = mQuickUnlockScreenPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mCustomAppTogglePref) {
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
        } else if (preference == mMenuUnlockPref) {
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
        } else if (preference == mCustomAppActivityPref) {
            pickShortcut();
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenStylePref) {
            int lockscreenStyle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_STYLE_PREF,
                    lockscreenStyle);
            if (lockscreenStyle==1) {
                mCustomAppTogglePref.setEnabled(true);
                mExtendedLockscreenPref.setEnabled(false);
            } else if (lockscreenStyle==2) {
                mCustomAppTogglePref.setEnabled(true);
                mExtendedLockscreenPref.setEnabled(false);
            } else if (lockscreenStyle==9) {
                mCustomAppTogglePref.setEnabled(false);
                mCustomAppTogglePref.setChecked(false);
                mExtendedLockscreenPref.setEnabled(true);
            }
            return true;
        } else if (preference == mScreenLockTimeoutDelayPref) {
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

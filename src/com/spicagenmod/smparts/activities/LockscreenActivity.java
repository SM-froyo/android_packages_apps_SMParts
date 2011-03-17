package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
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
    private static final String LOCKSCREEN_PHONE_MESSAGING_TAB = "lockscreen_phone_messaging_tab";
    private static final String LOCKSCREEN_DISABLE_UNLOCK_TAB = "lockscreen_disable_unlock_tab";
    private static final String MESSAGING_TAB_APP = "pref_messaging_tab_app";
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
    private CheckBoxPreference mPhoneMessagingTabPref;
    private CheckBoxPreference mDisableUnlockTab;

    private CheckBoxPreference mHoldUnlockPref;
    private PreferenceScreen mExtendedLockscreenPref;
    private AlertDialog alertDialog;

    private ListPreference mLockscreenStylePref;

    private Preference mMessagingTabApp;
    private int mKeyNumber = 1;

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

        /* Lockscreen Phone Messaging Tab */
        mPhoneMessagingTabPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_PHONE_MESSAGING_TAB);
        mPhoneMessagingTabPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_PHONE_MESSAGING_TAB, 0) == 1);

        /* Extended Lockscreen preferences */
        mExtendedLockscreenPref = (PreferenceScreen) prefSet.findPreference(EXTENDED_LOCKSCREEN_PREF);

        /* Lockscreen Style */
        mLockscreenStylePref = (ListPreference) prefSet.findPreference(LOCKSCREEN_STYLE_PREF);
        int lockscreenStyle = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_STYLE_PREF, 1);
        mLockscreenStylePref.setValue(String.valueOf(lockscreenStyle));
        mLockscreenStylePref.setOnPreferenceChangeListener(this);
        if (lockscreenStyle==1) {
            mPhoneMessagingTabPref.setEnabled(true);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==2) {
            mPhoneMessagingTabPref.setEnabled(false);
            mPhoneMessagingTabPref.setChecked(false);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==9) {
            mPhoneMessagingTabPref.setEnabled(false);
            mPhoneMessagingTabPref.setChecked(false);
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
        if (!doesUnlockAbilityExist()) {
            mDisableUnlockTab.setEnabled(false);
            mDisableUnlockTab.setChecked(false);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GESTURES_DISABLE_UNLOCK, 0);
        } else {
            mDisableUnlockTab.setEnabled(true);
        }

        PreferenceCategory buttonCategory = (PreferenceCategory)prefSet.findPreference(BUTTON_CATEGORY);

        mMessagingTabApp = (Preference) prefSet.findPreference(MESSAGING_TAB_APP);
        
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

        mMessagingTabApp.setSummary(Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_MESSAGING_TAB_APP));
        if (!doesUnlockAbilityExist()) {
            mDisableUnlockTab.setEnabled(false);
            mDisableUnlockTab.setChecked(false);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GESTURES_DISABLE_UNLOCK, 0);
        } else {
            mDisableUnlockTab.setEnabled(true);
        }
        if (lockscreenStyle==1) {
            mPhoneMessagingTabPref.setEnabled(true);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==2) {
            mPhoneMessagingTabPref.setEnabled(false);
            mPhoneMessagingTabPref.setChecked(false);
            mExtendedLockscreenPref.setEnabled(false);
        } else if (lockscreenStyle==9) {
            mPhoneMessagingTabPref.setEnabled(false);
            mPhoneMessagingTabPref.setChecked(false);
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
        } else if (preference == mPhoneMessagingTabPref) {
            value = mPhoneMessagingTabPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_PHONE_MESSAGING_TAB, value ? 1 : 0);
            return true;
        } else if (preference == mMenuUnlockPref) {
            value = mMenuUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MENU_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mHoldUnlockPref) {
            value = mHoldUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.HOLD_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mDisableUnlockTab) {
            value = mDisableUnlockTab.isChecked();
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GESTURES_DISABLE_UNLOCK, value ? 1 : 0);
        } else if (preference == mMessagingTabApp) {
            pickShortcut(4);
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenStylePref) {
            int lockscreenStyle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_STYLE_PREF,
                    lockscreenStyle);
            if (lockscreenStyle==1) {
                mPhoneMessagingTabPref.setEnabled(true);
                mExtendedLockscreenPref.setEnabled(false);
            } else if (lockscreenStyle==2) {
                mPhoneMessagingTabPref.setEnabled(false);
                mPhoneMessagingTabPref.setChecked(false);
                mExtendedLockscreenPref.setEnabled(false);
            } else if (lockscreenStyle==9) {
                mPhoneMessagingTabPref.setEnabled(false);
                mPhoneMessagingTabPref.setChecked(false);
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

    private void pickShortcut(int keyNumber) {
        mKeyNumber = keyNumber;
        Bundle bundle = new Bundle();
        ArrayList<String> shortcutNames = new ArrayList<String>();
        shortcutNames.add(getString(R.string.group_applications));
        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
        shortcutIcons.add(ShortcutIconResource.fromContext(this, R.drawable.ic_launcher_application));
        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);
        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.select_custom_app_title));
        pickIntent.putExtras(bundle);
        startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_APPLICATION:
                    completeSetCustomApp(data);
                    break;
                case REQUEST_CREATE_SHORTCUT:
                    completeSetCustomShortcut(data);
                    break;
                case REQUEST_PICK_SHORTCUT:
                    processShortcut(data, REQUEST_PICK_APPLICATION, REQUEST_CREATE_SHORTCUT);
                    break;
            }
        }
    }

    void processShortcut(Intent intent, int requestCodeApplication, int requestCodeShortcut) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            startActivityForResult(pickIntent, requestCodeApplication);
        } else {
            startActivityForResult(intent, requestCodeShortcut);
        }
    }

    void completeSetCustomShortcut(Intent data) {
        Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        int keyNumber = mKeyNumber;
        if (keyNumber == 4){
            if (Settings.System.putString(getContentResolver(), Settings.System.LOCKSCREEN_MESSAGING_TAB_APP, intent.toUri(0))) {
                mMessagingTabApp.setSummary(intent.toUri(0));
            }
        }
    }

    void completeSetCustomApp(Intent data) {
        int keyNumber = mKeyNumber;
        if (keyNumber == 4){
            if (Settings.System.putString(getContentResolver(), Settings.System.LOCKSCREEN_MESSAGING_TAB_APP, data.toUri(0))) {
                mMessagingTabApp.setSummary(data.toUri(0));
            }
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

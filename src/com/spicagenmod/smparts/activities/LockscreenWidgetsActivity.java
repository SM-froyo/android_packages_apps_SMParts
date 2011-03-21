package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import java.io.File;
import java.util.ArrayList;

public class LockscreenWidgetsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String LOCKSCREEN_MUSIC_CONTROLS = "lockscreen_music_controls";
    private static final String LOCKSCREEN_ALWAYS_MUSIC_CONTROLS = "lockscreen_always_music_controls";
    private static final String LOCKSCREEN_DPAD_MUSIC_CONTROLS = "lockscreen_dpad_music_controls";
    private static final String LOCKSCREEN_NOW_PLAYING = "lockscreen_now_playing";
    private static final String LOCKSCREEN_ALBUM_ART = "lockscreen_album_art";
    private static final String LOCKSCREEN_ALWAYS_BATTERY = "lockscreen_always_battery";


    private CheckBoxPreference mMusicControlPref;
    private CheckBoxPreference mAlwaysMusicControlPref;
    private CheckBoxPreference mDpadMusicControlPref;
    private CheckBoxPreference mNowPlayingPref;
    private CheckBoxPreference mAlbumArtPref;
    private CheckBoxPreference mLockAlwaysBatteryPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_widgets_title);
        addPreferencesFromResource(R.xml.lockscreen_widget_settings);

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
        mDpadMusicControlPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_DPAD_MUSIC_CONTROLS);
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
        mLockAlwaysBatteryPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALWAYS_BATTERY);
        mLockAlwaysBatteryPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_ALWAYS_BATTERY_INFO, 0) == 1);

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
        } else if (preference == mLockAlwaysBatteryPref) {
            value = mLockAlwaysBatteryPref.isChecked();
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
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

}

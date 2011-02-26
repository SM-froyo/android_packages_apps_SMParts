package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class DisplayActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    /* Other */
    private static final String ROTATION_90_PREF = "pref_rotation_90";
    private static final String ROTATION_180_PREF = "pref_rotation_180";
    private static final String ROTATION_270_PREF = "pref_rotation_270";
    private static final String NOTIF_LIGHT_PREF = "notif_light";
    private static final String NOTIF_LIGHT_TIME_PREF = "notif_light_time";

    private CheckBoxPreference mRotation90Pref;
    private CheckBoxPreference mRotation180Pref;
    private CheckBoxPreference mRotation270Pref;

    private CheckBoxPreference mNotificationLighterPref;
    private ListPreference mNotificationLighterTimePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.display_settings_title_subhead);
        addPreferencesFromResource(R.xml.display_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Rotation */
        mRotation90Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_90_PREF);
        mRotation180Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_180_PREF);
        mRotation270Pref = (CheckBoxPreference) prefSet.findPreference(ROTATION_270_PREF);
        int mode = Settings.System.getInt(getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION_MODE, 5);
        mRotation90Pref.setChecked((mode & 1) != 0);
        mRotation180Pref.setChecked((mode & 2) != 0);
        mRotation270Pref.setChecked((mode & 4) != 0);

        /* Notification lighter */
        mNotificationLighterPref = (CheckBoxPreference) prefSet.findPreference(NOTIF_LIGHT_PREF);
        mNotificationLighterPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_SCREEN_LIGHTER, 0) == 1);
        mNotificationLighterTimePref = (ListPreference) prefSet.findPreference(NOTIF_LIGHT_TIME_PREF);
        mNotificationLighterTimePref.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mRotation90Pref ||
            preference == mRotation180Pref ||
            preference == mRotation270Pref) {
            int mode = 0;
            if (mRotation90Pref.isChecked()) mode |= 1;
            if (mRotation180Pref.isChecked()) mode |= 2;
            if (mRotation270Pref.isChecked()) mode |= 4;
            Settings.System.putInt(getContentResolver(),
                     Settings.System.ACCELEROMETER_ROTATION_MODE, mode);
        }

        if (preference == mNotificationLighterPref) {
            value = mNotificationLighterPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_SCREEN_LIGHTER, value ? 1 : 0);
        }

        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationLighterTimePref) {
            int lighterTime = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATION_SCREEN_LIGHTER_TIME,
                    lighterTime);
            return true;
        }
        return false;
    }

}

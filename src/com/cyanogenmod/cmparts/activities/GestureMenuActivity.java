package com.cyanogenmod.cmparts.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import java.util.List;
import com.cyanogenmod.cmparts.R;

public class GestureMenuActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "Gestures";

    private static final String LOCKSCREEN_GESTURES_ENABLE = "lockscreen_gestures_enable";
    private static final String LOCKSCREEN_GESTURES_TRAIL = "lockscreen_gestures_trail";
    private static final String LOCKSCREEN_GESTURES_SENSITIVITY = "lockscreen_gestures_sensitivity";

    private CheckBoxPreference mGesturesEnable;
    private CheckBoxPreference mGesturesTrail;
    private ListPreference mGesturesSensitivity;


    public static boolean updatePreferenceToSpecificActivityOrRemove(Context context,
            PreferenceGroup parentPreferenceGroup, String preferenceKey, int flags) {

        Preference preference = parentPreferenceGroup.findPreference(preferenceKey);
        if (preference == null) {
            return false;
        }

        Intent intent = preference.getIntent();
        if (intent != null) {
            // Find the activity that is in the system image
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                ResolveInfo resolveInfo = list.get(i);
                if ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)
                        != 0) {
                    preference.setIntent(new Intent().setClassName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name));
                    return true;
                }
            }
        }
        parentPreferenceGroup.removePreference(preference);
        return true;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.gesture_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        mGesturesEnable = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_GESTURES_ENABLE);
        mGesturesTrail = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_GESTURES_TRAIL);
        mGesturesSensitivity = (ListPreference) prefSet.findPreference(LOCKSCREEN_GESTURES_SENSITIVITY);

        final PreferenceGroup parentPreference = getPreferenceScreen();
        parentPreference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void updateToggles() {
            mGesturesEnable.setChecked(Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.LOCKSCREEN_GESTURES_ENABLED, 0) != 0);
            mGesturesTrail.setChecked(Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.LOCKSCREEN_GESTURES_TRAIL, 0) != 0);
            mGesturesSensitivity.setValue(Integer.toString(Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.LOCKSCREEN_GESTURES_SENSITIVITY, 0)));
            mGesturesSensitivity.setSummary(mGesturesSensitivity.getEntry());
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (LOCKSCREEN_GESTURES_ENABLE.equals(key)) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_GESTURES_ENABLED,
                    mGesturesEnable.isChecked() ? 1 : 0);
        } else if (LOCKSCREEN_GESTURES_TRAIL.equals(key)) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_GESTURES_TRAIL,
                    mGesturesTrail.isChecked() ? 1 : 0);
        } else if (LOCKSCREEN_GESTURES_SENSITIVITY.equals(key)) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_GESTURES_SENSITIVITY,
                    Integer.parseInt(mGesturesSensitivity.getValue()));
            mGesturesSensitivity.setSummary(mGesturesSensitivity.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToggles();
    }
}

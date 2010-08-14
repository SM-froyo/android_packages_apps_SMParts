package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

public class SoundActivity extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private static final String NOTIFICATIONS_FOCUS = "notif-focus";
    private static final String NOTIFICATIONS_SPEAKER = "notif-speaker";
    private static final String NOTIFICATIONS_ATTENUATION = "notif-attn";
    private static final String NOTIFICATIONS_LIMITVOL = "notif-limitvol";
    private static final String RINGS_SPEAKER = "ring-speaker";
    private static final String RINGS_ATTENUATION = "ring-attn";
    private static final String RINGS_LIMITVOL = "ring-limitvol";
    private static final String ALARMS_SPEAKER = "alarm-speaker";
    private static final String ALARMS_ATTENUATION = "alarm-attn";
    private static final String ALARMS_LIMITVOL = "alarm-limitvol";

    private static final String PREFIX = "persist.sys.";

    private static String getKey(String suffix) {
        return PREFIX + suffix;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.sound_title);
        addPreferencesFromResource(R.xml.sound_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        CheckBoxPreference p = (CheckBoxPreference) prefSet.findPreference(NOTIFICATIONS_FOCUS);
        p.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATIONS_AUDIO_FOCUS, 1) != 0);
        p.setOnPreferenceChangeListener(this);

        p = (CheckBoxPreference) prefSet.findPreference(NOTIFICATIONS_SPEAKER);
        p.setChecked(SystemProperties.getBoolean(getKey(NOTIFICATIONS_SPEAKER), false));
        p.setOnPreferenceChangeListener(this);

        p = (CheckBoxPreference) prefSet.findPreference(RINGS_SPEAKER);
        p.setChecked(SystemProperties.getBoolean(getKey(RINGS_SPEAKER), false));
        p.setOnPreferenceChangeListener(this);

        p = (CheckBoxPreference) prefSet.findPreference(ALARMS_SPEAKER);
        p.setChecked(SystemProperties.getBoolean(getKey(ALARMS_SPEAKER), false));
        p.setOnPreferenceChangeListener(this);

        ListPreference lp = (ListPreference) prefSet.findPreference(NOTIFICATIONS_ATTENUATION);
        lp.setValue(String.valueOf(SystemProperties.getInt(getKey(NOTIFICATIONS_ATTENUATION), 6)));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(this);

        lp = (ListPreference) prefSet.findPreference(RINGS_ATTENUATION);
        lp.setValue(String.valueOf(SystemProperties.getInt(getKey(RINGS_ATTENUATION), 6)));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(this);

        lp = (ListPreference) prefSet.findPreference(ALARMS_ATTENUATION);
        lp.setValue(String.valueOf(SystemProperties.getInt(getKey(ALARMS_ATTENUATION), 6)));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(this);

        lp = (ListPreference) prefSet.findPreference(NOTIFICATIONS_LIMITVOL);
        lp.setValue(String.valueOf(SystemProperties.getInt(getKey(NOTIFICATIONS_LIMITVOL), 1)));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(this);

        lp = (ListPreference) prefSet.findPreference(RINGS_LIMITVOL);
        lp.setValue(String.valueOf(SystemProperties.getInt(getKey(RINGS_LIMITVOL), 1)));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(this);

        lp = (ListPreference) prefSet.findPreference(ALARMS_LIMITVOL);
        lp.setValue(String.valueOf(SystemProperties.getInt(getKey(ALARMS_LIMITVOL), 1)));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key.equals(NOTIFICATIONS_FOCUS)) {
            Settings.System.putInt(getContentResolver(),
                Settings.System.NOTIFICATIONS_AUDIO_FOCUS, getBoolean(newValue) ? 1 : 0);
        }
        else if (key.equals(NOTIFICATIONS_SPEAKER) ||
                key.equals(RINGS_SPEAKER) ||
                key.equals(ALARMS_SPEAKER)) {
            SystemProperties.set(getKey(key), getBoolean(newValue) ? "1" : "0");
        } else {
            SystemProperties.set(getKey(key), String.valueOf(getInt(newValue)));
            mHandler.sendMessage(mHandler.obtainMessage(0, key));
        }

        return true;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.obj != null) {
                        ListPreference p = (ListPreference) findPreference(msg.obj.toString());
                        p.setSummary(p.getEntry());
                    }
                    break;
            }
        }
    };

    private boolean getBoolean(Object o) {
        return Boolean.valueOf(o.toString());
    }

    private int getInt(Object o) {
        return Integer.valueOf(o.toString());
    }
}

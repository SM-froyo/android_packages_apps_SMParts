package com.cyanogenmod.cmparts.activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.cyanogenmod.cmparts.R;

public class BatteryClockActivity extends PreferenceActivity {

    /* Display Battery Percentage */
    private static final String BATTERY_PERCENTAGE_PREF = "pref_battery_percentage";
    private CheckBoxPreference mBatteryPercentagePref;
    /* Battery Percentage Font Color */
    private static final String UI_BATTERY_PERCENT_COLOR = "battery_status_color_title";
    private Preference mBatteryPercentColorPreference;
    /* Display Clock */
    private static final String UI_SHOW_STATUS_CLOCK = "show_status_clock";
    private CheckBoxPreference mShowClockPref;
    /* Clock Font Color */
    private static final String UI_CLOCK_COLOR = "clock_color";
    private Preference mClockColorPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.bc_title);
        addPreferencesFromResource(R.xml.battery_clock_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Battery Percentage */
        mBatteryPercentagePref = (CheckBoxPreference) prefSet.findPreference(BATTERY_PERCENTAGE_PREF);
        mBatteryPercentagePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.BATTERY_PERCENTAGE_STATUS_ICON, 0) == 1);
        /* Battery Precentage Color */
        mBatteryPercentColorPreference = prefSet.findPreference(UI_BATTERY_PERCENT_COLOR);
        /* Show Clock */
        mShowClockPref = (CheckBoxPreference) prefSet.findPreference(UI_SHOW_STATUS_CLOCK);
        mShowClockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SHOW_STATUS_CLOCK, 1) != 0);
        /* Clock Color */
        mClockColorPref = prefSet.findPreference(UI_CLOCK_COLOR);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        /* Display Battery Percentage */
        if (preference == mBatteryPercentagePref) {
            value = mBatteryPercentagePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BATTERY_PERCENTAGE_STATUS_ICON, value ? 1 : 0);
        }
        /* Battery Font Color */
        else if (preference == mBatteryPercentColorPreference) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                mBatteryColorListener,
                readBatteryColor());
            cp.show();
        }
        /* Display Clock */
        else if (preference == mShowClockPref) {
            value = mShowClockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SHOW_STATUS_CLOCK, value ? 1 : 0);
        }
        /* Clock Font Color */
        else if (preference == mClockColorPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                mClockFontColorListener,
                readClockFontColor());
            cp.show();          
        }
        return true;
    }
    /* Battery Font Color */
    ColorPickerDialog.OnColorChangedListener mBatteryColorListener = 
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(), Settings.System.BATTERY_PERCENTAGE_STATUS_COLOR, color);
            }
    };
    private int readBatteryColor() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.BATTERY_PERCENTAGE_STATUS_COLOR);
        }
        catch (SettingNotFoundException e) {
            return -1;
        }
    }
    /* Clock Font Color */
    ColorPickerDialog.OnColorChangedListener mClockFontColorListener = 
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(), Settings.System.CLOCK_COLOR, color);
            }
    };
    private int readClockFontColor() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.CLOCK_COLOR);
        }
        catch (SettingNotFoundException e) {
            return -16777216;
        }
    }
}

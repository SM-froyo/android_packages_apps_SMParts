package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

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
import android.provider.Settings.SettingNotFoundException;

public class InputActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String END_BUTTON_PREF = "end_button";

    private ListPreference mEndButtonPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.input_settings_title_subhead);
        addPreferencesFromResource(R.xml.input_settings);

		PreferenceScreen prefSet = getPreferenceScreen();

		mEndButtonPref = (ListPreference) prefSet.findPreference(END_BUTTON_PREF);
		mEndButtonPref.setOnPreferenceChangeListener(this);
		

		try {
            mEndButtonPref.setValueIndex(Settings.System.getInt(getContentResolver(), Settings.System.END_BUTTON_BEHAVIOR));
        } catch (SettingNotFoundException e) {
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mEndButtonPref) {
			try {
				int val = Integer.parseInt(newValue.toString());
				Settings.System.putInt(getContentResolver(), Settings.System.END_BUTTON_BEHAVIOR, val);
				mEndButtonPref.setValueIndex(val);	
        	} catch (NumberFormatException e) {
        	}
        }
        return false;
    }

}

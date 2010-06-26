package com.cyanogenmod.cmparts.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.provider.SettingsProvider;

public class TrackballActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TRACKBALL_WAKE_PREF = "pref_trackball_wake";
    private static final String TRACKBALL_UNLOCK_PREF = "pref_trackball_unlock";

    private CheckBoxPreference mTrackballWakePref;
    private CheckBoxPreference mTrackballUnlockPref;

    private String[] DEFAULT_PROJECTION = new String[] {
            SettingsProvider.Constants._ID,
            SettingsProvider.Constants.KEY,
            SettingsProvider.Constants.VALUE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.trackball_settings_title);
        addPreferencesFromResource(R.xml.trackball_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Trackball Wake */
        mTrackballWakePref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_WAKE_PREF);
        mTrackballWakePref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.TRACKBALL_WAKE_SCREEN, 0) == 1);

        /* Trackball Unlock */
        mTrackballUnlockPref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_UNLOCK_PREF);
        mTrackballUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_UNLOCK_SCREEN, 0) == 1);

        final PreferenceGroup parentPreference = getPreferenceScreen();
        parentPreference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /*
	private void writeToProvider(String key, String value) {
		ContentValues values = new ContentValues();

		values.put(Constants.KEY, key);
		values.put(Constants.VALUE, value);

		int id = getRowId(key);
		if (id == -1) {
			getContentResolver().insert(SettingsProvider.CONTENT_URI, values);
		} else {
			final String selection = Constants.KEY + "='" + key + "'";
			Uri rowuri = Uri.parse("content://"+SettingsProvider.AUTHORITY+"/"+SettingsProvider.TABLE_NAME+"/"+id);
			getContentResolver().update(rowuri, values, selection, null);
		}
	}

	private int getInt(String key, int def) {
		String value = getString(key);
		try {
			return value != null ? Integer.parseInt(value) : def;
		} catch (NumberFormatException e) {
			return def;
		}
	}

	private String getString(String key) {
		String value = null;
		Cursor c = null;
		String selection = "key='"+key+"'";
		try {
			c = managedQuery(SettingsProvider.CONTENT_URI, DEFAULT_PROJECTION, selection, null, null);
			if (c != null && c.moveToNext()) {
				value = c.getString(2);
			}
		} catch (SQLException e) {
			// return null
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return value;
	}

	private int getRowId(String key) {
		String selection = "key='"+key+"'";
		Cursor cur = managedQuery(SettingsProvider.CONTENT_URI, DEFAULT_PROJECTION, selection, null, null);
		cur.moveToFirst();
		if (cur.getCount() == 0) {
			return -1;
		} else {
			return cur.getInt(0);
		}
	}
     */

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mTrackballWakePref) {
            value = mTrackballWakePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_WAKE_SCREEN, value ? 1 : 0);
        } else if (preference == mTrackballUnlockPref) {
            value = mTrackballUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_UNLOCK_SCREEN, value ? 1 : 0);
        }
        return true;
    }
}

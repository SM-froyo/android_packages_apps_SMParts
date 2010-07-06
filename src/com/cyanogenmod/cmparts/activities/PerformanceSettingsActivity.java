package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import java.io.File;

/**
 * Performance Settings
 * 
 * TODO: Add JIT toggle
 */
public class PerformanceSettingsActivity extends PreferenceActivity {

    private static final String COMPCACHE_PREF = "pref_compcache";
    
    private CheckBoxPreference mCompcachePref;

    private int swapAvailable = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(R.string.performance_settings_title);
        addPreferencesFromResource(R.xml.performance_settings);
        
        PreferenceScreen prefSet = getPreferenceScreen();
        
        mCompcachePref = (CheckBoxPreference) prefSet.findPreference(COMPCACHE_PREF);
        
        if (isSwapAvailable()) {
            mCompcachePref.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.Secure.COMPCACHE_ENABLED, 0) == 1);
        } else {
            prefSet.removePreference(mCompcachePref);
        }
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCompcachePref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.Secure.COMPCACHE_ENABLED, mCompcachePref.isChecked() ? 1 : 0);
            return true;
        }
        return false;
    }
    
    /**
     * Check if swap support is available on the system
     */
    private boolean isSwapAvailable() {
        if (swapAvailable < 0) {
            swapAvailable = new File("/proc/swaps").exists() ? 1 : 0;
        }
        return swapAvailable > 0;
    }

}

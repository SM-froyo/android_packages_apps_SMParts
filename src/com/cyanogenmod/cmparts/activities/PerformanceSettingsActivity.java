package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import java.io.File;

/**
 * Performance Settings
 */
public class PerformanceSettingsActivity extends PreferenceActivity {

    private static final String COMPCACHE_PREF = "pref_compcache";
    
    private static final String JIT_PREF = "pref_jit_mode";
    
    private static final String JIT_ENABLED = "int:jit";
    
    private static final String JIT_DISABLED = "int:fast";
    
    private static final String JIT_PROP = "dalvik.vm.execution-mode";
    
    private CheckBoxPreference mCompcachePref;

    private CheckBoxPreference mJitPref;
    
    private int swapAvailable = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(R.string.performance_settings_title);
        addPreferencesFromResource(R.xml.performance_settings);
        
        PreferenceScreen prefSet = getPreferenceScreen();
        
        mCompcachePref = (CheckBoxPreference) prefSet.findPreference(COMPCACHE_PREF);
        if (isSwapAvailable()) {
            mCompcachePref.setChecked(Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.COMPCACHE_ENABLED, 0) == 1);
        } else {
            prefSet.removePreference(mCompcachePref);
        }

        mJitPref = (CheckBoxPreference) prefSet.findPreference(JIT_PREF);
        String jitMode = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.JIT_MODE);
        if (jitMode == null || "".equals(jitMode)) {
            jitMode = SystemProperties.get(JIT_PROP, JIT_ENABLED);
        }
        mJitPref.setChecked(JIT_ENABLED.equals(jitMode));
        
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCompcachePref) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.COMPCACHE_ENABLED, mCompcachePref.isChecked() ? 1 : 0);
            return true;
        }
        
        if (preference == mJitPref) {
            Settings.Secure.putString(getContentResolver(),
                    Settings.Secure.JIT_MODE, mJitPref.isChecked() ? JIT_ENABLED : JIT_DISABLED);
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

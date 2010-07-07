package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import java.io.File;

/**
 * Performance Settings
 */
public class PerformanceSettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String COMPCACHE_PREF = "pref_compcache";
    
    private static final String COMPCACHE_PROP = "persist.service.compcache";
    
    private static final String JIT_PREF = "pref_jit_mode";
    
    private static final String JIT_ENABLED = "int:jit";
    
    private static final String JIT_DISABLED = "int:fast";
    
    private static final String JIT_PERSIST_PROP = "persist.sys.jit-mode";
    
    private static final String JIT_PROP = "dalvik.vm.execution-mode";
    
    private static final String HEAPSIZE_PREF = "pref_heapsize";
    
    private static final String HEAPSIZE_PROP = "dalvik.vm.heapsize";
    
    private static final String HEAPSIZE_PERSIST_PROP = "persist.sys.vm.heapsize";
    
    private static final String HEAPSIZE_DEFAULT = "16m";
    
    private CheckBoxPreference mCompcachePref;

    private CheckBoxPreference mJitPref;
    
    private ListPreference mHeapsizePref;
    
    private AlertDialog alertDialog;
    
    private int swapAvailable = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(R.string.performance_settings_title);
        addPreferencesFromResource(R.xml.performance_settings);
        
        PreferenceScreen prefSet = getPreferenceScreen();
        
        mCompcachePref = (CheckBoxPreference) prefSet.findPreference(COMPCACHE_PREF);
        if (isSwapAvailable()) {
            mCompcachePref.setChecked(SystemProperties.getBoolean(COMPCACHE_PROP, false));
        } else {
            prefSet.removePreference(mCompcachePref);
        }

        mJitPref = (CheckBoxPreference) prefSet.findPreference(JIT_PREF);
        String jitMode = SystemProperties.get(JIT_PERSIST_PROP,
                SystemProperties.get(JIT_PROP, JIT_ENABLED));
        mJitPref.setChecked(JIT_ENABLED.equals(jitMode));
        
        mHeapsizePref = (ListPreference) prefSet.findPreference(HEAPSIZE_PREF);
        mHeapsizePref.setValue(SystemProperties.get(HEAPSIZE_PERSIST_PROP, 
                SystemProperties.get(HEAPSIZE_PROP, HEAPSIZE_DEFAULT)));
        mHeapsizePref.setOnPreferenceChangeListener(this);
    
        // Set up the warning
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.performance_settings_warning_title);
        alertDialog.setMessage(getResources().getString(R.string.performance_settings_warning));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        
        alertDialog.show();
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCompcachePref) {
            SystemProperties.set(COMPCACHE_PROP, mCompcachePref.isChecked() ? "1" : "0");
            return true;
        }
        
        if (preference == mJitPref) {
            SystemProperties.set(JIT_PERSIST_PROP, 
                    mJitPref.isChecked() ? JIT_ENABLED : JIT_DISABLED);
        }
        
        return false;
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHeapsizePref) {
            if (newValue != null) {
                SystemProperties.set(HEAPSIZE_PERSIST_PROP, (String)newValue);
                return true;
            }
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

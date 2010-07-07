package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.content.pm.IPackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

public class ApplicationActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String BROTIPS_PREF= "pref_brotips";

    private static final String INSTALL_LOCATION_PREF = "pref_install_location";
    
    private static final String LOG_TAG = "CMParts";
    
    private ListPreference mInstallLocationPref;
    
    private CheckBoxPreference mBrotipsPref;

    private IPackageManager mPm;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        if (mPm == null) {
            Log.wtf(LOG_TAG, "Unable to get PackageManager!");
        }
        
        setTitle(R.string.performance_settings_title);
        addPreferencesFromResource(R.xml.application_settings);
        
        PreferenceScreen prefSet = getPreferenceScreen();
        
        mBrotipsPref = (CheckBoxPreference) prefSet.findPreference(BROTIPS_PREF);
        mBrotipsPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.BROTIPS, 0) == 1);
        
        mInstallLocationPref = (ListPreference) prefSet.findPreference(INSTALL_LOCATION_PREF);
        String installLocation = "0";
        try {
            installLocation = String.valueOf(mPm.getInstallLocation());
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Unable to get default install location!", e);
        }
        mInstallLocationPref.setValue(installLocation);
        mInstallLocationPref.setOnPreferenceChangeListener(this);
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mBrotipsPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BROTIPS, mBrotipsPref.isChecked() ? 1 : 0);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mInstallLocationPref) {
            if (newValue != null) {
                try {
                    mPm.setInstallLocation(Integer.valueOf((String)newValue));
                    return true;
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "Unable to get default install location!", e);
                }
            }
        }
        return false;
    }

}

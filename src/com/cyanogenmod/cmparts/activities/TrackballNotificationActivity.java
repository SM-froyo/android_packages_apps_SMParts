package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import java.io.Serializable;
import android.util.Log;

public class TrackballNotificationActivity extends PreferenceActivity {

	public static String[] mPackage;
	public String mPackageSource;
	
	public String[] getArray(String mGetFrom) {
		String[] temp = mGetFrom.split("\\|");
		return temp;
	}
	
	public String createString(String[] mArray) {
		int i;
		String temp = "";
		for(i = 0; i < mArray.length; i++) {
			temp = temp + "|" + mArray[i];
		}
		return temp;
	}
	
	public String[] getPackageAndColor(String mString) {
		String[] temp;
		temp = mString.split("=");
		return temp;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mPackageSource = Settings.System.getString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS);

        //Settings.System.putString(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION_MODE, Package);
//        PackageArray[0] = color;
//        getResources(d);
    }

}
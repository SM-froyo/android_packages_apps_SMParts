package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import java.io.Serializable;
import android.util.Log;

public class TrackballNotificationActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	private static final String GOOGLE_VOICE_PREF = "pref_google_voice";
	private static final String MMS_PREF= "pref_mms";
	private static final String GMAIL_PREF= "pref_gmail";
	private static final String EMAIL_PREF= "pref_email";
	private static final String TWITTER_PREF= "pref_twitter";

	private ListPreference mGoogleVoice;
	private ListPreference mMms;
	private ListPreference mGmail;
	private ListPreference mEmail;
	private ListPreference mTwitter;

	public static String[] mPackage;
	public String mPackageSource;

	public boolean isNull(String mString) {
		if(mString == null || mString.matches("null") 
		|| mString.length() == 0
		|| mString.matches("|")) {
			return true;
		} else {
			return false;
		}
	}

	public static String[] getArray(String mGetFrom) {
		if(mGetFrom == null || mGetFrom.matches("|") || mGetFrom.length() == 0) {
			String[] tempfalse = new String[20];
			return tempfalse;
		}
		String[] temp;
		temp = mGetFrom.split("\\|");
		for(int x = 0; x < temp.length; x++)
			Log.i("GetArray", "X="+x+" temp="+temp[x]);
		return temp;
	}

	public String createString(String[] mArray) {
		int i;
		String temp = "";
		for(i = 0; i < mArray.length; i++) {
			if(mArray[i] == "" || mArray[i] == null || mArray[i].matches("null") || mArray[i].length() == 0)
				continue;

			temp = temp + "|" + mArray[i];
			Log.i("createString", "String:" +temp+ " mArray: " +mArray[i]);
		}
		return temp;
	}

	public static String[] getPackageAndColor(String mString) {
		if(mString == null || mString.matches("=") || mString.length() == 0 || mString.matches("null")) {
			return null;
		}
		String[] temp;
		temp = mString.split("=");
		Log.i("getPackageAndColor", "String: "+mString);
		return temp;
	}

	public void addPackage(String pkg, String color) {
		String stringtemp = Settings.System.getString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS);
		String[] temp = getArray(stringtemp);
		int i;
		String[] temp2;
		temp2 = new String[temp.length];
		boolean found = false;
		for(i = 0; i < temp.length; i++) {
			temp2 = getPackageAndColor(temp[i]);
			if(temp2 == null) {
				continue;
			}
			Log.i("addPackNew", "Temp2 Pkg="+pkg+" temp2[0]="+temp2[0]+" temp2[1]="+temp2[1]);
			if(temp2[0].matches(pkg)) {
				temp2[1] = color;
				found = true;
				break;
			}
		}
		if(found) {
			String tempcolor = temp2[0] +"="+temp2[1];
			temp[i] = tempcolor;
		} else {
			int x = 0;
			//Get the last one
			for(x = 0; x < temp.length; x++) {
				Log.i("addPackNew", "X="+x+" temp="+temp[x]);
				if(temp[x] == null || temp[x].length() == 0 || temp[x].matches("null"))
					break;
			}
			String tempcolor = pkg+"="+color;
			temp[x] = tempcolor;
		}
		Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS, createString(temp));
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(R.string.trackball_notifications_title);
        addPreferencesFromResource(R.xml.trackball_notifications);

        PreferenceScreen prefSet = getPreferenceScreen();
        
        mGoogleVoice = (ListPreference) prefSet.findPreference(GOOGLE_VOICE_PREF);
        mGoogleVoice.setOnPreferenceChangeListener(this);
        
        mMms = (ListPreference) prefSet.findPreference(MMS_PREF);
        mMms.setOnPreferenceChangeListener(this);
        mGmail = (ListPreference) prefSet.findPreference(GMAIL_PREF);
        mGmail.setOnPreferenceChangeListener(this);
        mEmail = (ListPreference) prefSet.findPreference(EMAIL_PREF);
        mEmail.setOnPreferenceChangeListener(this);
        mTwitter = (ListPreference) prefSet.findPreference(TWITTER_PREF);
        mTwitter.setOnPreferenceChangeListener(this);
//        mGoogleVoice = (ListPreference) prefSet.findPreference(RECENT_APPS_NUM_PREF);
//        mGoogleVoice.setOnPreferenceChangeListener(this);
//        mPackageSource = Settings.System.getString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS);

        //Settings.System.putString(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION_MODE, Package);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mGoogleVoice) {
        	Log.i("TrackballColor", objValue.toString());
            addPackage("com.google.android.apps.googlevoice", objValue.toString());
        }
        if (preference == mMms) {
        	//addPackage("com.google.android.apps.googlevoice", objValue.toString());
        }
        if (preference == mGmail) {
        	addPackage("com.google.android.gm", objValue.toString());
        }
        if (preference == mEmail) {
        	//addPackage("com.google.android.apps.googlevoice", objValue.toString());
        }
        if (preference == mTwitter) {
        	addPackage("com.twitter.android", objValue.toString());
        }
        
        
        // always let the preference setting proceed.
        return true;
    }
    
}

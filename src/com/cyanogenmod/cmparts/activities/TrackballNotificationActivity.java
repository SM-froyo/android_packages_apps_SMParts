package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import java.io.Serializable;
import android.util.Log;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

public class TrackballNotificationActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	private static final int NOTIFICATION_ID = 400;
	
	/* Color */
	private static final String PREF_GOOGLE_VOICE_COLOR = "pref_google_voice_color";
	private static final String PREF_MMS_COLOR = "pref_mms_color";
	private static final String PREF_GMAIL_COLOR = "pref_gmail_color";
	private static final String PREF_EMAIL_COLOR = "pref_email_color";
	private static final String PREF_TWITTER_COLOR = "pref_twitter_color";
	private static final String PREF_TWICCA_COLOR = "pref_twicca_color";
	
	/* Blink */
	private static final String PREF_GOOGLE_VOICE_BLINK = "pref_google_voice_blink";
	private static final String PREF_MMS_BLINK = "pref_mms_blink";
	private static final String PREF_GMAIL_BLINK = "pref_gmail_blink";
	private static final String PREF_EMAIL_BLINK = "pref_email_blink";
	private static final String PREF_TWITTER_BLINK = "pref_twitter_blink";
	private static final String PREF_TWICCA_BLINK = "pref_twicca_blink";
	
	/* Test */
	private static final String PREF_GOOGLE_VOICE_TEST = "pref_google_voice_test";
	private static final String PREF_MMS_TEST = "pref_mms_test";
	private static final String PREF_GMAIL_TEST = "pref_gmail_test";
	private static final String PREF_EMAIL_TEST = "pref_email_test";
	private static final String PREF_TWITTER_TEST = "pref_twitter_test";
	private static final String PREF_TWICCA_TEST = "pref_twicca_test";
	
	private static final String TRACKBALL_SCREEN_PREF = "pref_trackball_screen";
	private static final String PREF_RESET = "pref_reset_notifcation";

	/* Color */
	private ListPreference mGoogleVoiceColor;
	private ListPreference mMmsColor;
	private ListPreference mGmailColor;
	private ListPreference mEmailColor;
	private ListPreference mTwitterColor;
	private ListPreference mTwiccaColor;
	
	/* Blink Rate */
	private ListPreference mGoogleVoiceBlink;
	private ListPreference mMmsBlink;
	private ListPreference mGmailBlink;
	private ListPreference mEmailBlink;
	private ListPreference mTwitterBlink;
	private ListPreference mTwiccaBlink;
	
	/* Test */
	private Preference mGoogleVoiceTest;
	private Preference mMmsTest;
	private Preference mGmailTest;
	private Preference mEmailTest;
	private Preference mTwitterTest;
	private Preference mTwiccaTest;
	
    private CheckBoxPreference mTrackballScreenPref;
    private Preference mReset;

	public static String[] mPackage;
	public String mPackageSource;
	
	

	public boolean isNull(String mString) {
		if(mString == null || mString.matches("null") 
		|| mString.length() == 0
		|| mString.matches("|")
		|| mString.matches("")) {
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
	//	for(int x = 0; x < temp.length; x++)
			//Log.i("GetArray", "X="+x+" temp="+temp[x]);
		return temp;
	}

	public String createString(String[] mArray) {
		int i;
		String temp = "";
		for(i = 0; i < mArray.length; i++) {
			if(mArray[i] == "" || mArray[i] == null || mArray[i].matches("null") || mArray[i].length() == 0)
				continue;

			temp = temp + "|" + mArray[i];
			//Log.i("createString", "String:" +temp+ " mArray: " +mArray[i]);
		}
		return temp;
	}

	public static String[] getPackageAndColorAndBlink(String mString) {
		if(mString == null || mString.matches("=") || mString.length() == 0 || mString.matches("null")) {
			return null;
		}
		String[] temp;
		temp = mString.split("=");
		//Log.i("getPackageAndColor", "String: "+mString);
		return temp;
	}
	
	public String[] findPackage(String pkg) {
		String mBaseString = Settings.System.getString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS);
		String[] mBaseArray = getArray(mBaseString);
		for(int i = 0; i < mBaseArray.length; i++) {
			if(isNull(mBaseArray[i])) {
				continue;
			}
			if(mBaseArray[i].contains(pkg)) {
				return getPackageAndColorAndBlink(mBaseArray[i]);
			}
		}
		return null;
	}

	public void updatePackage(String pkg, String color, String blink) {
		String stringtemp = Settings.System.getString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS);
		String[] temp = getArray(stringtemp);
		int i;
		String[] temp2;
		temp2 = new String[temp.length];
		boolean found = false;
		for(i = 0; i < temp.length; i++) {
			temp2 = getPackageAndColorAndBlink(temp[i]);
			if(temp2 == null) {
				continue;
			}
			//Log.i("addPackNew", "Temp2 Pkg="+pkg+": Package="+temp2[0]+" Color="+temp2[1]+" Blink="+temp2[2]);
			if(temp2[0].matches(pkg)) {
				if(blink.matches("0")) {
					temp2[1] = color;
				} else {
					temp2[2] = blink;
				}
				found = true;
				break;
			}
		}
		if(found) {
			String tempcolor = temp2[0] +"="+temp2[1]+"="+temp2[2];
			temp[i] = tempcolor;
		} else {
			int x = 0;
			//Get the last one
			for(x = 0; x < temp.length; x++) {
				//Log.i("addPackNew", "X="+x+" temp="+temp[x]);
				if(temp[x] == null || temp[x].length() == 0 || temp[x].matches("null"))
					break;
			}
			String tempcolor = pkg+"="+color+"=2";
			temp[x] = tempcolor;
		}
		Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS, createString(temp));
	}
	
	public void testPackage(String pkg) {
		String[] mTestPackage = findPackage(pkg);
		if(mTestPackage == null) {
			return;
		}
		
		final Notification notification = new Notification();
		
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		notification.ledARGB = Color.parseColor(mTestPackage[1]);
        notification.ledOnMS = 500;
        notification.ledOffMS = 2 * 1000;
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        nm.notify(NOTIFICATION_ID, notification);
        
        AlertDialog.Builder endFlash = new AlertDialog.Builder(this);
        endFlash.setMessage("Clear Flash")
        .setCancelable(false)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
        	  NotificationManager dialogNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        	  dialogNM.cancel(NOTIFICATION_ID);
        } });
        endFlash.show();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.trackball_notifications_title);
        addPreferencesFromResource(R.xml.trackball_notifications);

        PreferenceScreen prefSet = getPreferenceScreen();
        Boolean mCanEnableTrackball = (getResources().getBoolean(R.bool.has_trackball) == true);
        
        /* Colors */
        mGoogleVoiceColor = (ListPreference) prefSet.findPreference(PREF_GOOGLE_VOICE_COLOR);
        mGoogleVoiceColor.setOnPreferenceChangeListener(this);
        mMmsColor = (ListPreference) prefSet.findPreference(PREF_MMS_COLOR);
        mMmsColor.setOnPreferenceChangeListener(this);
        mGmailColor = (ListPreference) prefSet.findPreference(PREF_GMAIL_COLOR);
        mGmailColor.setOnPreferenceChangeListener(this);
        mEmailColor = (ListPreference) prefSet.findPreference(PREF_EMAIL_COLOR);
        mEmailColor.setOnPreferenceChangeListener(this);
        mTwitterColor = (ListPreference) prefSet.findPreference(PREF_TWITTER_COLOR);
        mTwitterColor.setOnPreferenceChangeListener(this);
        mTwiccaColor = (ListPreference) prefSet.findPreference(PREF_TWICCA_COLOR);
        mTwiccaColor.setOnPreferenceChangeListener(this);
        
        /* Blink */
        mGoogleVoiceBlink = (ListPreference) prefSet.findPreference(PREF_GOOGLE_VOICE_BLINK);
        mGoogleVoiceBlink.setOnPreferenceChangeListener(this);
        mMmsBlink = (ListPreference) prefSet.findPreference(PREF_MMS_BLINK);
        mMmsBlink.setOnPreferenceChangeListener(this);
        mGmailBlink = (ListPreference) prefSet.findPreference(PREF_GMAIL_BLINK);
        mGmailBlink.setOnPreferenceChangeListener(this);
        mEmailBlink = (ListPreference) prefSet.findPreference(PREF_EMAIL_BLINK);
        mEmailBlink.setOnPreferenceChangeListener(this);
        mTwitterBlink = (ListPreference) prefSet.findPreference(PREF_TWITTER_BLINK);
        mTwitterBlink.setOnPreferenceChangeListener(this);
        mTwiccaBlink = (ListPreference) prefSet.findPreference(PREF_TWICCA_BLINK);
        mTwiccaBlink.setOnPreferenceChangeListener(this);
        
        /* Test Color */
        mGoogleVoiceTest = (Preference) prefSet.findPreference(PREF_GOOGLE_VOICE_TEST);
        mMmsTest = (Preference) prefSet.findPreference(PREF_MMS_TEST);
        mGmailTest = (Preference) prefSet.findPreference(PREF_GMAIL_TEST);
        mEmailTest = (Preference) prefSet.findPreference(PREF_EMAIL_TEST);
        mTwitterTest = (Preference) prefSet.findPreference(PREF_TWITTER_TEST);
        mTwiccaTest = (Preference) prefSet.findPreference(PREF_TWICCA_TEST);
        
        mReset = (Preference) prefSet.findPreference(PREF_RESET);
        
        /* Trackball While Screen On */
        mTrackballScreenPref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_SCREEN_PREF);
        mTrackballScreenPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_SCREEN_ON, 0) == 1);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mGoogleVoiceColor) {
            updatePackage("com.google.android.apps.googlevoice", objValue.toString(), "0");
        }
        if (preference == mGoogleVoiceBlink) {
        	updatePackage("com.google.android.apps.googlevoice", "", objValue.toString());
        }
        
        if (preference == mMmsColor) {
        	updatePackage("com.android.mms", objValue.toString(), "0");
        }
        if (preference == mMmsBlink) {
        	updatePackage("com.android.mms", "", objValue.toString());
        }
        
        if (preference == mGmailColor) {
        	updatePackage("com.google.android.gm", objValue.toString(), "0");
        }
        if (preference == mGmailBlink) {
        	updatePackage("com.google.android.gm", "", objValue.toString());
        }
        
        if (preference == mEmailColor) {
        	updatePackage("com.android.email", objValue.toString(), "0");
        }
        if (preference == mEmailBlink) {
        	updatePackage("com.android.email", "", objValue.toString());
        }
        
        if (preference == mTwitterColor) {
        	updatePackage("com.twitter.android", objValue.toString(), "0");
        }
        if (preference == mTwitterBlink) {
        	updatePackage("com.twitter.android", "", objValue.toString());
        }
        
        if(preference == mTwiccaColor) {
        	updatePackage("jp.r246.twicca", objValue.toString(), "0");
        }
        if(preference == mTwiccaBlink) {
        	updatePackage("jp.r246.twicca", "", objValue.toString());
        }

        // always let the preference setting proceed.
        return true;
    }
    
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mTrackballScreenPref) {
            value = mTrackballScreenPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_SCREEN_ON, value ? 1 : 0);
        }
        else if (preference == mReset) {
        	Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS, "");
        	Toast.makeText(this, "Reset all colors", Toast.LENGTH_LONG);
        } 
        else if (preference == mGoogleVoiceTest) {
        	testPackage("com.google.android.apps.googlevoice");
        }
        else if (preference == mMmsTest) {
        	testPackage("com.android.mms");
        }
        else if (preference == mGmailTest) {
        	testPackage("com.google.android.gm");
        }
        else if (preference == mEmailTest) {
        	testPackage("com.android.email");
        }
        else if (preference == mTwitterTest) {
        	testPackage("com.twitter.android");
        }
        else if(preference == mTwiccaTest) {
        	testPackage("jp.r246.twicca");
        }
        return false;
    }
}

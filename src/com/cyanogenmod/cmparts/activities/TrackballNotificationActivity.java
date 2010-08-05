package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import java.io.File;
import android.util.Log;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import java.util.List;
import java.util.Random;

public class TrackballNotificationActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	private static final int NOTIFICATION_ID = 400;

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

	public String[] getArray(String mGetFrom) {
		if(isNull(mGetFrom)) {
			String[] tempfalse = new String[20];
			return tempfalse;
		}
		String[] temp;
		temp = mGetFrom.split("\\|");
		return temp;
	}

	public String createString(String[] mArray) {
		int i;
		String temp = "";
		for(i = 0; i < mArray.length; i++) {
			if(isNull(mArray[i]))
				continue;
			temp = temp + "|" + mArray[i];
		}
		return temp;
	}

	public String[] getPackageAndColorAndBlink(String mString) {
		if(isNull(mString)) {
			return null;
		}
		String[] temp;
		temp = mString.split("=");
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
				if(isNull(temp[x]))
					break;
			}
			String tempcolor;
			if(blink.matches("0")) {
				tempcolor = pkg+"="+color+"=2";
			} else {
				tempcolor = pkg+"=black="+blink;
			}
			temp[x] = tempcolor;
		}
		Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS, createString(temp));
	}

	private String[] colorList = {"green", "white", "red", "blue", "yellow", "cyan", "#800080", "#ffc0cb", "#ffa500", "#add8e6"};

	public void testPackage(String pkg) {
		final int mAlwaysPulse = Settings.System.getInt(getContentResolver(), Settings.System.TRACKBALL_SCREEN_ON, 0);
		String[] mTestPackage = findPackage(pkg);
		if(mTestPackage == null) {
			return;
		}
		final Notification notification = new Notification();
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		int mBlinkRate = Integer.parseInt(mTestPackage[2]);

		if(mTestPackage[1].equals("random")) {
			Random generator = new Random();
                        int x = generator.nextInt(colorList.length - 1);
                        notification.ledARGB = Color.parseColor(colorList[x]);
                } else {
			notification.ledARGB = Color.parseColor(mTestPackage[1]);
		}

	        notification.ledOnMS = 500;
		notification.ledOffMS = mBlinkRate * 1000;
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if(mAlwaysPulse != 1) {
		 	Settings.System.putInt(getContentResolver(), Settings.System.TRACKBALL_SCREEN_ON, 1);
		}
	        nm.notify(NOTIFICATION_ID, notification);

        	AlertDialog.Builder endFlash = new AlertDialog.Builder(this);
        	endFlash.setMessage(R.string.dialog_clear_flash)
        	.setCancelable(false)
        	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          	public void onClick(DialogInterface dialog, int which) {
        	  	NotificationManager dialogNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	        	  dialogNM.cancel(NOTIFICATION_ID);
			if(mAlwaysPulse != 1) {
                        	Settings.System.putInt(getContentResolver(), Settings.System.TRACKBALL_SCREEN_ON, 0);
			}
       		 } });
	        endFlash.show();
	}


        public String knownPackage(String pkg) {
                if(pkg.equals("com.android.email"))
                        return "Email";
                else if(pkg.equals("com.android.mms"))
                        return "Messaging";
                else if(pkg.equals("com.google.android.apps.googlevoice"))
                        return "Google Voice";
                else if(pkg.equals("com.google.android.gm"))
                        return "Gmail";
                else if(pkg.equals("com.google.android.gsf"))
                        return "GTalk";
                else if(pkg.equals("com.twitter.android"))
                        return "Twitter";
                else if(pkg.equals("jp.r246.twicca"))
                        return "Twicca";

                return null;
        }

	public String getPackageName(String pkg) {
	        PackageManager packageManager = getPackageManager(); 
		List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        	int size = packs.size();
        	for (int i = 0; i < size; i++) {
        		PackageInfo p = packs.get(i);
        		if(p.packageName.equals(pkg)) {
				if(knownPackage(pkg) != null) {
					return knownPackage(pkg);
				} else {
        				return p.applicationInfo.loadLabel(packageManager).toString();
				}
			}
        	}
        	return null;
	}

	public String[] getPackageList() {
		PackageManager packageManager = getPackageManager(); 
	        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        	int size = packs.size();
        	String[] list = new String[50];
        	int x = 0;
        	for (int i = 0; i < size; i++) {
        		PackageInfo p = packs.get(i);
        		try {
        			Context appContext = createPackageContext(p.packageName, 0);
        			boolean exists = (new File(appContext.getFilesDir(), "trackball_lights")).exists(); 
				if(exists || (knownPackage(p.packageName) != null)) {
        				list[x] = p.packageName;
        				x++;
        			}
        		} catch (Exception e) {
        			Log.d("GetPackageList", e.toString());
        		}
        	}
        	return list;
	}

	private PreferenceScreen createPreferenceScreen() {
		//The root of our system
		String[] packageList = getPackageList();
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

		for(int i = 0; i < packageList.length; i++) {
        		if(isNull(packageList[i]))
        			continue;

        	String packageName = getPackageName(packageList[i]);
        	PreferenceScreen appName = getPreferenceManager().createPreferenceScreen(this);
        	appName.setKey(packageList[i] + "_screen");
        	appName.setTitle(packageName);
        	root.addPreference(appName);

        	ListPreference colorList = new ListPreference(this);
                colorList.setKey(packageList[i]+"_color");
        	colorList.setTitle(R.string.color_trackball_flash_title);
        	colorList.setSummary(R.string.color_trackball_flash_summary);
        	colorList.setDialogTitle(R.string.dialog_color_trackball);
        	colorList.setEntries(R.array.entries_trackball_colors);
        	colorList.setEntryValues(R.array.pref_trackball_colors_values);
        	colorList.setOnPreferenceChangeListener(this);
        	appName.addPreference(colorList);

        	ListPreference blinkList = new ListPreference(this);
                blinkList.setKey(packageList[i]+"_blink");
        	blinkList.setTitle(R.string.color_trackball_blink_title);
        	blinkList.setSummary(R.string.color_trackball_blink_summary);
        	blinkList.setDialogTitle(R.string.dialog_blink_trackball);
        	blinkList.setEntries(R.array.pref_trackball_blink_rate_entries);
        	blinkList.setEntryValues(R.array.pref_trackball_blink_rate_values);
        	blinkList.setOnPreferenceChangeListener(this);
        	appName.addPreference(blinkList);

        	Preference testColor = new Preference(this);
                testColor.setKey(packageList[i]+"_test");
        	testColor.setSummary(R.string.color_trackball_test_summary);
        	testColor.setTitle(R.string.color_trackball_test_title);
        	appName.addPreference(testColor);
        }

        /*Advanced*/
        PreferenceScreen advancedScreen = getPreferenceManager().createPreferenceScreen(this);
        advancedScreen.setKey("advanced_screen");
        advancedScreen.setTitle(R.string.trackball_advanced_title);
    	root.addPreference(advancedScreen);

    	CheckBoxPreference alwaysPulse = new CheckBoxPreference(this);
    	alwaysPulse.setKey("always_pulse");
    	alwaysPulse.setSummary(R.string.pref_trackball_screen_summary);
    	alwaysPulse.setTitle(R.string.pref_trackball_screen_title);
    	advancedScreen.addPreference(alwaysPulse);

    	//TRACKBALL_NOTIFICATION_SUCCESSION
    	CheckBoxPreference successionPulse = new CheckBoxPreference(this);
    	successionPulse.setKey("pulse_succession");
    	successionPulse.setSummary(R.string.pref_trackball_sucess_summary);
    	successionPulse.setTitle(R.string.pref_trackball_sucess_title);
    	advancedScreen.addPreference(successionPulse);

        //TRACKBALL_NOTIFICATION_SUCCESSION
        CheckBoxPreference randomPulse = new CheckBoxPreference(this);
        randomPulse.setKey("pulse_random_colors");
        randomPulse.setSummary(R.string.pref_trackball_random_summary);
        randomPulse.setTitle(R.string.pref_trackball_random_title);
        advancedScreen.addPreference(randomPulse);

        //TRACKBALL_NOTIFICATION_SUCCESSION
        CheckBoxPreference orderPulse = new CheckBoxPreference(this);
        orderPulse.setKey("pulse_colors_in_order");
        orderPulse.setSummary(R.string.pref_trackball_order_summary);
        orderPulse.setTitle(R.string.pref_trackball_order_title);
        advancedScreen.addPreference(orderPulse);

    	Preference resetColors = new Preference(this);
    	resetColors.setKey("reset_notifications");
    	resetColors.setSummary(R.string.pref_trackball_reset_summary);
    	resetColors.setTitle(R.string.pref_trackball_reset_title);
    	advancedScreen.addPreference(resetColors);

        return root;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setTitle(R.string.trackball_notifications_title);
        setPreferenceScreen(createPreferenceScreen());
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        String value = objValue.toString();
        String key = preference.getKey().toString();
        String pkg = key.substring(0, key.lastIndexOf("_"));
        if(key.endsWith("_blink")) {
            updatePackage(pkg, "", value);
        } else {
            updatePackage(pkg, value, "0");
        }
        return true;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final boolean value;
	AlertDialog alertDialog;
        if (preference.getKey().toString().equals("reset_notifications")) {
        	Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_PACKAGE_COLORS, "");
        	Toast.makeText(this, "Reset all colors", Toast.LENGTH_LONG).show();
        } else if (preference.getKey().toString().equals("always_pulse")) {
        	CheckBoxPreference keyPref = (CheckBoxPreference) preference;
        	value = keyPref.isChecked();
                Settings.System.putInt(getContentResolver(),
                Settings.System.TRACKBALL_SCREEN_ON, value ? 1 : 0);
        } else if (preference.getKey().toString().equals("pulse_succession")) {
       		final CheckBoxPreference keyPref = (CheckBoxPreference) preference;
            	value = keyPref.isChecked();
                if(value == false) {
                        Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, 0);
                        return true;
                }

		alertDialog = new AlertDialog.Builder(this).create();
       		alertDialog.setTitle(R.string.notification_battery_warning_title);
       		alertDialog.setMessage(getResources().getString(R.string.notification_battery_warning));
       		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
     		public void onClick(DialogInterface dialog, int which) {
               		Settings.System.putInt(getContentResolver(),
                       		Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, value ? 1 : 0);
			return;
      		} });
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                        Settings.System.putInt(getContentResolver(),
                           Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, value ? 1 : 0);
               		keyPref.setChecked(false);
		         return;
                }});
	        alertDialog.show();
	} else if (preference.getKey().toString().equals("pulse_random_colors")) {
                final CheckBoxPreference keyPref = (CheckBoxPreference) preference;
                value = keyPref.isChecked();
                if(value == false) {
                        Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_RANDOM, 0);
                        return true;
                }

                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(R.string.notification_battery_warning_title);
                alertDialog.setMessage(getResources().getString(R.string.notification_battery_warning));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
	                Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_RANDOM, value ? 1 : 0);
                        return;

                } });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
        	        Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_RANDOM, 0);
			keyPref.setChecked(false);
                        return;
                }});
                alertDialog.show();
	} else if (preference.getKey().toString().equals("pulse_colors_in_order")) {
                final CheckBoxPreference keyPref = (CheckBoxPreference) preference;
                value = keyPref.isChecked();
		if(value == false) {
			Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_PULSE_ORDER, 0);
			return true;
		}

		alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(R.string.notification_battery_warning_title);
                alertDialog.setMessage(getResources().getString(R.string.notification_battery_warning));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
        	        Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_PULSE_ORDER, value ? 1 : 0);
                        return;
                } });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
	                Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_PULSE_ORDER, 0);
			keyPref.setChecked(false);
                        return;
                }});
                alertDialog.show();


        } else if(preference.getKey().toString().endsWith("_test")) {
            String pkg = preference.getKey().toString().substring(0, preference.getKey().toString().lastIndexOf("_"));
            testPackage(pkg);
        }
        return false;
    }
}


package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class TrackballNotificationActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final int NOTIFICATION_ID = 400;

    public static String[] mPackage;

    public String mPackageSource;

    public Handler mHandler = new Handler();

    public ProgressDialog pbarDialog;

    public String mGlobalPackage;

    public int mGlobalPulse = 0;

    public int mGlobalSuccession = 0;

    public int mGlobalBlend = 0;

    public CheckBoxPreference globalSuccession;

    public CheckBoxPreference globalRandom;

    public CheckBoxPreference globalOrder;

    public CheckBoxPreference globalBlend;

    public Preference globalCustom;

    public Preference globalTest;

    public String[] uniqueArray(String[] array) {
        Set<String> set = new HashSet<String>(Arrays.asList(array));
        String[] array2 = set.toArray(new String[set.size()]);
        return array2;
    }

    public boolean isNull(String mString) {
        if (mString == null || mString.matches("null") || mString.length() == 0
                || mString.matches("|") || mString.matches("")) {
            return true;
        } else {
            return false;
        }
    }

    public String[] getArray(String mGetFrom) {
        if (isNull(mGetFrom)) {
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
        for (i = 0; i < mArray.length; i++) {
            if (isNull(mArray[i]))
                continue;
            temp = temp + "|" + mArray[i];
        }
        return temp;
    }

    public String[] getPackageAndColorAndBlink(String mString) {
        if (isNull(mString)) {
            return null;
        }
        String[] temp;
        temp = mString.split("=");
        return temp;
    }

    public String[] findPackage(String pkg) {
        String mBaseString = Settings.System.getString(getContentResolver(),
                Settings.System.NOTIFICATION_PACKAGE_COLORS);
        String[] mBaseArray = getArray(mBaseString);
        if (mBaseArray == null)
            return null;
        for (int i = 0; i < mBaseArray.length; i++) {
            if (isNull(mBaseArray[i])) {
                continue;
            }
            if (mBaseArray[i].contains(pkg)) {
                return getPackageAndColorAndBlink(mBaseArray[i]);
            }
        }
        return null;
    }

    public void updatePackage(String pkg, String color, String blink, String cat) {
        String stringtemp = Settings.System.getString(getContentResolver(),
                Settings.System.NOTIFICATION_PACKAGE_COLORS);
        String[] temp = getArray(stringtemp);
        int i;
        String[] temp2;
        temp2 = new String[temp.length];
        boolean found = false;
        for (i = 0; i < temp.length; i++) {
            temp2 = getPackageAndColorAndBlink(temp[i]);
            if (temp2 == null) {
                continue;
            }
            if (temp2[0].matches(pkg)) {
                if (!cat.matches("0")) {
                    temp2[3] = cat;
                } else if (!blink.matches("0")) {
                    temp2[2] = blink;
                } else {
                    temp2[1] = color;
                }
                found = true;
                break;
            }
        }
        if (found) {
            try {
                String tempcolor = temp2[0] + "=" + temp2[1] + "=" + temp2[2] + "=" + temp2[3];
                temp[i] = tempcolor;
            } catch (ArrayIndexOutOfBoundsException e) {
                // Making array changes, if they aren't new, they will error.
                // Have to force them to be reset unfortunately.
                Settings.System.putString(getContentResolver(),
                        Settings.System.NOTIFICATION_PACKAGE_COLORS, "");
                Toast.makeText(
                        this,
                        "Unfortunately there was an array error. Your colors have been reset, we apologize for any inconveience.",
                        Toast.LENGTH_LONG).show();
                return; // we want to end it here, no need to continue
            }
        } else {
            int x = 0;
            // Get the last one
            for (x = 0; x < temp.length; x++) {
                if (isNull(temp[x]))
                    break;
            }
            String tempcolor;
            if (!cat.matches("0")) {
                tempcolor = pkg + "=color=2=" + cat;
            } else if (!blink.matches("0")) {
                tempcolor = pkg + "=black=" + blink + "=New";
            } else {
                tempcolor = pkg + "=" + color + "=2=New";
            }
            temp[x] = tempcolor;
        }
        Settings.System.putString(getContentResolver(),
                Settings.System.NOTIFICATION_PACKAGE_COLORS, createString(temp));
    }

    private String[] colorList = {
            "green", "white", "red", "blue", "yellow", "cyan", "#800080", "#ffc0cb", "#ffa500",
            "#add8e6"
    };

    public void testPackage(String pkg) {
        final int mAlwaysPulse = Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_SCREEN_ON, 0);
        String[] mTestPackage = findPackage(pkg);
        if (mTestPackage == null) {
            return;
        }
        final Notification notification = new Notification();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        int mBlinkRate = Integer.parseInt(mTestPackage[2]);

        if (mTestPackage[1].equals("random")) {
            Random generator = new Random();
            int x = generator.nextInt(colorList.length - 1);
            notification.ledARGB = Color.parseColor(colorList[x]);
        } else {
            notification.ledARGB = Color.parseColor(mTestPackage[1]);
        }

        notification.ledOnMS = 500;
        notification.ledOffMS = mBlinkRate * 1000;
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mAlwaysPulse != 1) {
            Settings.System.putInt(getContentResolver(), Settings.System.TRACKBALL_SCREEN_ON, 1);
        }
        nm.notify(NOTIFICATION_ID, notification);

        AlertDialog.Builder endFlash = new AlertDialog.Builder(this);
        endFlash.setMessage(R.string.dialog_clear_flash).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationManager dialogNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        dialogNM.cancel(NOTIFICATION_ID);
                        if (mAlwaysPulse != 1) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.TRACKBALL_SCREEN_ON, 0);
                        }
                    }
                });
        endFlash.show();
    }

    public String knownPackage(String pkg) {
        if (pkg.equals("com.android.email"))
            return "Email";
        else if (pkg.equals("com.android.mms"))
            return "Messaging";
        else if (pkg.equals("com.google.android.apps.googlevoice"))
            return "Google Voice";
        else if (pkg.equals("com.google.android.gm"))
            return "Gmail";
        else if (pkg.equals("com.google.android.gsf"))
            return "GTalk";
        else if (pkg.equals("com.twitter.android"))
            return "Twitter";
        else if (pkg.equals("jp.r246.twicca"))
            return "Twicca";
        else if (pkg.equals("com.android.phone"))
            return "Missed Call"; // Say Missed Call instead of "Dialer" as
                                  // people think its missing.

        return null;
    }

    public String getPackageName(PackageInfo p) {
        String knownPackage = knownPackage(p.packageName);
        return knownPackage == null ?
                p.applicationInfo.loadLabel(getPackageManager()).toString() : knownPackage;
    }

    public List<PackageInfo> getPackageList() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        List<PackageInfo> list = new ArrayList<PackageInfo>();
        for (PackageInfo p : packs) {
            try {
                Context appContext = createPackageContext(p.packageName, 0);
                boolean exists = (new File(appContext.getFilesDir(), "trackball_lights")).exists();
                if (exists || (knownPackage(p.packageName) != null)) {
                    list.add(p);
                }
            } catch (Exception e) {
                Log.d("GetPackageList", e.toString());
            }
        }
        return list;
    }

    private String[] getCategoryList() {
        String mBaseString = Settings.System.getString(getContentResolver(),
                Settings.System.NOTIFICATION_PACKAGE_COLORS);
        String[] mBaseArray = getArray(mBaseString);
        String[] catList = new String[30];
        boolean found = false;
        for (int i = 0; i < mBaseArray.length; i++) {
            String[] temp = getPackageAndColorAndBlink(mBaseArray[i]);
            if (isNull(temp[3])) {
                continue;
            }
            catList[i] = temp[3];
            found = true;
        }
        return (found ? uniqueArray(catList) : null);
    }

    private PreferenceScreen createPreferenceScreen() {

        // The root of our system
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        /* Advanced */
        PreferenceScreen advancedScreen = getPreferenceManager().createPreferenceScreen(this);
        advancedScreen.setKey("advanced_screen");
        advancedScreen.setTitle(R.string.trackball_advanced_title);
        root.addPreference(advancedScreen);

        CheckBoxPreference alwaysPulse = new CheckBoxPreference(this);
        alwaysPulse.setKey("always_pulse");
        alwaysPulse.setSummary(R.string.pref_trackball_screen_summary);
        alwaysPulse.setTitle(R.string.pref_trackball_screen_title);
        advancedScreen.addPreference(alwaysPulse);

        CheckBoxPreference blendPulse = new CheckBoxPreference(this);
        blendPulse.setKey("blend_colors");
        blendPulse.setSummary(R.string.pref_trackball_blend_summary);
        blendPulse.setTitle(R.string.pref_trackball_blend_title);
        advancedScreen.addPreference(blendPulse);

        CheckBoxPreference successionPulse = new CheckBoxPreference(this);
        successionPulse.setKey("pulse_succession");
        successionPulse.setSummary(R.string.pref_trackball_sucess_summary);
        successionPulse.setTitle(R.string.pref_trackball_sucess_title);
        advancedScreen.addPreference(successionPulse);

        CheckBoxPreference randomPulse = new CheckBoxPreference(this);
        randomPulse.setKey("pulse_random_colors");
        randomPulse.setSummary(R.string.pref_trackball_random_summary);
        randomPulse.setTitle(R.string.pref_trackball_random_title);
        advancedScreen.addPreference(randomPulse);

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

        // Add each application
        PreferenceCategory cat = new PreferenceCategory(this);
        cat.setTitle(R.string.group_applications);
        root.addPreference(cat);

        Map<String, PackageInfo> sortedPackages = new TreeMap<String, PackageInfo>();
        for (PackageInfo pkgInfo : getPackageList()) {
            sortedPackages.put(getPackageName(pkgInfo), pkgInfo);
        }

        for (Map.Entry<String, PackageInfo> pkgEntry : sortedPackages.entrySet()) {
            String pkg = pkgEntry.getValue().packageName;
            if (isNull(pkg))
                continue;

            String shortPackageName = pkgEntry.getKey();
            PreferenceScreen appName = getPreferenceManager().createPreferenceScreen(this);
            appName.setKey(pkg + "_screen");
            appName.setTitle(shortPackageName);
            cat.addPreference(appName);

            ListPreference colorList = new ListPreference(this);
            colorList.setKey(pkg + "_color");
            colorList.setTitle(R.string.color_trackball_flash_title);
            colorList.setSummary(R.string.color_trackball_flash_summary);
            colorList.setDialogTitle(R.string.dialog_color_trackball);
            colorList.setEntries(R.array.entries_trackball_colors);
            /*
             * if(packageValues != null) { colorList.setValue(packageValues[1]);
             * }
             */
            colorList.setEntryValues(R.array.pref_trackball_colors_values);
            colorList.setOnPreferenceChangeListener(this);
            appName.addPreference(colorList);

            ListPreference blinkList = new ListPreference(this);
            blinkList.setKey(pkg + "_blink");
            blinkList.setTitle(R.string.color_trackball_blink_title);
            blinkList.setSummary(R.string.color_trackball_blink_summary);
            blinkList.setDialogTitle(R.string.dialog_blink_trackball);
            blinkList.setEntries(R.array.pref_trackball_blink_rate_entries);
            blinkList.setEntryValues(R.array.pref_trackball_blink_rate_values);
            /*
             * if(packageValues != null) { blinkList.setValue(packageValues[2]);
             * }
             */
            blinkList.setOnPreferenceChangeListener(this);
            appName.addPreference(blinkList);

            Preference customColor = new Preference(this);
            customColor.setKey(pkg + "_custom");
            customColor.setSummary(R.string.color_trackball_custom_summary);
            customColor.setTitle(R.string.color_trackball_custom_title);

            String[] packageValues = findPackage(pkg);
            if (packageValues != null) {
                // Check if the color is none, if it is disable custom.
                customColor.setEnabled(!packageValues[1].equals("none"));
            }
            appName.addPreference(customColor);

            Preference testColor = new Preference(this);
            testColor.setKey(pkg + "_test");
            testColor.setSummary(R.string.color_trackball_test_summary);
            testColor.setTitle(R.string.color_trackball_test_title);

            if (packageValues != null) {
                // Check if the color is none, if it isdisable Test.
                testColor.setEnabled(!packageValues[1].equals("none"));
            }
            appName.addPreference(testColor);
        }

        return root;
    }

    final Runnable mFinishLoading = new Runnable() {
        public void run() {
            pbarDialog.dismiss();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.trackball_notifications_title);
        pbarDialog = ProgressDialog.show(this, getString(R.string.dialog_trackball_loading),
                getString(R.string.dialog_trackball_packagelist), true, false);
        Thread t = new Thread() {
            public void run() {
                setPreferenceScreen(createPreferenceScreen());
                mHandler.post(mFinishLoading);
            }
        };
        t.start();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        String value = objValue.toString();
        String key = preference.getKey().toString();
        String pkg = key.substring(0, key.lastIndexOf("_"));
        if (key.endsWith("_blink")) {
            updatePackage(pkg, "", value, "0");
        } else if (key.endsWith("_color")) {
            updatePackage(pkg, value, "0", "0");

            PreferenceScreen prefSet = getPreferenceScreen();

            globalCustom = prefSet.findPreference(pkg + "_custom");
            globalCustom.setEnabled(!value.matches("none"));

            globalTest = prefSet.findPreference(pkg + "_test");
            globalTest.setEnabled(!value.matches("none"));
        }

        return true;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final boolean value;
        AlertDialog alertDialog;
        if (preference.getKey().toString().equals("reset_notifications")) {
            Settings.System.putString(getContentResolver(),
                    Settings.System.NOTIFICATION_PACKAGE_COLORS, "");
            Toast.makeText(this, "Reset all colors", Toast.LENGTH_LONG).show();
        } else if (preference.getKey().toString().equals("always_pulse")) {
            CheckBoxPreference keyPref = (CheckBoxPreference) preference;
            value = keyPref.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.TRACKBALL_SCREEN_ON,
                    value ? 1 : 0);
        } else if (preference.getKey().toString().equals("pulse_succession")) {
            final CheckBoxPreference keyPref = (CheckBoxPreference) preference;
            value = keyPref.isChecked();
            if (value == false) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, 0);
                return true;
            }

            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(R.string.notification_battery_warning_title);
            alertDialog.setMessage(getResources().getString(R.string.notification_battery_warning));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, value ? 1
                                            : 0);
                            return;
                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, value ? 1
                                            : 0);
                            keyPref.setChecked(false);
                            return;
                        }
                    });
            alertDialog.show();
        } else if (preference.getKey().toString().equals("pulse_random_colors")) {
            final CheckBoxPreference keyPref = (CheckBoxPreference) preference;
            value = keyPref.isChecked();
            if (value == false) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_RANDOM, 0);
                return true;
            }

            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(R.string.notification_battery_warning_title);
            alertDialog.setMessage(getResources().getString(R.string.notification_battery_warning));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.TRACKBALL_NOTIFICATION_RANDOM, value ? 1 : 0);
                            return;

                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.TRACKBALL_NOTIFICATION_RANDOM, 0);
                            keyPref.setChecked(false);
                            return;
                        }
                    });
            alertDialog.show();
        } else if (preference.getKey().toString().equals("pulse_colors_in_order")) {
            final CheckBoxPreference keyPref = (CheckBoxPreference) preference;
            value = keyPref.isChecked();
            if (value == false) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.TRACKBALL_NOTIFICATION_PULSE_ORDER, 0);
                return true;
            }

            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(R.string.notification_battery_warning_title);
            alertDialog.setMessage(getResources().getString(R.string.notification_battery_warning));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.TRACKBALL_NOTIFICATION_PULSE_ORDER, value ? 1
                                            : 0);
                            return;
                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.TRACKBALL_NOTIFICATION_PULSE_ORDER, 0);
                            keyPref.setChecked(false);
                            return;
                        }
                    });
            alertDialog.show();
        } else if (preference.getKey().toString().equals("blend_colors")) {
            final CheckBoxPreference keyPref = (CheckBoxPreference) preference;
            value = keyPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_NOTIFICATION_BLEND_COLOR, value ? 1 : 0);
        } else if (preference.getKey().toString().endsWith("_custom")) {
            String pkg = preference.getKey().toString()
                    .substring(0, preference.getKey().toString().lastIndexOf("_"));
            mGlobalPackage = pkg;
            ColorPickerDialog cp = new ColorPickerDialog(this, mPackageColorListener,
                    readPackageColor());
            cp.show();
        } else if (preference.getKey().toString().endsWith("_test")) {
            String pkg = preference.getKey().toString()
                    .substring(0, preference.getKey().toString().lastIndexOf("_"));
            testPackage(pkg);
        }
        return false;
    }

    private int readPackageColor() {
        String[] mPackage = findPackage(mGlobalPackage);
        if (mPackage == null) {
            return -16777216;
        }
        if (mPackage[1].equals("random")) {
            return -16777216;
        } else {
            return Color.parseColor(mPackage[1]);
        }
    }

    public void pulseLight(int color) {
        mGlobalPulse = Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_SCREEN_ON, 0);
        mGlobalSuccession = Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, 0);
        mGlobalBlend = Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_NOTIFICATION_BLEND_COLOR, 0);
        Notification notification = new Notification();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = color;
        notification.ledOnMS = 500;
        notification.ledOffMS = 0;
        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mGlobalPulse != 1) {
            Settings.System.putInt(getContentResolver(), Settings.System.TRACKBALL_SCREEN_ON, 1);
        }
        if (mGlobalSuccession != 1) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, 1);
        }
        if (mGlobalBlend == 1) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_NOTIFICATION_BLEND_COLOR, 0);
        }
        nm.notify(NOTIFICATION_ID, notification);
        Thread t = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // shouldn't happen
                }

                nm.cancel(NOTIFICATION_ID);
                if (mGlobalPulse != 1) {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.TRACKBALL_SCREEN_ON, 0);
                }
                if (mGlobalSuccession != 1) {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.TRACKBALL_NOTIFICATION_SUCCESSION, 0);
                }
                if (mGlobalBlend == 1) {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.TRACKBALL_NOTIFICATION_BLEND_COLOR, 1);
                }
            }
        };
        t.start();
    }

    ColorPickerDialog.OnColorChangedListener mPackageColorListener = new ColorPickerDialog.OnColorChangedListener() {
        public void colorUpdate(int color) {
            pulseLight(color);
        }

        public void colorChanged(int color) {
            updatePackage(mGlobalPackage, convertToARGB(color), "0", "0");
        }
    };

    private String convertToARGB(int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + alpha + red + green + blue;
    }

}

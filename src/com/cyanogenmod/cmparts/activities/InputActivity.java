package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import java.io.File;
import java.util.ArrayList;

public class InputActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TRACKBALL_WAKE_PREF = "pref_trackball_wake";
    private static final String BUTTON_CATEGORY = "pref_category_button_settings";
    private static final String USER_DEFINED_KEY1 = "pref_user_defined_key1";
    private static final String USER_DEFINED_KEY2 = "pref_user_defined_key2";
    private static final String USER_DEFINED_KEY3 = "pref_user_defined_key3";

    private CheckBoxPreference mTrackballWakePref;

    private Preference mUserDefinedKey1Pref;
    private Preference mUserDefinedKey2Pref;
    private Preference mUserDefinedKey3Pref;
    private int mKeyNumber = 1;

    private static final int REQUEST_PICK_SHORTCUT = 1;
    private static final int REQUEST_PICK_APPLICATION = 2;
    private static final int REQUEST_CREATE_SHORTCUT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.input_settings_title_subhead);
        addPreferencesFromResource(R.xml.input_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Trackball Wake */
        mTrackballWakePref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_WAKE_PREF);
        mTrackballWakePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_WAKE_SCREEN, 0) == 1);

        PreferenceCategory buttonCategory = (PreferenceCategory)prefSet.findPreference(BUTTON_CATEGORY);

        mUserDefinedKey1Pref = (Preference) prefSet.findPreference(USER_DEFINED_KEY1);
        mUserDefinedKey2Pref = (Preference) prefSet.findPreference(USER_DEFINED_KEY2);
        mUserDefinedKey3Pref = (Preference) prefSet.findPreference(USER_DEFINED_KEY3);

        if (!"vision".equals(Build.DEVICE) && !getResources().getBoolean(R.bool.has_trackball)) {
            prefSet.removePreference(buttonCategory);
        } else {
            if (!getResources().getBoolean(R.bool.has_trackball)) {
                buttonCategory.removePreference(mTrackballWakePref);
            }
            if (!"vision".equals(Build.DEVICE)) {
                buttonCategory.removePreference(mUserDefinedKey1Pref);
                buttonCategory.removePreference(mUserDefinedKey2Pref);
                buttonCategory.removePreference(mUserDefinedKey3Pref);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserDefinedKey1Pref.setSummary(Settings.System.getString(getContentResolver(),
                Settings.System.USER_DEFINED_KEY1_APP));
        mUserDefinedKey2Pref.setSummary(Settings.System.getString(getContentResolver(),
                Settings.System.USER_DEFINED_KEY2_APP));
        mUserDefinedKey3Pref.setSummary(Settings.System.getString(getContentResolver(),
                Settings.System.USER_DEFINED_KEY3_APP));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mTrackballWakePref) {
            value = mTrackballWakePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_WAKE_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mUserDefinedKey1Pref) {
            pickShortcut(1);
            return true;
        } else if (preference == mUserDefinedKey2Pref) {
            pickShortcut(2);
            return true;
        } else if (preference == mUserDefinedKey3Pref) {
            pickShortcut(3);
            return true;
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    private void pickShortcut(int keyNumber) {
        mKeyNumber = keyNumber;
        Bundle bundle = new Bundle();
        ArrayList<String> shortcutNames = new ArrayList<String>();
        shortcutNames.add(getString(R.string.group_applications));
        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
        shortcutIcons.add(ShortcutIconResource.fromContext(this, R.drawable.ic_launcher_application));
        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);
        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.select_custom_app_title));
        pickIntent.putExtras(bundle);
        startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_APPLICATION:
                    completeSetCustomApp(data);
                    break;
                case REQUEST_CREATE_SHORTCUT:
                    completeSetCustomShortcut(data);
                    break;
                case REQUEST_PICK_SHORTCUT:
                    processShortcut(data, REQUEST_PICK_APPLICATION, REQUEST_CREATE_SHORTCUT);
                    break;
            }
        }
    }

    void processShortcut(Intent intent, int requestCodeApplication, int requestCodeShortcut) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            startActivityForResult(pickIntent, requestCodeApplication);
        } else {
            startActivityForResult(intent, requestCodeShortcut);
        }
    }

    void completeSetCustomShortcut(Intent data) {
        Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        int keyNumber = mKeyNumber;
        if (keyNumber == 1){
            if (Settings.System.putString(getContentResolver(), Settings.System.USER_DEFINED_KEY1_APP, intent.toUri(0))) {
                mUserDefinedKey1Pref.setSummary(intent.toUri(0));
            }
        } else if (keyNumber == 2){
            if (Settings.System.putString(getContentResolver(), Settings.System.USER_DEFINED_KEY2_APP, intent.toUri(0))) {
                mUserDefinedKey2Pref.setSummary(intent.toUri(0));
            }
        } else if (keyNumber == 3){
            if (Settings.System.putString(getContentResolver(), Settings.System.USER_DEFINED_KEY3_APP, intent.toUri(0))) {
                mUserDefinedKey3Pref.setSummary(intent.toUri(0));
            }
        }
    }

    void completeSetCustomApp(Intent data) {
        int keyNumber = mKeyNumber;
        if (keyNumber == 1){
            if (Settings.System.putString(getContentResolver(), Settings.System.USER_DEFINED_KEY1_APP, data.toUri(0))) {
                mUserDefinedKey1Pref.setSummary(data.toUri(0));
            }
        } else if (keyNumber == 2){
            if (Settings.System.putString(getContentResolver(), Settings.System.USER_DEFINED_KEY2_APP, data.toUri(0))) {
                mUserDefinedKey2Pref.setSummary(data.toUri(0));
            }
        } else if (keyNumber == 3){
            if (Settings.System.putString(getContentResolver(), Settings.System.USER_DEFINED_KEY3_APP, data.toUri(0))) {
                mUserDefinedKey3Pref.setSummary(data.toUri(0));
            }
        }
    }
}

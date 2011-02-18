package com.cyanogenmod.cmparts.activities;

import java.io.File;
import java.util.ArrayList;

import com.cyanogenmod.cmparts.R;

import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

public class ExtendedLockscreenActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "ExtendedLockscreen";

    private static final String BACKGROUND_TYPE = "ext_ls_background";
    private static final String COUNTER_BAR_VISIBILITY = "ext_ls_counterbar_visibility";
    private static final String CONNECTIVITY_BAR_VISIBILITY = "ext_ls_connectivitybar_visibility";
    private static final String GENIE_VISIBILITY = "ext_ls_genie_visibility";
    private static final String NOW_PLAYING_VISIBILITY = "ext_ls_nowplaying_visibility";
    private static final String SMS_MMS_NOTIFICATION = "ext_ls_smsmms_notification";
    private static final String CALL_NOTIFICATION = "ext_ls_call_notification";
    private static final String GMAIL_NOTIFICATION = "ext_ls_gmail_notification";
    private static final String ALARM_NOTIFICATION = "ext_ls_alarm_notification";
    private static final String STATE_NOTIFICATION = "ext_ls_state_notification";

    private CheckBoxPreference mBackgroundType;
    private CheckBoxPreference mVisibleCounterBar;
    private CheckBoxPreference mVisibleConnectivityBar;
    private CheckBoxPreference mVisibleGenieWidget;
    private CheckBoxPreference mVisibleNowPlaying;
    private CheckBoxPreference mNotificationSMSMMS;
    private CheckBoxPreference mNotificationCall;
    private CheckBoxPreference mNotificationGmail;
    private CheckBoxPreference mNotificationAlarm;
    private CheckBoxPreference mNotificationState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.ext_ls_title);
        addPreferencesFromResource(R.xml.ext_ls_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* wallpaper style */
        mBackgroundType = (CheckBoxPreference) prefSet.findPreference(BACKGROUND_TYPE);
        mBackgroundType.setChecked(Settings.System.getInt(getContentResolver(), 
                BACKGROUND_TYPE, 1) == 1);

        /* visibilities */
        mVisibleCounterBar = (CheckBoxPreference) prefSet.findPreference(COUNTER_BAR_VISIBILITY);
        mVisibleCounterBar.setChecked(Settings.System.getInt(getContentResolver(), 
                COUNTER_BAR_VISIBILITY, 1) == 1);

        mVisibleConnectivityBar = (CheckBoxPreference) prefSet.findPreference(CONNECTIVITY_BAR_VISIBILITY);
        mVisibleConnectivityBar.setChecked(Settings.System.getInt(getContentResolver(), 
                CONNECTIVITY_BAR_VISIBILITY, 1) == 1);

        mVisibleGenieWidget = (CheckBoxPreference) prefSet.findPreference(GENIE_VISIBILITY);
        mVisibleGenieWidget.setChecked(Settings.System.getInt(getContentResolver(), 
                GENIE_VISIBILITY, 1) == 1);

        mVisibleNowPlaying = (CheckBoxPreference) prefSet.findPreference(NOW_PLAYING_VISIBILITY);
        mVisibleNowPlaying.setChecked(Settings.System.getInt(getContentResolver(), 
                NOW_PLAYING_VISIBILITY, 1) == 1);

        /* notifications */
        mNotificationSMSMMS = (CheckBoxPreference) prefSet.findPreference(SMS_MMS_NOTIFICATION);
        mNotificationSMSMMS.setChecked(Settings.System.getInt(getContentResolver(), 
                SMS_MMS_NOTIFICATION, 1) == 1);

        mNotificationCall = (CheckBoxPreference) prefSet.findPreference(CALL_NOTIFICATION);
        mNotificationCall.setChecked(Settings.System.getInt(getContentResolver(), 
                CALL_NOTIFICATION, 1) == 1);

        mNotificationGmail = (CheckBoxPreference) prefSet.findPreference(GMAIL_NOTIFICATION);
        mNotificationGmail.setChecked(Settings.System.getInt(getContentResolver(), 
                GMAIL_NOTIFICATION, 1) == 1);

        mNotificationAlarm = (CheckBoxPreference) prefSet.findPreference(ALARM_NOTIFICATION);
        mNotificationAlarm.setChecked(Settings.System.getInt(getContentResolver(), 
                ALARM_NOTIFICATION, 1) == 1);

        mNotificationState = (CheckBoxPreference) prefSet.findPreference(STATE_NOTIFICATION);
        mNotificationState.setChecked(Settings.System.getInt(getContentResolver(), 
                STATE_NOTIFICATION, 1) == 1);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mBackgroundType) {
            value = mBackgroundType.isChecked();
            Settings.System.putInt(getContentResolver(),
                    BACKGROUND_TYPE, value ? 1 : 0);
            return true;
        } else if (preference == mVisibleCounterBar) {
            value = mVisibleCounterBar.isChecked();
            Settings.System.putInt(getContentResolver(),
                    COUNTER_BAR_VISIBILITY, value ? 1 : 0);
            return true;
        } else if (preference == mVisibleConnectivityBar) {
            value = mVisibleConnectivityBar.isChecked();
            Settings.System.putInt(getContentResolver(),
                    CONNECTIVITY_BAR_VISIBILITY, value ? 1 : 0);
            return true;
        } else if (preference == mVisibleGenieWidget) {
            value = mVisibleGenieWidget.isChecked();
            Settings.System.putInt(getContentResolver(),
                    GENIE_VISIBILITY, value ? 1 : 0);
            return true;
        } else if (preference == mVisibleNowPlaying) {
            value = mVisibleNowPlaying.isChecked();
            Settings.System.putInt(getContentResolver(),
                    NOW_PLAYING_VISIBILITY, value ? 1 : 0);
            return true;
        } else if (preference == mNotificationSMSMMS) {
            value = mNotificationSMSMMS.isChecked();
            Settings.System.putInt(getContentResolver(),
                    SMS_MMS_NOTIFICATION, value ? 1 : 0);
            return true;
        } else if (preference == mNotificationCall) {
            value = mNotificationCall.isChecked();
            Settings.System.putInt(getContentResolver(),
                    CALL_NOTIFICATION, value ? 1 : 0);
            return true;
        } else if (preference == mNotificationGmail) {
            value = mNotificationGmail.isChecked();
            Settings.System.putInt(getContentResolver(),
                    GMAIL_NOTIFICATION, value ? 1 : 0);
            return true;
        } else if (preference == mNotificationAlarm) {
            value = mNotificationAlarm.isChecked();
            Settings.System.putInt(getContentResolver(),
                    ALARM_NOTIFICATION, value ? 1 : 0);
            return true;
        } else if (preference == mNotificationState) {
            value = mNotificationState.isChecked();
            Settings.System.putInt(getContentResolver(),
                    STATE_NOTIFICATION, value ? 1 : 0);
            return true;
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // empty
        return false;
    }

}

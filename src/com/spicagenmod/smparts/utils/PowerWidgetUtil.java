package com.spicagenmod.smparts.utils;

import com.spicagenmod.smparts.R;

import android.content.Context;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class PowerWidgetUtil {
    public static final String BUTTON_WIFI = "toggleWifi";
    public static final String BUTTON_GPS = "toggleGPS";
    public static final String BUTTON_BLUETOOTH = "toggleBluetooth";
    public static final String BUTTON_BRIGHTNESS = "toggleBrightness";
    public static final String BUTTON_SOUND = "toggleSound";
    public static final String BUTTON_SYNC = "toggleSync";
    public static final String BUTTON_WIFIAP = "toggleWifiAp";
    public static final String BUTTON_SCREENTIMEOUT = "toggleScreenTimeout";
    public static final String BUTTON_MOBILEDATA = "toggleMobileData";
    public static final String BUTTON_LOCKSCREEN = "toggleLockScreen";
    public static final String BUTTON_NETWORKMODE = "toggleNetworkMode";
    public static final String BUTTON_AUTOROTATE = "toggleAutoRotate";
    public static final String BUTTON_AIRPLANE = "toggleAirplane";
    public static final String BUTTON_SLEEP = "toggleSleepMode";
    public static final HashMap<String, ButtonInfo> BUTTONS = new HashMap<String, ButtonInfo>();
    static {
        BUTTONS.put(BUTTON_AIRPLANE, new PowerWidgetUtil.ButtonInfo(
                BUTTON_AIRPLANE, R.string.title_toggle_airplane, com.android.internal.R.drawable.stat_airplane_on));
        BUTTONS.put(BUTTON_AUTOROTATE, new PowerWidgetUtil.ButtonInfo(
                BUTTON_AUTOROTATE, R.string.title_toggle_autorotate, com.android.internal.R.drawable.stat_orientation_off));
        BUTTONS.put(BUTTON_BLUETOOTH, new PowerWidgetUtil.ButtonInfo(
                BUTTON_BLUETOOTH, R.string.title_toggle_bluetooth, com.android.internal.R.drawable.stat_bluetooth_on));
        BUTTONS.put(BUTTON_BRIGHTNESS, new PowerWidgetUtil.ButtonInfo(
                BUTTON_BRIGHTNESS, R.string.title_toggle_brightness, com.android.internal.R.drawable.stat_brightness_on));
        BUTTONS.put(BUTTON_GPS, new PowerWidgetUtil.ButtonInfo(
                BUTTON_GPS, R.string.title_toggle_gps, com.android.internal.R.drawable.stat_gps_on));
        BUTTONS.put(BUTTON_LOCKSCREEN, new PowerWidgetUtil.ButtonInfo(
                BUTTON_LOCKSCREEN, R.string.title_toggle_lockscreen, com.android.internal.R.drawable.stat_lock_screen_off));
        BUTTONS.put(BUTTON_MOBILEDATA, new PowerWidgetUtil.ButtonInfo(
                BUTTON_MOBILEDATA, R.string.title_toggle_mobiledata, com.android.internal.R.drawable.stat_data_on));
        BUTTONS.put(BUTTON_NETWORKMODE, new PowerWidgetUtil.ButtonInfo(
                BUTTON_NETWORKMODE, R.string.title_toggle_networkmode, com.android.internal.R.drawable.stat_2g3g_on));
        BUTTONS.put(BUTTON_SCREENTIMEOUT, new PowerWidgetUtil.ButtonInfo(
                BUTTON_SCREENTIMEOUT, R.string.title_toggle_screentimeout, com.android.internal.R.drawable.stat_screen_timeout_on));
        BUTTONS.put(BUTTON_SLEEP, new PowerWidgetUtil.ButtonInfo(
                BUTTON_SLEEP, R.string.title_toggle_sleep,com.android.internal.R.drawable.stat_sleep));
        BUTTONS.put(BUTTON_SOUND, new PowerWidgetUtil.ButtonInfo(
                BUTTON_SOUND, R.string.title_toggle_sound, com.android.internal.R.drawable.stat_ring_on));
        BUTTONS.put(BUTTON_SYNC, new PowerWidgetUtil.ButtonInfo(
                BUTTON_SYNC, R.string.title_toggle_sync, com.android.internal.R.drawable.stat_sync_on));
        BUTTONS.put(BUTTON_WIFI, new PowerWidgetUtil.ButtonInfo(
                BUTTON_WIFI, R.string.title_toggle_wifi, com.android.internal.R.drawable.stat_wifi_on));
        BUTTONS.put(BUTTON_WIFIAP, new PowerWidgetUtil.ButtonInfo(
                BUTTON_WIFIAP, R.string.title_toggle_wifiap, com.android.internal.R.drawable.stat_wifi_ap_on));
    }

    private static final String BUTTON_DELIMITER = "|";
    private static final String BUTTONS_DEFAULT = BUTTON_WIFI
                             + BUTTON_DELIMITER + BUTTON_BLUETOOTH
                             + BUTTON_DELIMITER + BUTTON_GPS
                             + BUTTON_DELIMITER + BUTTON_SOUND;

    public static String getCurrentButtons(Context context) {
        String buttons = Settings.System.getString(context.getContentResolver(), Settings.System.WIDGET_BUTTONS);
        if(buttons == null) { buttons = BUTTONS_DEFAULT; }
        return buttons;
    }

    public static void saveCurrentButtons(Context context, String buttons) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.WIDGET_BUTTONS, buttons);
    }

    public static String mergeInNewButtonString(String oldString, String newString) {
        ArrayList<String> oldList = getButtonListFromString(oldString);
        ArrayList<String> newList = getButtonListFromString(newString);
        ArrayList<String> mergedList = new ArrayList<String>();

        // add any items from oldlist that are in new list
        for(String button : oldList) {
            if(newList.contains(button)) {
                mergedList.add(button);
            }
        }

        // append anything in newlist that isn't already in the merged list to the end of the list
        for(String button : newList) {
            if(!mergedList.contains(button)) {
                mergedList.add(button);
            }
        }

        // return merged list
        return getButtonStringFromList(mergedList);
    }

    public static ArrayList<String> getButtonListFromString(String buttons) {
        return new ArrayList<String>(Arrays.asList(buttons.split("\\|")));
    }

    public static String getButtonStringFromList(ArrayList<String> buttons) {
        if(buttons == null || buttons.size() <= 0) {
            return "";
        } else {
            String s = buttons.get(0);
            for(int i = 1; i < buttons.size(); i++) {
                s += BUTTON_DELIMITER + buttons.get(i);
            }
            return s;
        }
    }

    public static class ButtonInfo {
        private String mId;
        private int mTitleResId;
        private int mIconResId;

        public ButtonInfo(String id, int titleResId, int iconResId) {
            mId = id;
            mTitleResId = titleResId;
            mIconResId = iconResId;
        }

        public String getId() { return mId; }
        public int getTitleResId() { return mTitleResId; }
        public int getIconResId() { return mIconResId; }
    }
}

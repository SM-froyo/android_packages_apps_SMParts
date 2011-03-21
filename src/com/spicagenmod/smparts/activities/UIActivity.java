package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

import android.app.ActivityManagerNative;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.content.res.Configuration;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.IWindowManager;

public class UIActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    /* Preference Screens */
    private static final String STATUS_BAR_SCREEN = "status_bar_settings";
    private static final String DATE_PROVIDER_SCREEN = "date_provider_settings";
    private static final String NOTIFICATION_SCREEN = "notification_settings";
    private static final String EXTRAS_SCREEN = "tweaks_extras";
    private static final String GENERAL_CATEGORY = "general_category";
    private static final String UI_EXP_WIDGET = "expanded_widget";
    private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";
    private static final String UI_EXP_WIDGET_PICKER = "widget_picker";
    private static final String UI_EXP_WIDGET_ORDER = "widget_order";
    private static final String WINDOW_ANIMATIONS_PREF = "window_animations";
    private static final String TRANSITION_ANIMATIONS_PREF = "transition_animations";
    private static final String FANCY_IME_ANIMATIONS_PREF = "fancy_ime_animations";
    private static final String FONT_SIZE_PREF = "font_size";

    private PreferenceScreen mStatusBarScreen;
    private PreferenceScreen mDateProviderScreen;
    private PreferenceScreen mNotificationScreen;
    private PreferenceScreen mExtrasScreen;

    /* Other */
    private static final String PINCH_REFLOW_PREF = "pref_pinch_reflow";
    private static final String POWER_PROMPT_PREF = "power_dialog_prompt";
    private static final String OVERSCROLL_PREF = "pref_overscroll_effect";
    private static final String OVERSCROLL_WEIGHT_PREF = "pref_overscroll_weight";
    private static final String STATUSBAR_MUSIC_CONTROLS = "statusbar_music_controls";
    private static final String STATUSBAR_ALWAYS_MUSIC_CONTROLS = "statusbar_always_music_controls";

    private CheckBoxPreference mMusicControlPref;
    private CheckBoxPreference mAlwaysMusicControlPref;
    private CheckBoxPreference mPinchReflowPref;
    private CheckBoxPreference mPowerPromptPref;

	private ListPreference mWindowAnimationsPref;
	private ListPreference mTransitionAnimationsPref;
	private ListPreference mFontSizePref;

    float mWindowAnimationScale;
    float mTransitionAnimationScale;
    
    private IWindowManager mWindowManager;

    private final Configuration mCurConfig = new Configuration();

    private CheckBoxPreference mPowerWidget;
    private CheckBoxPreference mPowerWidgetHideOnChange;
	private CheckBoxPreference mFancyImeAnimationsPref;

    private PreferenceScreen mPowerPicker;
    private PreferenceScreen mPowerOrder;

    private ListPreference mOverscrollPref;
    private ListPreference mOverscrollWeightPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.interface_settings_title_head);
        addPreferencesFromResource(R.xml.ui_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Preference Screens */
        mStatusBarScreen = (PreferenceScreen) prefSet.findPreference(STATUS_BAR_SCREEN);
        mDateProviderScreen = (PreferenceScreen) prefSet.findPreference(DATE_PROVIDER_SCREEN);
        mNotificationScreen = (PreferenceScreen) prefSet.findPreference(NOTIFICATION_SCREEN);
        mExtrasScreen = (PreferenceScreen) prefSet.findPreference(EXTRAS_SCREEN);

        /* Pinch reflow */
        mPinchReflowPref = (CheckBoxPreference) prefSet.findPreference(PINCH_REFLOW_PREF);
        mPinchReflowPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.WEB_VIEW_PINCH_REFLOW, 0) == 1);

        mPowerPromptPref = (CheckBoxPreference) prefSet.findPreference(POWER_PROMPT_PREF);
        updateFlingerOptions();

		/* Animation */
		mWindowAnimationsPref = (ListPreference) prefSet.findPreference(WINDOW_ANIMATIONS_PREF);
		mWindowAnimationsPref.setOnPreferenceChangeListener(this);
		mTransitionAnimationsPref = (ListPreference) prefSet.findPreference(TRANSITION_ANIMATIONS_PREF);
		mTransitionAnimationsPref.setOnPreferenceChangeListener(this);
		mFancyImeAnimationsPref = (CheckBoxPreference) prefSet.findPreference(FANCY_IME_ANIMATIONS_PREF);
		
		/* Font Size */
		mFontSizePref = (ListPreference) prefSet.findPreference(FONT_SIZE_PREF);
		mFontSizePref.setOnPreferenceChangeListener(this);
	
		/* Expanded View Power Widget */
		mPowerWidget = (CheckBoxPreference) prefSet.findPreference(UI_EXP_WIDGET);
		mPowerWidgetHideOnChange = (CheckBoxPreference) prefSet
			.findPreference(UI_EXP_WIDGET_HIDE_ONCHANGE);

		mPowerPicker = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_PICKER);
        mPowerOrder = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_ORDER);
		mPowerWidget.setChecked((Settings.System.getInt(getContentResolver(),
			Settings.System.EXPANDED_VIEW_WIDGET, 1) == 1));
		mPowerWidgetHideOnChange.setChecked((Settings.System.getInt(getContentResolver(),
			Settings.System.EXPANDED_HIDE_ONCHANGE, 0) == 1));

        /* Status Music Controls */
        mMusicControlPref = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_MUSIC_CONTROLS);
        mMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.STATUSBAR_MUSIC_CONTROLS, 0) == 1);
        mAlwaysMusicControlPref = (CheckBoxPreference) prefSet.findPreference(STATUSBAR_ALWAYS_MUSIC_CONTROLS);
        mAlwaysMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.STATUSBAR_ALWAYS_MUSIC_CONTROLS, 0) == 1);

        /* Overscroll Effect */
        mOverscrollPref = (ListPreference) prefSet.findPreference(OVERSCROLL_PREF);
        int overscrollEffect = Settings.System.getInt(getContentResolver(),
                Settings.System.ALLOW_OVERSCROLL, 1);
        mOverscrollPref.setValue(String.valueOf(overscrollEffect));
        mOverscrollPref.setOnPreferenceChangeListener(this);

        mOverscrollWeightPref = (ListPreference) prefSet.findPreference(OVERSCROLL_WEIGHT_PREF);
        int overscrollWeight = Settings.System.getInt(getContentResolver(),
                Settings.System.OVERSCROLL_WEIGHT, 5);
        mOverscrollWeightPref.setValue(String.valueOf(overscrollWeight));
        mOverscrollWeightPref.setOnPreferenceChangeListener(this);
		
		mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));

		try{
        	mWindowAnimationsPref.setValueIndex(floatToIndex(mWindowManager.getAnimationScale(0), R.array.entryvalues_animations));
        	mTransitionAnimationsPref.setValueIndex(floatToIndex(mWindowManager.getAnimationScale(1), R.array.entryvalues_animations));
        	mFancyImeAnimationsPref.setChecked(Settings.System.getInt( getContentResolver(), Settings.System.FANCY_IME_ANIMATIONS, 0) !=0);
			mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
			mFontSizePref.setValueIndex(floatToIndex(mCurConfig.fontScale, R.array.values_font_size));
		} catch (RemoteException e) {
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mStatusBarScreen) {
        	startActivity(mStatusBarScreen.getIntent());
        }
        if (preference == mDateProviderScreen) {
        	startActivity(mDateProviderScreen.getIntent());
        }
        if (preference == mNotificationScreen) {
            startActivity(mNotificationScreen.getIntent());
        }
        if (preference == mExtrasScreen) {
            startActivity(mExtrasScreen.getIntent());
        }
        if (preference == mPowerPicker) {
            startActivity(mPowerPicker.getIntent());
        }
        if (preference == mPowerOrder) {
            startActivity(mPowerOrder.getIntent());
        }
        if (preference == mPinchReflowPref) {
			value = mPinchReflowPref.isChecked();
			Settings.System.putInt(getContentResolver(), Settings.System.WEB_VIEW_PINCH_REFLOW,
				    value ? 1 : 0);
        }

        if (preference == mPowerPromptPref) {
            value = mPowerPromptPref.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.POWER_DIALOG_PROMPT,
                    value ? 1 : 0);
        }

        if (preference == mPowerWidget) {
            value = mPowerWidget.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_VIEW_WIDGET,
                    value ? 1 : 0);
        }

        if (preference == mPowerWidgetHideOnChange) {
            value = mPowerWidgetHideOnChange.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_HIDE_ONCHANGE,
                    value ? 1 : 0);
        }

        if (preference == mMusicControlPref) {
            value = mMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_MUSIC_CONTROLS, value ? 1 : 0);
        }

        if (preference == mAlwaysMusicControlPref) {
            value = mAlwaysMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_ALWAYS_MUSIC_CONTROLS, value ? 1 : 0);
        }

		if (preference == mFancyImeAnimationsPref) {
			Settings.System.putInt(getContentResolver(),
				Settings.System.FANCY_IME_ANIMATIONS,
				mFancyImeAnimationsPref.isChecked() ? 1 : 0);
		}
		
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mOverscrollPref) {
            int overscrollEffect = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.ALLOW_OVERSCROLL,
                    overscrollEffect);
            return true;
        } else if (preference == mOverscrollWeightPref) {
            int overscrollWeight = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_WEIGHT,
                    overscrollWeight);
            return true;
        } else if (preference == mWindowAnimationsPref) {
			float val = Float.parseFloat(newValue.toString());
            try {
			    mWindowManager.setAnimationScale(0, val);
            } catch (RemoteException e) {
            }
			return true;
		} else if (preference == mTransitionAnimationsPref) {
			float val = Float.parseFloat(newValue.toString());
            try {
			    mWindowManager.setAnimationScale(1, val);
            } catch (RemoteException e) {
            }
			return true;
        } else if (preference == mFontSizePref) {
            try {
                mCurConfig.fontScale = Float.parseFloat(newValue.toString());
                ActivityManagerNative.getDefault().updateConfiguration(mCurConfig);
            } catch (RemoteException e) {
            }
        }
        return false;
    }

    // Taken from DevelopmentSettings
    private void updateFlingerOptions() {
        // magic communication with surface flinger.
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                flinger.transact(1010, data, reply, 0);
                int v;
                v = reply.readInt();
                // mShowCpuCB.setChecked(v != 0);
                v = reply.readInt();
                // mEnableGLCB.setChecked(v != 0);
                v = reply.readInt();
                // mShowUpdatesCB.setChecked(v != 0);
                v = reply.readInt();
                // mShowBackgroundCB.setChecked(v != 0);

                v = reply.readInt();

                reply.recycle();
                data.recycle();
            }
        } catch (RemoteException ex) {
        }

    }
	
	int floatToIndex(float val, int resid) {
		String[] indices = getResources().getStringArray(resid);
		float lastVal = Float.parseFloat(indices[0]);
		for (int i=1; i<indices.length; i++) {
			float thisVal = Float.parseFloat(indices[i]);
			if (val < (lastVal + (thisVal-lastVal)*.5f)) {
				return i-1;
			}
			lastVal = thisVal;
		}
		return indices.length-1;
	}
}

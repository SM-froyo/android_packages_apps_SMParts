package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

public class WidgetOptionsActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	
	public int mAppWidgetId;
	public Context mContext;
	public SharedPreferences mPreferences;
	private ListPreference mRenderFxPref;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.options);
		this.mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.mContext = this;
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		mRenderFxPref = (ListPreference)findPreference("widget_render_effect");
		mRenderFxPref.setSummary(mRenderFxPref.getEntry());
		mRenderFxPref.setOnPreferenceChangeListener(this);
		
		Preference mSave = findPreference("save_settings");
		mSave.setOnPreferenceClickListener(savePrefListener);
	}
	
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRenderFxPref) {
            if (newValue != null) {
                int index = mRenderFxPref.findIndexOfValue(newValue.toString());
    			Editor editor = mPreferences.edit();
	    		editor.putString("widget_render_effect", newValue.toString());
	    		editor.commit();
                mRenderFxPref.setSummary(mRenderFxPref.getEntries()[index]);
            }
        }
        return false;
    }
	
	private OnPreferenceClickListener savePrefListener = new OnPreferenceClickListener() {
		
		@Override		
		public boolean onPreferenceClick(Preference preference) {
			Editor editor = mPreferences.edit();
			editor.putInt("widget_render_effect_" + mAppWidgetId, Integer.parseInt(mPreferences.getString("widget_render_effect", "1")));
			editor.commit();			
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
			
			return false;
		}        
	};  
}

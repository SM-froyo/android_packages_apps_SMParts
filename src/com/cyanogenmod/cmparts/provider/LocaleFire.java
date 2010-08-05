package com.cyanogenmod.cmparts.provider;

import com.cyanogenmod.cmparts.activities.LocaleRenderEdit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class LocaleFire extends BroadcastReceiver
{
	// from locale_platform.jar
	private static final String ACTION_FIRE_SETTING = "com.twofortyfouram.locale.intent.action.FIRE_SETTING";

    private static final String RENDER_EFFECT_PREF = "pref_render_effect";

	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		if (ACTION_FIRE_SETTING.equals(intent.getAction())) {
			final int effect = intent.getIntExtra(LocaleRenderEdit.EXTRA_RENDER_EFFECT, 0);
			FlingerPinger.writeRenderEffect(effect);
			SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
			ed.putString(RENDER_EFFECT_PREF, String.valueOf(effect));
			ed.commit();
		}
	}
}

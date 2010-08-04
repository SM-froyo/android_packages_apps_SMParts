package com.cyanogenmod.cmparts.provider;

import com.cyanogenmod.cmparts.activities.LocaleEdit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class LocaleFire extends BroadcastReceiver
{
	// from locale_platform.jar
	private static final String ACTION_FIRE_SETTING = "com.twofortyfouram.locale.intent.action.FIRE_SETTING";

	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		if (ACTION_FIRE_SETTING.equals(intent.getAction())) {
			final boolean toggle = intent.getBooleanExtra(LocaleEdit.EXTRA_TOGGLE, false);
			FlingerPinger.writeRenderEffect(toggle ? 1 : 0);
		}
	}
}

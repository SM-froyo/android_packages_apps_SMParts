package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public final class LocaleEdit extends Activity
{
	public static final String EXTRA_TOGGLE = "com.cyanogenmod.cmparts.intent.extra.toggle";

	private static final int MENU_SAVE = 1;
	private static final int MENU_DONT_SAVE = 2;

	// from locale_platform.jar
	private static final String EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
	private static final int MAXIMUM_BLURB_LENGTH = 40;
	private static final String EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";

	private boolean isCancelled = false;
	ToggleButton tb;
	TextView hint;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locale_edit);
		tb = (ToggleButton)findViewById(R.id.locale_toggle);
		hint = (TextView)findViewById(R.id.locale_hint);
		tb.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateHint();
			}
		});

		if (savedInstanceState == null) {
			final Bundle forwardedBundle = getIntent().getBundleExtra(EXTRA_BUNDLE);
			if (forwardedBundle != null) {
				final boolean checked = getIntent().getBooleanExtra(EXTRA_TOGGLE, false);
				tb.setChecked(checked);
			}
		}
		updateHint();
	}

	public void updateHint()
	{
		hint.setText(getString(tb.isChecked() ? R.string.locale_onBlurb : R.string.locale_offBlurb));
	}

	@Override
	public void finish()
	{
		if (isCancelled) {
			setResult(RESULT_CANCELED);
		} else {
			final boolean checked = ((ToggleButton) findViewById(R.id.locale_toggle)).isChecked();
			final Intent returnIntent = new Intent();
			final Bundle storeAndForwardExtras = new Bundle();
			storeAndForwardExtras.putBoolean(EXTRA_TOGGLE, checked);
			returnIntent.putExtra(EXTRA_BUNDLE, storeAndForwardExtras);
			String message = getString(checked ? R.string.locale_onBlurb : R.string.locale_offBlurb);
			if (message.length() > MAXIMUM_BLURB_LENGTH)
				returnIntent.putExtra(EXTRA_STRING_BLURB, message.substring(0, MAXIMUM_BLURB_LENGTH));
			else
				returnIntent.putExtra(EXTRA_STRING_BLURB, message);

			setResult(RESULT_OK, returnIntent);
		}

		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		final PackageManager manager = getPackageManager();
		menu.add(0, MENU_DONT_SAVE, 0, R.string.locale_dont_save);
		menu.add(0, MENU_SAVE, 0, R.string.locale_save);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item)
	{
		switch (item.getItemId()) {
			case MENU_SAVE:
				finish();
				return true;
			case MENU_DONT_SAVE:
				isCancelled = true;
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
}

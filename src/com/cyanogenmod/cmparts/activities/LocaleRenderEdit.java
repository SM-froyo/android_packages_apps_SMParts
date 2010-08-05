package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.provider.FlingerPinger;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public final class LocaleRenderEdit extends ListActivity
{
	public static final String EXTRA_RENDER_EFFECT = "com.cyanogenmod.cmparts.intent.extra.RENDER_EFFECT";

	private static final int MENU_SAVE = 1;
	private static final int MENU_DONT_SAVE = 2;

	// from locale_platform.jar
	private static final String EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
	private static final int MAXIMUM_BLURB_LENGTH = 40;
	private static final String EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";
	private static final String EXTRA_STRING_BREADCRUMB = "com.twofortyfouram.locale.intent.extra.BREADCRUMB";
	private static final String BREADCRUMB_SEPARATOR = " > ";

	private boolean isCancelled = false;
	private int previous = -1;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final String breadcrumbString = getIntent().getStringExtra(EXTRA_STRING_BREADCRUMB);
		if (breadcrumbString != null)
			setTitle(String.format("%s%s%s", breadcrumbString, BREADCRUMB_SEPARATOR, getString(R.string.pref_render_effect_title))); //$NON-NLS-1$
		final ListView lv = getListView();

		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setListAdapter(ArrayAdapter.createFromResource(this, R.array.entries_render_effect, android.R.layout.simple_list_item_single_choice));

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FlingerPinger.writeRenderEffect(position);
			}
		});

		if (savedInstanceState == null) {
			final Bundle forwardedBundle = getIntent().getBundleExtra(EXTRA_BUNDLE);
			if (forwardedBundle != null) {
				final int effect = getIntent().getIntExtra(EXTRA_RENDER_EFFECT, 0);
				lv.setItemChecked(effect, true);
			} else {
				lv.setItemChecked(0, true);
			}
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		previous = FlingerPinger.readRenderEffect();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (previous >= 0)
			FlingerPinger.writeRenderEffect(previous);
	}

	@Override
	public void finish()
	{
		if (isCancelled) {
			setResult(RESULT_CANCELED);
		} else {
			final int effect = getListView().getCheckedItemPosition();
			final Intent returnIntent = new Intent();
			final Bundle storeAndForwardExtras = new Bundle();
			storeAndForwardExtras.putInt(EXTRA_RENDER_EFFECT, effect);
			returnIntent.putExtra(EXTRA_BUNDLE, storeAndForwardExtras);
			String message = (effect >= 0) ? getResources().getStringArray(R.array.entries_render_effect)[effect] : "unknown!";
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

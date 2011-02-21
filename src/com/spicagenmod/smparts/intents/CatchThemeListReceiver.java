package com.spicagenmod.smparts.intents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.spicagenmod.smparts.activities.TweaksExtras;
import com.spicagenmod.smparts.R;


public class CatchThemeListReceiver extends BroadcastReceiver {

    public static final String catchList = "com.cyanogenmod.smpartshelper.GET_THEME_LIST";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(catchList)) {
            Bundle extras = intent.getExtras();
            int success = extras.getInt("gotfile");
            if (success == 1) { 
                String sdList[] = extras.getStringArray("filelist");
                TweaksExtras.buildFileList(sdList, true);
            } else {
                TweaksExtras.buildFileList(null, false);
            }
        }
    }
}

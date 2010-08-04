package com.cyanogenmod.cmparts.services;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.provider.FlingerPinger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

public class RenderFXService extends Service {
	
	public static final String MSG_TAG = "RenderFXService";
    private NotificationManager mNotificationManager;
    private Notification mNotification;
	
	public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			FlingerPinger.writeRenderEffect(intent.getIntExtra("widget_render_effect", 1));
		}
		
		mNotification = new Notification(R.drawable.notification_icon, getResources().getString(R.string.notify_render_effect),
                                System.currentTimeMillis());

        startForeground(0, mNotification);
		
		return START_STICKY;
	}
	
	public void onDestroy() {
	    FlingerPinger.writeRenderEffect(0);
		stopForeground(true);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}

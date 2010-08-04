package com.cyanogenmod.cmparts.provider;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;

public class FlingerPinger
{
	public static void writeRenderEffect(int mRenderEffect)
	{
		try {
			IBinder flinger = ServiceManager.getService("SurfaceFlinger");
			if (flinger != null) {
				Parcel data = Parcel.obtain();
				data.writeInterfaceToken("android.ui.ISurfaceComposer");
				data.writeInt(mRenderEffect);
				flinger.transact(1014, data, null, 0);
				data.recycle();
			}
		} catch (RemoteException ex) {
		}
	}
}

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

    // Taken from DevelopmentSettings
	public static int readRenderEffect()
	{
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
				return v;
			}
		} catch (RemoteException ex) {
		}
		return -1;
	}
}

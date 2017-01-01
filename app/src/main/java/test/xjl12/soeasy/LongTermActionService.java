package test.xjl12.soeasy;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.widget.*;
import java.io.*;

import java.lang.Process;


public class LongTermActionService extends Service 
{
	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder
	{
		LongTermActionService getService()
		{
			return LongTermActionService.this;
		}
	}
	@Override
	public IBinder onBind(Intent p1)
	{
		return mBinder;
	}
	
	public void SendShot()
	{
		File shot_file = new File(getExternalCacheDir(), Others.RandomString(5) + ".png");
		ProcessBuilder pb = new ProcessBuilder(new String[] {"screencap","-p",shot_file.toString()});
		try
		{
			Process proc = pb.start();
			try
			{
				proc.waitFor();
			}
			catch (InterruptedException e)
			{}
		}
		catch (IOException e)
		{}
		if (shot_file.exists())
		{
			Intent send = new Intent(Intent.ACTION_SEND);
			send.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(shot_file));
			send.setType("image/png");
			startActivity(Intent.createChooser(Others.isQQInstalled(this, send),getString(R.string.share_point)));
		}
		Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
	}
	/*@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
	}*/
}

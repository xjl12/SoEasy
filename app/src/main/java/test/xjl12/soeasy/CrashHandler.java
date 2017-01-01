package test.xjl12.soeasy;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import android.net.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.lang.Thread.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import java.util.Map.*;

import android.os.Process;

public class CrashHandler implements UncaughtExceptionHandler
{
    private static CrashHandler INSTANCE = new CrashHandler();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private Map<String, String> infos = new HashMap<String,String>();
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;

    CrashHandler()
	{
    }

    public static CrashHandler getInstance()
	{
        return INSTANCE;
    }

    public void init(Context context)
	{
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable th)
	{
        if (handleException(th) || this.mDefaultHandler == null)
		{
            try
			{
                Thread.sleep((long) 1000);
            }
			catch (InterruptedException e)
			{
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
            return;
        }
        this.mDefaultHandler.uncaughtException(thread, th);
    }

    private boolean handleException(Throwable th)
	{
        if (th == null)
		{
            return false;
        }
        collectDeviceInfo(this.mContext);
        if (Environment.getExternalStorageState().equals("mounted"))
		{
            File saveCrashInfo2File = saveCrashInfo2File(th);
            if (saveCrashInfo2File != null) 
			{
                ((AlarmManager) mContext.getSystemService("alarm")).set(1, System.currentTimeMillis() + ((long) 100), PendingIntent.getActivity(mContext, 0, Intent.createChooser(Others.isQQInstalled(mContext, new Intent(Intent.ACTION_SEND).setData(Uri.fromFile(saveCrashInfo2File)).setType("text/*")), mContext.getResources().getString(R.string.fc_and_send)), 268435456));
                return true;
            }
            Toast.makeText(mContext, this.mContext.getResources().getString(R.string.fc), 0).show();
            return false;
        }
        Toast.makeText(mContext, this.mContext.getResources().getString(R.string.fc), 0).show();
        return false;
    }

	public void collectDeviceInfo(Context ctx)
	{
		try
		{
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null)
			{
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		}
		catch (NameNotFoundException e)
		{}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields)
		{
			try
			{
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			}
			catch (Exception e)
			{}
		}
	}


    private File saveCrashInfo2File(Throwable th)
	{
        StringBuffer stringBuffer = new StringBuffer();
        for (Entry entry : this.infos.entrySet())
		{
            String str = (String) entry.getKey();
            stringBuffer.append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(str).append("=").toString()).append((String) entry.getValue()).toString()).append("\n").toString());
        }
        Writer stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause())
		{
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        stringBuffer.append(stringWriter.toString());
        try
		{
            File file = new File(this.mContext.getExternalFilesDir("Log"), new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("log-").append(this.formatter.format(new Date())).toString()).append("-").toString()).append(System.currentTimeMillis()).toString()).append(".log").toString());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(stringBuffer.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            return file;
        }
		catch (Exception e)
		{
            return (File) null;
        }
    }
}

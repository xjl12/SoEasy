package test.xjl12.soeasy;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import android.net.*;

public class Others
{
	//判断QQ是否安装
	public static boolean isQQInstalled(Context context)
	{
        PackageInfo packageInfo = null;
        try
		{
			packageInfo = context.getPackageManager().getPackageInfo(context.getResources().getString(R.string.qq_name), 0);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			packageInfo = null;
		}

        if (packageInfo == null) 
		{
            return false;
        } 
		else
		{
            return true;
        }
    }
	//获取程序版本号
	public static String getAppVersionName(Context context)
	{
		String app_version;
		try
		{
			PackageInfo app_pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			app_version = app_pinfo.versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			Toast.makeText(context,R.string.Error_PackageManager_NameNotFoundException,Toast.LENGTH_LONG).show();
			app_version = context.getResources().getString(R.string.unknow);
		}
		return app_version;
	}
	//获取Activity名字
	public static String getRunningActivityName(Context context)
	{    
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
	}
	//改变编辑框为非输入状态并收回软键盘
	public static void ChangeEdittextStatusAndHideSoftInput(Context context,View parent_view,TextInputEditText edittext)
	{
		parent_view.setFocusable(true);
		parent_view.setFocusableInTouchMode(true);
		parent_view.requestFocus();
		
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edittext.getWindowToken(),0);
	}
	//获取屏幕截图并发送(screencap)
	public static void SendShot(Context context)
	{
		File shot_file = new File(context.getExternalCacheDir(),Others.RandomString(5) + ".png");
		try
		{
			Process proccess = Runtime.getRuntime().exec("sh");
			PrintStream outs = new PrintStream(new BufferedOutputStream(proccess.getOutputStream(), 8192));
			outs.println("screencap -p" + shot_file.getAbsolutePath());
			outs.flush();
			outs.close();
			try
			{
				proccess.waitFor();
			}
			catch (InterruptedException e)
			{}
			proccess.destroy();
		}
		catch (IOException e)
		{}
		if (shot_file.exists() == false)
		{
			
		}
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setData(Uri.fromFile(shot_file));
		send.setType("image/*");
		if (Others.isQQInstalled(context))
		{
			send.setPackage(context.getString(R.string.qq_name));
		}
		context.startActivity(Intent.createChooser(send,context.getString(R.string.share_point)));
	}
	//改变Snackbar背景,字体颜色
	public static void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) 
	{
		View view = snackbar.getView();
		if (view != null)
		{ 
			view.setBackgroundColor(backgroundColor);
			((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
		} 
	}

	//产生一个随机的字符串
	public static String RandomString(int length)
	{
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			int num = random.nextInt(62);
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}
	//删除目录内所有文件文件
	public static boolean DeleteDirAllFile(File dir)
	{
		File cache_dir_file;
		String[] cache_dir_files = dir.list();
		for (int f = 0;f < cache_dir_files.length;f++)
		{
			cache_dir_file = new File(dir, cache_dir_files[f]);
			cache_dir_file.delete();
		}
		return true;
	}
	//输入流转文本
	public static String getString(InputStream is)
	{
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer("");
		String line;
		try
		{
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
				//sb.append("/n");
			}
		}
		catch (IOException e)
		{
			Log.e("Read", "Read test.txt error");
 			return "Error:java.io.IOException";
		}
		return sb.toString();
	}
	// 获取当前活动的截屏
	public static File getShot(Activity activity, File shot_file) throws FileNotFoundException, IOException
	{
		View view = activity.getWindow().getDecorView();
		view.buildDrawingCache();
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		int status_bar_height = rect.top;
		Display display = activity.getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, status_bar_height, width, height - status_bar_height);
		view.destroyDrawingCache();
		FileOutputStream shot_fos = new FileOutputStream(shot_file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, shot_fos);
		shot_fos.flush();
		shot_fos.close();
		return shot_file;
	}

	public static Activity getGlobleActivity() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException
	{
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        for (Object activityRecord:activities.values())
		{
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord))
			{
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }
        return null;
    }

}

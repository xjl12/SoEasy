package test.xjl12.soeasy;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import android.support.v7.widget.Toolbar;
import java.lang.Process;
import android.accounts.*;

public class Others
{
	public static final String separator = ";";
	//初始化activity
	public static void initActivity(AppCompatActivity mdActivity, Toolbar toolbar)
	{
		mdActivity.setSupportActionBar(toolbar);
		mdActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mdActivity.getSupportActionBar().setHomeButtonEnabled(true);
	}
	//判断程序是否安装
	public static boolean isAppInstalled(Context context, String package_name)
	{
		try
		{
			context.getPackageManager().getPackageInfo(package_name, 0);
			return true;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return false;
		}
	}
	//判断我的世界版本
	public static boolean CheckMyWorldVersion(Context context)
	{
		int versionCode = -1;
		String my_world = context.getString(R.string.my_world_name);
		List<PackageInfo> p_infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
		for (PackageInfo p_info : p_infos)
		{
			if (p_info.packageName.equals(my_world))
			{
				versionCode = p_info.versionCode;
			}
		}

		if (versionCode == 870160005)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	//判断QQ是否安装
	public static Intent isQQInstalled(Context context, Intent intent)
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
            return intent;
        } 
		else
		{
            return intent.setPackage(context.getString(R.string.qq_name));
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
			Toast.makeText(context, R.string.Error_PackageManager_NameNotFoundException, Toast.LENGTH_LONG).show();
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
	//反馈
	public static void feebbackErrorInfo (final String info,final Context context,final Handler mHandle)
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					Intent feebback;
					final String feebback_message = context.getString(R.string.send_error_message, Others.getAppVersionName(context), Others.getRunningActivityName(context),info == null? context.getString(R.string.wrong):info);
					if (Others.isAppInstalled(context,context.getString(R.string.qq_name)))
					{
						feebback = new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=1062256455"));
						mHandle.post(new Runnable(){

								@Override
								public void run()
								{
									ClipboardManager clip = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
									clip.setText(feebback_message);
								}

							});
					}
					else
					{
						feebback = new Intent(Intent.ACTION_SEND);
						feebback.setType("text/plain");
					}
					feebback.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					feebback.putExtra(Intent.EXTRA_TEXT,feebback_message);
					try
					{
						Thread.sleep(100L);
					}
					catch (InterruptedException e)
					{}
					context.startActivity(feebback);
				}
			}).start();
		Toast.makeText(context,context.getString(R.string.feebback_point),Toast.LENGTH_LONG).show();
	}
	//判断存储设备是否可写
	public static boolean checkIsStorageWritable(Context context)
	{
		try
		{
			File test_file = new File(context.getExternalCacheDir(), RandomString(5));
			OutputStream os = new FileOutputStream(test_file);
			os.write(RandomString(16).getBytes());
			os.flush();
			os.close();
			test_file.delete();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}

	public static String checkStorageWritableWithInfo(Context context)
	{
		try
		{
			File test_file = new File(context.getExternalCacheDir(), RandomString(5));
			OutputStream os = new FileOutputStream(test_file);
			os.write(RandomString(16).getBytes());
			os.flush();
			os.close();
			test_file.delete();
		}
		catch (IOException e)
		{
			return e.toString();
		}
		return null;
	}
	//获取目录结构
	public static String getDiretoryStructureForString(File dir, String dir_name)
	{
		StringBuffer result = new StringBuffer();
		if (dir.isDirectory())
		{
			File[] lists = dir.listFiles();
			if (lists.length == 0 && dir_name == null)
			{
				result.append(dir.getName() + File.separator);
			}
			else if (lists.length == 0)
			{
				result.append(dir_name + File.separator + dir.getName() + File.separator);
			}
			else
			{
				for (File file : lists)
				{
					if (dir_name == null)
					{
						result.append(getDiretoryStructureForString(file, dir.getName()));
					}
					else
					{
						result.append(getDiretoryStructureForString(file, dir_name + File.separator + dir.getName()));
					}
				}
			}
		}
		else
		{
			result.append(dir_name + File.separator + dir.getName() + separator);
		}
		return result.toString();
	}

	//流复制
	public static void copyAcordingStream(InputStream is, OutputStream os) throws IOException
	{
		byte[] cache = new byte[1024];
		int length;
		while ((length = is.read(cache)) > 0)
		{
			os.write(cache, 0, length);
		}
	}
	/*	复制文件或文件夹
	 例如:复制/abc目录到/abcd/
	 则应该传入/abc的File对象和/abcd/abc的File对象
	 */
	public static void fileCopy(File src, File dest) throws IOException
	{
		if (src.isDirectory())
		{
			if (!dest.exists())
			{
				dest.mkdir();
			}
			String[] list = src.list();
			for (String list_file_name : list)
			{
				File src_file = new File(src, list_file_name);
				File dest_file = new File(dest, list_file_name);
				fileCopy(src_file, dest_file);
			}
		}
		else
		{
			InputStream is = new FileInputStream(src);
			OutputStream os = new FileOutputStream(dest);
			copyAcordingStream(is, os);
			is.close();
			os.flush();
			os.close();
		}
	}
	/*	解压assets中指定目录到sd卡指定目录
	 例如将指定assets下abc目录所有文件解压到/sdcard/abc中
	 则应传入Context,abc的String对象,/sdcard/abc的File对象
	 */
	public static void assetsCopy(AssetManager am, String assets_path, File dest) throws IOException
	{
		if (dest.isDirectory())
		{
			String[] file_names = am.list(assets_path);
			for (String file_name : file_names)
			{
				File dest_file = new File(dest, file_name);
				String[] sub_list = am.list(assets_path + "/" + file_name);
				if (sub_list.length > 0)
				{
					dest_file.mkdir();
				}
				assetsCopy(am, assets_path + "/" + file_name, dest_file);
			}
		}
		else
		{
			InputStream is = am.open(assets_path);
			OutputStream os = new FileOutputStream(dest);
			copyAcordingStream(is, os);
			is.close();
			os.flush();
			os.close();
		}
	}
	//ZIP压缩方法，支持文件夹压缩

	public static void zipCompression(File src, File target) throws IOException
	{
		InputStream is;
		ZipOutputStream zipOs = new ZipOutputStream(new FileOutputStream(target));
		ZipEntry zipEntry;
		String[] lists = getDiretoryStructureForString(src, null).split(separator);
		for (String file : lists)
		{
			File src_file;
			if (file.endsWith(File.separator))
			{
				zipEntry = new ZipEntry(file);
				zipOs.putNextEntry(zipEntry);
			}
			else
			{
				src_file = new File(src.getParentFile(), file);
				zipEntry = new ZipEntry(file);
				is = new FileInputStream(src_file);
				zipOs.putNextEntry(zipEntry);
				copyAcordingStream(is, zipOs);
				is.close();
			}
		}
		zipOs.flush();
		zipOs.close();
	}
	//Web
	public static String getWebString(String url_str,HttpURLConnection connection) throws Exception
	{
		URL url = new URL(url_str);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setReadTimeout(5000);
		connection.setConnectTimeout(10000);
		
		int request_code = connection.getResponseCode();
		if (request_code == 200)
		{
			InputStream is = connection.getInputStream();
			String request = getString(is);
			return request;
		}
		else
		{
			throw new NetworkErrorException(Integer.toString(request_code));
		}
	}
	//改变编辑框为非输入状态并收回软键盘
	public static void ChangeEdittextStatusAndHideSoftInput(Context context, View parent_view, EditText edittext)
	{
		parent_view.setFocusable(true);
		parent_view.setFocusableInTouchMode(true);
		parent_view.requestFocus();

		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
	}
	//获取屏幕截图并发送(screencap)
	public static Intent SendShot(Context context)
	{
		File shot_file = new File(context.getExternalCacheDir(), Others.RandomString(5) + ".png");
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
			send.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shot_file));
			send.setType("image/png");
			return Intent.createChooser(isQQInstalled(context, send), context.getString(R.string.share_point));
		}
		return null;
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
		if (dir.isDirectory())
		{
			File[] list_files = dir.listFiles();
			for (File file : list_files)
			{
				DeleteDirAllFile(file);
			}
			
		}
		return dir.delete();
	}
	//测试
	public static void test(Context context)
	{
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}
	//输入流转文本
	public static String getString(InputStream is) throws IOException
	{
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer("");
		String line;
		while ((line = br.readLine()) != null)
		{
			sb.append(line);
			//sb.append("\n");
		}
		
		return sb.toString();
	}
	// 获取当前活动的截屏
	public static Intent getShot(Activity activity)
	{
		File shot_file = new File(activity.getExternalCacheDir(), RandomString(5) + ".png");
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
		try
		{
			FileOutputStream shot_fos = new FileOutputStream(shot_file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, shot_fos);
			try
			{
				shot_fos.flush();
				shot_fos.close();
			}
			catch (IOException e)
			{}
		}
		catch (FileNotFoundException e)
		{}

		return Intent.createChooser(isQQInstalled(activity.getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shot_file)).setType("image/png")), activity.getString(R.string.share_point));
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

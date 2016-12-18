package test.xjl12.soeasy;

import android.app.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class Others
{
	/** 产生一个随机的字符串*/
	public static String RandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(62);
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}
	/** 删除目录内所有文件文件 */
	public static boolean DeleteDirAllFile(File dir)
	{
		File cache_dir_file;
		String[] cache_dir_files = dir.list();
		for (int f = 0;f < cache_dir_files.length;f++)
		{
			cache_dir_file = new File (dir,cache_dir_files[f]);
			cache_dir_file.delete();
		}
		return true;
	}
	/** 输入流转文本 */
	public static String getString (InputStream is)
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
			Log.e("Read","Read test.txt error");
 			return "Error:java.io.IOException";
		}
		return sb.toString();
	}
	/** 获取当前活动的截屏 */
	public static File getShot (Activity activity,File shot_file) throws FileNotFoundException, IOException
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
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(),0,status_bar_height,width,height - status_bar_height);
		view.destroyDrawingCache();
		//File shot_file = new File (dir,file_name);
		FileOutputStream shot_fos = new FileOutputStream(shot_file);
		bitmap.compress(Bitmap.CompressFormat.PNG,100,shot_fos);
		shot_fos.flush();
		shot_fos.close();
		return shot_file;
	}
	
	public static Activity getGlobleActivity() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException{
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        for(Object activityRecord:activities.values()){
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if(!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }
        return null;
    }
	
}

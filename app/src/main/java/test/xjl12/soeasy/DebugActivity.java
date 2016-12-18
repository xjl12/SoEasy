package test.xjl12.soeasy;


import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;


public class DebugActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DisplayMetrics screen_info = getResources().getDisplayMetrics();
        setContentView(R.layout.debug);
        TextView screen = (TextView) findViewById(R.id.debug_screen);
        TextView android_version = (TextView) findViewById(R.id.debug_android_version);
        TextView app_version = (TextView) findViewById(R.id.debug_app_version);
        screen.setText(getResources().getString(R.string.debug_screen) + getResources().getString(R.string.debug_screen_width) + screen_info.widthPixels + getResources().getString(R.string.debug_screen_height) + screen_info.heightPixels + getResources().getString(R.string.debug_screen_dpi_before) + screen_info.densityDpi + getResources().getString(R.string.debug_screen_dpi_behind));
        android_version.setText(getResources().getString(R.string.debug_android_version) + android.os.Build.VERSION.SDK );
        PackageManager packagemanager = getPackageManager();
        try
        {
            PackageInfo app_info = packagemanager.getPackageInfo(getPackageName(), 0);
            app_version.setText(getResources().getString(R.string.debug_app_version) + app_info.versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.exit_action:
                finish();
                break;
                /*case R.id.settings_action:
                 openSettings ();
                 return true;
                 */
            default:    
        }

        return super.onOptionsItemSelected(item);
    }
	@Override
	public void onResume ()
	{
		super.onResume();
		Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
	}
	public void SaveAndShare (View view) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException, NoSuchFieldException, InvocationTargetException, IOException
	{
		File shot_file = new File (getApplicationContext().getExternalCacheDir(),Others.RandomString(5) + ".png");
		Others.getShot(Others.getGlobleActivity(),shot_file);
		Intent share_shot_file_intent = new Intent();
		share_shot_file_intent.setAction(Intent.ACTION_SEND);
		share_shot_file_intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(shot_file));
		share_shot_file_intent.setType("image/*");
		startActivity(Intent.createChooser(share_shot_file_intent,getResources().getText(R.string.share_point)));
	}
}

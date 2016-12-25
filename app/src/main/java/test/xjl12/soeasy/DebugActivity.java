package test.xjl12.soeasy;


import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;
import test.xjl12.soeasy.*;


public class DebugActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug);
		
        TextView screen = (TextView) findViewById(R.id.debug_screen);
        TextView android_version = (TextView) findViewById(R.id.debug_android_version);
        TextView app_version = (TextView) findViewById(R.id.debug_app_version);
		
		DisplayMetrics screen_info = getResources().getDisplayMetrics();
        screen.setText(getResources().getString(R.string.debug_screen) + getResources().getString(R.string.debug_screen_width) + screen_info.widthPixels + getResources().getString(R.string.debug_screen_height) + screen_info.heightPixels + getResources().getString(R.string.debug_screen_dpi_before) + screen_info.densityDpi + getResources().getString(R.string.debug_screen_dpi_behind));
        android_version.setText(getResources().getString(R.string.debug_android_version) + android.os.Build.VERSION.SDK );
		app_version.setText(this.getResources().getString(R.string.debug_app_version) + getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }
	@Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            default:
				Snackbar.make(findViewById(R.id.main_mdCoordinatorLayout),R.string.wrong,Snackbar.LENGTH_LONG)
					.setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
					.setAction(R.string.send_error, new View.OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							Intent share_intent = new Intent(Intent.ACTION_SEND);
							share_intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.send_error_message,Others.getAppVersionName(getApplicationContext()),Others.getRunningActivityName(DebugActivity.this),item.getTitle().toString()));
							share_intent.setType("text/plain");
							if (Others.isQQInstalled(getApplicationContext()))
							{
								share_intent.setPackage(getString(R.string.qq_name));
							}
							startActivity(Intent.createChooser(share_intent, getString(R.string.Error_no_item_action)));
						}
					}).show();
				break;
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
		if (Others.isQQInstalled(this))
		{
			share_shot_file_intent.setPackage(this.getResources().getString(R.string.qq_name));
		}
		startActivity(Intent.createChooser(share_shot_file_intent,getResources().getText(R.string.share_point)));
	}
}

package test.xjl12.soeasy;


import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;
import test.xjl12.soeasy.*;

import android.support.v7.widget.Toolbar;


public class DebugActivity extends AppCompatActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug);
		
        final TextView screen = (TextView) findViewById(R.id.debug_screen);
		final TextView screen_size = (TextView) findViewById(R.id.debug_content_screen_size);
        final TextView android_version = (TextView) findViewById(R.id.debug_android_version);
		final CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.debug_mdCoordinatorLayout);
        TextView app_version = (TextView) findViewById(R.id.debug_app_version);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.debug_fab);
		Toolbar toolbar = (Toolbar) findViewById(R.id.debug_mdToolbar);
		
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		DisplayMetrics screen_info = getResources().getDisplayMetrics();
        screen.setText(getResources().getString(R.string.debug_screen) + getResources().getString(R.string.debug_screen_width) + screen_info.widthPixels + getResources().getString(R.string.debug_screen_height) + screen_info.heightPixels + getResources().getString(R.string.debug_screen_dpi_before) + screen_info.densityDpi + getResources().getString(R.string.debug_screen_dpi_behind));
        android_version.setText(getResources().getString(R.string.debug_android_version) + android.os.Build.VERSION.SDK );
		app_version.setText(this.getResources().getString(R.string.debug_app_version) + getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE));
   		
		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent share = new Intent(Intent.ACTION_SEND);
					share.putExtra(Intent.EXTRA_TEXT,getString(R.string.debug_share,android_version.getText(),screen_size.getText(),screen.getText()));
					share.setType("text/plain");
					startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(),share),getString(R.string.share_point)));
				}
			});
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(final MenuItem item)
				{
					switch (item.getItemId())
					{
						case R.id.help_development:
							Intent help_development_web = new Intent(Intent.ACTION_VIEW);
							help_development_web.setData(Uri.parse(getResources().getStringArray(R.array.web_url)[1]));
							startActivity(help_development_web);
							break;
						case R.id.source_item:
							Intent source_web = new Intent(Intent.ACTION_VIEW);
							source_web.setData(Uri.parse(getResources().getStringArray(R.array.web_url)[0]));
							startActivity(source_web);
							break;
						case R.id.shot_item:
							new Thread(new Runnable(){

									@Override
									public void run()
									{
										Intent shot_share = Others.SendShot(getApplicationContext());
										if (shot_share != null)
										{
											startActivity(shot_share);
										}
										else
										{
											Snackbar.make(mCl, R.string.Error_shot_error, Snackbar.LENGTH_LONG)
												.setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
												.setAction(R.string.retry, new View.OnClickListener(){

													@Override
													public void onClick(View p1)
													{
														Intent intent = Others.getShot(DebugActivity.this);
														if (intent != null)
														{
															startActivity(intent);
														}
														else
														{
															Toast.makeText(DebugActivity.this,getString(R.string.Error_cannot_shot),Toast.LENGTH_LONG);
														}
													}
												}).show();
										}	
									}
								}).start();
							break;
						default:
							Snackbar.make(mCl, R.string.wrong, Snackbar.LENGTH_LONG)
								.setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
								.setAction(R.string.send_error, new View.OnClickListener(){

									@Override
									public void onClick(View p1)
									{startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(DebugActivity.this), item.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));}
								}).show();
							break;
					}
					return true;
				}
			});
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onResume ()
	{
		super.onResume();
		Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
	}
	public void SaveAndShare (View view) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException, NoSuchFieldException, InvocationTargetException, IOException
	{
		startActivity(Intent.createChooser(Others.getShot(this),getString(R.string.share_point)));
	}
}

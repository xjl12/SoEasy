package test.xjl12.soeasy;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import android.support.v7.widget.Toolbar;
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import android.net.*;
import android.view.inputmethod.*;
import java.io.*;

public class MainActivity extends AppCompatActivity
{
    public final static String EXTRA_MESSAGE ="test.xjl12.soeasy.MESSAGE";
	//public LinearLayout main_LinearLayout;
	//public int set_theme = -1;
	TextInputEditText input;
	CoordinatorLayout mCl;
	//LongTermActionService mService;
	//boolean mBound = false;

	/*
	 Handler mHandle = new Handler()
	 {

	 @Override
	 public void handleMessage(Message msg)
	 {
	 super.handleMessage(msg);
	 shot_thread.stop();
	 }
	 };*/
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		/** if (savedInstanceState != null)
		 {
		 if (savedInstanceState.getInt("theme") != -1)
		 {
		 setTheme(savedInstanceState.getInt("theme"));
		 }
		 } */
        setContentView(R.layout.main);

		final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.main_DrawerLayout);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.main_mdToolbar);
		final NavigationView mNavigation = (NavigationView) findViewById(R.id.main_NavigationView);
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
		mCl = (CoordinatorLayout) findViewById(R.id.main_mdCoordinatorLayout);
		input = (TextInputEditText) findViewById(R.id.main_m_input);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
			public void onDrawerOpened(View view)
			{
				super.onDrawerOpened(view);
				fab.hide();
			}
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				fab.show();
			}
		};
		mDrawerLayout.setDrawerListener(toggle);
		toggle.syncState();

		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (input.length() != 0)
					{
						Intent second_activity = new Intent(getApplicationContext(), SecondActivity.class);
						second_activity.putExtra(EXTRA_MESSAGE, input.getText().toString());
						startActivity(second_activity);
					}
					else
					{
						Toast.makeText(getApplicationContext(), R.string.second_Activity_text_null, Toast.LENGTH_SHORT).show();
					}
				}
			});

		toolbar.setNavigationOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(), mCl, input);
					final TextView mNavigation_header_version = (TextView) findViewById(R.id.navigation_header_m_version);
					mNavigation_header_version.setText(Others.getAppVersionName(getApplicationContext()));
					mDrawerLayout.openDrawer(GravityCompat.START);
				}
			});

		mCl.setOnTouchListener(new View.OnTouchListener(){

				@Override
				public boolean onTouch(View p1, MotionEvent p2)
				{
					Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(), p1, input);
					return false;
				}
			});

		mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

				@Override
				public boolean onNavigationItemSelected(final MenuItem p1)
				{
					mDrawerLayout.closeDrawer(GravityCompat.START);
					switch (p1.getItemId())
					{
						case R.id.navigation_item_games:
							Intent games_intent = new Intent(getApplicationContext(), GamesActivity.class);
							startActivity(games_intent);
							break;
						case R.id.navigation_item_urllist:
							Intent url_list_intent = new Intent(getApplicationContext(), URLListActivity.class);
							startActivity(url_list_intent);
							break;
						case R.id.navigation_item_test:
							Intent test_intent = new Intent(getApplicationContext(), TestActivity.class);
							startActivity(test_intent);
							break;
						case R.id.navigation_item_debug:
							Intent debug_intent = new Intent(getApplicationContext(), DebugActivity.class);
							debug_intent.putExtra(EXTRA_MESSAGE,Others.getAppVersionName(getApplicationContext()));
        					startActivity(debug_intent);
							break;
						case R.id.navigation_item_exit:
							Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
							finish();
							break;
						default:
							Snackbar.make(mCl, R.string.wrong, Snackbar.LENGTH_LONG)
								.setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
								.setAction(R.string.send_error, new View.OnClickListener(){

									@Override
									public void onClick(View view)
									{
										startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(MainActivity.this), p1.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));
									}
								}).show();
							break;
					}
					return true;
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
						case R.id.force_exit_item:
							Others.DeleteDirAllFile(getExternalCacheDir());
							android.os.Process.killProcess(android.os.Process.myPid());
							System.exit(0);
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
														Intent intent = Others.getShot(MainActivity.this);
														if (intent != null)
														{
															startActivity(intent);
														}
														else
														{
															Toast.makeText(MainActivity.this,getString(R.string.Error_cannot_shot),Toast.LENGTH_LONG);
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
									{startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(MainActivity.this), item.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));}
								}).show();
							break;
					}
					return true;
				}
			});
			Others.DeleteDirAllFile(getExternalFilesDir("Log"));
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(), mCl, input);
		Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
	}
	/*private ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName p1, IBinder p2)
		{
			LongTermActionService.LocalBinder binder = (LongTermActionService.LocalBinder) p2;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName p1)
		{
			mBound = false;
		}
	};*/
}

	/**@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putInt("theme",set_theme);
	} */
	/** public void button1(View view)
	{
		Intent intent = new Intent (this, SecondActivity.class);
		EditText editText = (EditText) findViewById(R.id.mainEditText);
        String edittext = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE,edittext);
        if (editText.length() == 0)
        {
            Toast.makeText(this,R.string.second_Activity_text_null,Toast.LENGTH_SHORT).show();
            //Context context = null;
            //Toast.makeText(context, R.string.second_Activity_text_null,l).show();

        }
        else
        {
            startActivity(intent);
        }
	} */

   /** public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            
            case R.id.exit_action:
				Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
                finish();
                return true;
            case R.id.app_theme_choose_holo:
                set_theme = android.R.style.Theme_Holo;
				this.recreate();
                return true;
            case R.id.app_theme_choose_default:
				set_theme = R.style.AppTheme;
				this.recreate();
				return true;
			
        }
            
         return super.onOptionsItemSelected(item);
    }*/

/* public class ArticleFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO: Implement this method
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
}
*/

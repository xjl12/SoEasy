package test.xjl12.soeasy;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import com.maddog05.maddogdialogs.MaddogProgressDialog;
import java.net.*;

public class MainActivity extends AppCompatActivity
{
    public final static String EXTRA_MESSAGE ="test.xjl12.soeasy.MESSAGE";
	//public LinearLayout main_LinearLayout;
	//public int set_theme = -1;
	EditText input;
	CoordinatorLayout mCl;
	DrawerLayout mDrawerLayout;
	MaddogProgressDialog mProgress;
	AlertDialog.Builder new_version;
	AlertDialog.Builder not_new_version;
	AlertDialog.Builder error_dialog;
	HttpURLConnection connection;
	MenuItem dev_sub_info;
	MenuItem dev_sub_test;
	AppCompatImageView nav_dev_imageView;
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
		/* if (savedInstanceState != null)
		 {
		 if (savedInstanceState.getInt("theme") != -1)
		 {
		 setTheme(savedInstanceState.getInt("theme"));
		 }
		 } */
        setContentView(R.layout.main);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.main_DrawerLayout);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.main_mdToolbar);
		final NavigationView mNavigation = (NavigationView) findViewById(R.id.main_NavigationView);
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
		mCl = (CoordinatorLayout) findViewById(R.id.main_mdCoordinatorLayout);
		input = (EditText) findViewById(R.id.main_m_input);
		//ImageView imageView = (ImageView) findViewById(R.id.main_content_ImageView1);
		//final TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.main_TextInputLayout);
		final Typeface light_typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		//final Typeface medium_italic = Typeface.createFromAsset(getAssets(),"fonts/Roboto-MediumItalic.ttf");

		Others.initActivity(this,toolbar,mCl);

		//mNavigation.setItemIconTintList(null);
		//input.setText(R.string.happy_new_year);
		View header = mNavigation.getHeaderView(0);
		TextView mNavigation_header_version = (TextView) header.findViewById(R.id.navigation_header_app_version);
		TextView app_name = (TextView) header.findViewById(R.id.navigation_header_m_app_name);
		mNavigation_header_version.setText(Others.getAppVersionName(getApplicationContext()));
		app_name.setTypeface(light_typeface);
		Menu navigation_menu = mNavigation.getMenu();
		dev_sub_info = navigation_menu.findItem(R.id.main_develop_sub_info);
		dev_sub_test = navigation_menu.findItem(R.id.main_develop_sub_test);
		nav_dev_imageView = (AppCompatImageView) navigation_menu.findItem(R.id.navigation_item_develop).getActionView().findViewById(R.id.navigation_dev_imgview);

		nav_dev_imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				changeNavigationDevSubState();
			}
		});
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
		//mDrawerLayout.setDrawerListener(toggle);
		toggle.syncState();

		 //Dialog init
        /*
		mProgress = new MaddogProgressDialog(this);
		mProgress.setCancelable(false);
		mProgress.setTitle(R.string.running);
		mProgress.setMessage(R.string.updating_message);
		
		new_version = new AlertDialog.Builder(this);
		new_version.setTitle(R.string.find_new_version_title);
		new_version.setIcon(R.drawable.ic_check_black);
		new_version.setMessage(R.string.find_new_version);
		new_version.setPositiveButton();
		*/
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
					switch (p1.getItemId())
					{
						case R.id.navigation_item_caishu_games:
							Intent games_intent = new Intent(getApplicationContext(), GamesActivity.class);
							startActivity(games_intent);
							break;
						case R.id.navigation_item_my_world:
							startActivity(new Intent(getApplicationContext(), MyWorldActivity.class));
							break;
						case R.id.navigation_item_urllist:
							Intent url_list_intent = new Intent(getApplicationContext(), URLListActivity.class);
							startActivity(url_list_intent);
							break;
						case R.id.navigation_item_develop:
							changeNavigationDevSubState();
							break;
						case R.id.main_develop_sub_test:
							Intent test_intent = new Intent(getApplicationContext(), TestActivity.class);
							startActivity(test_intent);
							break;
						case R.id.main_develop_sub_info:
							Intent debug_intent = new Intent(getApplicationContext(), DebugActivity.class);
							debug_intent.putExtra(EXTRA_MESSAGE, Others.getAppVersionName(getApplicationContext()));
							startActivity(debug_intent);
							break;
                        case R.id.navigation_item_math:
                            Intent math_intent = new Intent(getApplicationContext(),MathActivity.class);
                            startActivity(math_intent);
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


		//Others.DeleteDirAllFile(getExternalFilesDir("Log"));
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
		//Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
	}

	@Override
    public void onBackPressed() 
	{
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) 
		{
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
		else 
		{
            super.onBackPressed();
        }
    }

	@Override
	protected void onStop()
	{
		if (dev_sub_info.isVisible() || dev_sub_test.isVisible()) {
			changeNavigationDevSubState();
		}
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) 
		{
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
		super.onStop();
	}

	private void changeNavigationDevSubState() {
		if (dev_sub_info.isVisible() || dev_sub_test.isVisible()) {
			dev_sub_info.setVisible(false);
			dev_sub_test.setVisible(false);
			nav_dev_imageView.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
		}
		else {
			dev_sub_info.setVisible(true);
			dev_sub_test.setVisible(true);
			nav_dev_imageView.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
		}
	}
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

	/*@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putInt("theme",set_theme);
	} */
	/* public void button1(View view)
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

   /* public boolean onOptionsItemSelected(MenuItem item)
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


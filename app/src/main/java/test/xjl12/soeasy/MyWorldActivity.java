package test.xjl12.soeasy;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import org.apache.http.impl.client.*;
import java.io.*;

public class MyWorldActivity extends AppCompatActivity
{
	boolean running = false;
	CoordinatorLayout mCl;
	TextView soeasy_has_version_show;
	TextView phone_has_version;
	TextView phone_has_version_show;
	Button unzip_button;
	Button revoke_button;
	Button delete_button;
	Button send_button;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_world);

		soeasy_has_version_show = (TextView) findViewById(R.id.my_world_content_soeaay_version_show_TextView);
		phone_has_version_show = (TextView) findViewById(R.id.my_world_content_phone_version_show_TextView);
		phone_has_version = (TextView) findViewById(R.id.my_world_content_phone_has_version_TextView);
		unzip_button = (Button) findViewById(R.id.my_world_content_unzip_Button);
		send_button = (Button) findViewById(R.id.my_world_content_send_Button);
		revoke_button = (Button) findViewById(R.id.my_world_content_revoke_Button);
		delete_button = (Button) findViewById(R.id.my_world_content_delete_Button);
		mCl = (CoordinatorLayout) findViewById(R.id.my_world_mdCoordinatorLayout);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.my_world_mdToolbar);

		Others.initActivity(this,mToolbar);
		
		unzip_button.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					running = true;
					finishProgress();
				}
			});

		revoke_button.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					running = true;
					finishProgress();
				}
			});
			
		mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){

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
														Intent intent = Others.getShot(MyWorldActivity.this);
														if (intent != null)
														{
															startActivity(intent);
														}
														else
														{
															Toast.makeText(MyWorldActivity.this, getString(R.string.Error_cannot_shot), Toast.LENGTH_LONG);
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
									{startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(MyWorldActivity.this), item.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));}
								}).show();
							break;
					}
					return true;
				}
			});
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		updateView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		if (running)
		{
			Snackbar.make(mCl, getString(R.string.warn_not_exit), Snackbar.LENGTH_LONG).show();
		}
		else
		{
			finish();
		}
	}
	private void updateView()
	{
		File my_world_file = new File (Environment.getExternalStorageDirectory().getPath() + "/games/com.mojang/minecraftWorlds/MyWorld_xjl_lyy");
		if (my_world_file.exists())
		{
			phone_has_version_show.setVisibility(View.VISIBLE);
			phone_has_version.setVisibility(View.VISIBLE);
			send_button.setVisibility(View.VISIBLE);
			delete_button.setVisibility(View.VISIBLE);
			
			String[] my_world_file_list = my_world_file.list();
			String phone_has_my_world_version = null;
			for (int t = 0;t <= my_world_file_list.length;t++)
			{
				phone_has_my_world_version = my_world_file_list[t];
			}
			if (phone_has_my_world_version != null)
			{
				phone_has_version_show.setText(phone_has_my_world_version);
			}
			else
			{
				phone_has_version_show.setText(R.string.my_world_hurt);
			}
		}
	}
	private void finishProgress()
	{
		running = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.finish_process);
		builder.setMessage(R.string.finish_if_start_myworld);
		builder.setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.dismiss();
				}
			});
		builder.setPositiveButton(R.string.good, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					boolean checkInstall = Others.isAppInstalled(getApplicationContext(), getString(R.string.my_world_name));
					boolean checkVersion = Others.CheckMyWorldVersion(getApplicationContext());
					if (checkInstall && checkVersion)
					{
						Toast.makeText(getApplicationContext(),R.string.my_world_starting,Toast.LENGTH_LONG).show();
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									Intent start_my_world = getPackageManager().getLaunchIntentForPackage(getString(R.string.my_world_name));
									start_my_world.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									try
									{
										Thread.sleep(1000L);
									}
									catch (InterruptedException e)
									{}
									startActivity(start_my_world);
								}
						}).start();
					}
					else if (checkInstall)
					{
						Snackbar.make(mCl, getString(R.string.my_world_not_version), Snackbar.LENGTH_LONG)
							.show();
					}
					else
					{
						Snackbar.make(mCl, getString(R.string.my_world_not_installed), Snackbar.LENGTH_LONG)
							.show();
					}
					p1.dismiss();
				}
			});
		builder.setOnDismissListener(new DialogInterface.OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface p1)
				{
					updateView();
				}
			});
		builder.show();
	}
}




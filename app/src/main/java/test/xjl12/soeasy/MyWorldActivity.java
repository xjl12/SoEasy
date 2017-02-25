package test.xjl12.soeasy;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import com.maddog05.maddogdialogs.*;
import android.widget.AutoCompleteTextView.*;

public class MyWorldActivity extends AppCompatActivity
{
	boolean is_storage_write_able;
	int phone_version = -1;
	int soeasy_version = -1;
	int backup_version = -1;
	File my_world_archive_dir;
	File backup_file;
	private static String archive_name = "MyWorld_xjl_lyy";
	private static String help_show_key = "show_help_dialog";
	String error_info = null;
	CoordinatorLayout mCl;
	TextView soeasy_has_version_show;
	TextView phone_has_version;
	TextView phone_has_version_show;
	TextView backup_has_version;
	TextView backup_has_version_show;
	Button unzip_button;
	Button revoke_button;
	Button delete_button;
	Button send_button;
	Button delete_backup_button;
	MaddogProgressDialog mProgressDialog;
	AlertDialog.Builder success_builder;
	AlertDialog.Builder error_can_not_write;
	AlertDialog.Builder error_builder;
	AlertDialog.Builder error_phone_has_version_bigger;
	AlertDialog.Builder help_builder;
	SharedPreferences help_sp;
	SharedPreferences.Editor help_edit;
	
	private static Handler mHandle = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_world);

		soeasy_has_version_show = (TextView) findViewById(R.id.my_world_content_soeaay_version_show_TextView);
		phone_has_version_show = (TextView) findViewById(R.id.my_world_content_phone_version_show_TextView);
		phone_has_version = (TextView) findViewById(R.id.my_world_content_phone_has_version_TextView);
		backup_has_version = (TextView) findViewById(R.id.my_world_content_backup_version_TextView);
		backup_has_version_show = (TextView) findViewById(R.id.my_world_content_backup_version_shoe_TextView);
		unzip_button = (Button) findViewById(R.id.my_world_content_unzip_Button);
		send_button = (Button) findViewById(R.id.my_world_content_send_Button);
		revoke_button = (Button) findViewById(R.id.my_world_content_revoke_Button);
		delete_button = (Button) findViewById(R.id.my_world_content_delete_Button);
		delete_backup_button = (Button) findViewById(R.id.my_world_content_delete_backup_Button);
		mCl = (CoordinatorLayout) findViewById(R.id.my_world_mdCoordinatorLayout);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.my_world_mdToolbar);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.my_world_fab);

		Others.initActivity(this, mToolbar);

		help_sp = getSharedPreferences(Others.getRunningActivityName(this),MODE_APPEND);
		help_edit = help_sp.edit();
		
		my_world_archive_dir = new File(Environment.getExternalStorageDirectory().getPath() + "/games/com.mojang/minecraftWorlds/" + archive_name);
		backup_file = new File(getExternalFilesDir("Backup"), archive_name);

		mProgressDialog = new MaddogProgressDialog(this);
		mProgressDialog.setTitle(R.string.running);
		mProgressDialog.setMessage(getString(R.string.running_mesage));
		mProgressDialog.setCancelable(false);

		//初始化对话框
		
		help_builder  = new AlertDialog.Builder(this);
		help_builder.setTitle(R.string.help);
		help_builder.setMessage(R.string.my_world_help);
		help_builder.setIcon(R.drawable.ic_lightbulb_outline);
		help_builder.setPositiveButton(R.string.ok_I_know, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					help_edit.putBoolean(help_show_key,false);
					help_edit.commit();
				}
			});
			
		if (help_sp.getBoolean(help_show_key,true))
		{
			help_builder.show();
		}
		
		error_can_not_write = new AlertDialog.Builder(this);
		error_can_not_write.setTitle(R.string.error);
		error_can_not_write.setMessage(R.string.sdcard_error);
		error_can_not_write.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					is_storage_write_able = Others.checkIsStorageWritable(getApplicationContext());
					p1.dismiss();
				}
			});
		error_can_not_write.setNeutralButton(R.string.feebback, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Others.feebbackErrorInfo(error_info,MyWorldActivity.this,mHandle);
					p1.dismiss();
				}
			});
		error_can_not_write.setOnDismissListener(errorDialogDismiss());
		error_can_not_write.setIcon(R.drawable.ic_warning);
		
		error_phone_has_version_bigger = new AlertDialog.Builder(MyWorldActivity.this);
		error_phone_has_version_bigger.setTitle(R.string.warn);
		error_phone_has_version_bigger.setMessage(R.string.my_world_phone_version_bigger);
		error_phone_has_version_bigger.setPositiveButton(R.string.continue_str, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					unzipArchive();
					p1.dismiss();
				}
			});
		error_phone_has_version_bigger.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.dismiss();
				}
			});
		error_phone_has_version_bigger.setIcon(R.drawable.ic_warning);

		error_builder = new AlertDialog.Builder(this);
		error_builder.setTitle(R.string.error);
		error_builder.setMessage(R.string.error_message);
		error_builder.setPositiveButton(R.string.good, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.dismiss();
				}

			});
		error_builder.setNeutralButton(R.string.feebback, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Others.feebbackErrorInfo(error_info,MyWorldActivity.this,mHandle);
				}
			});
		error_builder.setOnDismissListener(errorDialogDismiss());
		error_builder.setIcon(R.drawable.ic_error);
		
		success_builder = new AlertDialog.Builder(this);
		success_builder.setTitle(R.string.finish_process);
		success_builder.setMessage(R.string.finish_if_start_myworld);
		success_builder.setIcon(R.drawable.ic_check_black);
		success_builder.setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.dismiss();
				}
			});
		success_builder.setPositiveButton(R.string.good, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					boolean checkInstall = Others.isAppInstalled(getApplicationContext(), getString(R.string.my_world_name));
					boolean checkVersion = Others.CheckMyWorldVersion(getApplicationContext());
					if (checkInstall && checkVersion)
					{
						Toast.makeText(getApplicationContext(), R.string.my_world_starting, Toast.LENGTH_LONG).show();
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
		success_builder.setOnDismissListener(new DialogInterface.OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface p1)
				{
					try
					{
						updateView();
					}
					catch (IOException e)
					{errorDialog(e.toString());}
				}
			});


		//初始化各个按钮
		
		unzip_button.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (phone_version > soeasy_version)
					{
						error_phone_has_version_bigger.show();
					}
					else if (is_storage_write_able)
					{
						mProgressDialog.show();
						unzipArchive();
					}
					else
					{
						errorCanNotWrite(Others.checkStorageWritableWithInfo(MyWorldActivity.this));
					}
				}
			});
		send_button.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (is_storage_write_able)
					{
						mProgressDialog.show();
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									File target_zip = new File(getExternalCacheDir(), Integer.toString(phone_version) + "-" + archive_name +  ".zip");
									try
									{
										Others.zipCompression(my_world_archive_dir, target_zip);
										sendArchivePackage(target_zip);
									}
									catch (final IOException e)
									{
										mHandle.post(new Runnable(){

												@Override
												public void run()
												{
													errorDialog(e.toString());
												}
											});
									}
								}
							}).start();
					}
					else
					{
						errorCanNotWrite(Others.checkStorageWritableWithInfo(MyWorldActivity.this));
					}
				}
			});
		delete_button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (is_storage_write_able)
					{
						mProgressDialog.show();
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									if (Others.DeleteDirAllFile(my_world_archive_dir))
									{
										finishProgressAtThread();
									}
									else
									{
										mHandle.post(new Runnable(){

												@Override
												public void run()
												{
													errorDialog(null);
												}
											});
									}
								}
							}).start();
					}
					else
					{
						errorCanNotWrite(Others.checkStorageWritableWithInfo(MyWorldActivity.this));
					}
				}
			});
		revoke_button.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (is_storage_write_able)
					{
						mProgressDialog.show();
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									if (my_world_archive_dir.exists())
									{
										Others.DeleteDirAllFile(my_world_archive_dir);
									}
									else
									{
										my_world_archive_dir.mkdirs();
									}
									try
									{
										Others.fileCopy(backup_file, my_world_archive_dir);
										finishProgressAtThread();
									}
									catch (final IOException e)
									{
										mHandle.post(new Runnable(){

												@Override
												public void run()
												{
													errorDialog(e.toString());
												}
											});
									}
								}
							}).start();
					}
					else
					{
						errorCanNotWrite(Others.checkStorageWritableWithInfo(MyWorldActivity.this));
					}
				}
			});

		delete_backup_button.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (is_storage_write_able)
					{
						mProgressDialog.show();
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									if (Others.DeleteDirAllFile(backup_file))
									{
										finishProgressAtThread();
									}
									else
									{
										mHandle.post(new Runnable(){

												@Override
												public void run()
												{
													errorDialog(null);
												}
											});
									}
								}
							}).start();
					}
					else
					{
						errorCanNotWrite(Others.checkStorageWritableWithInfo(MyWorldActivity.this));
					}
				}
			});

		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					help_builder.show();
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
	protected void onResume()
	{
		super.onResume();
		try
		{
			updateView();
		}
		catch (IOException e)
		{
			errorDialog(e.toString());
		}
		is_storage_write_able = Others.checkIsStorageWritable(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
	//解压与更新
	private void unzipArchive()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						if (phone_version != -1)
						{
							Others.fileCopy(my_world_archive_dir, backup_file);
							Others.DeleteDirAllFile(my_world_archive_dir);
						}
						if (!my_world_archive_dir.exists())
						{
							my_world_archive_dir.mkdirs();
						}
						Others.assetsCopy(getAssets(), "MyWorld_xjl_lyy", my_world_archive_dir);
						finishProgressAtThread();
					}
					catch (final IOException e)
					{
						mHandle.post(new Runnable(){

								@Override
								public void run()
								{
									errorDialog(e.toString());
								}
							});
					}
				}
			}).start();
	}
	private void finishProgressAtThread()
	{
		mHandle.post(new Runnable(){

				@Override
				public void run()
				{
					mProgressDialog.dismiss();
					success_builder.show();
				}
			});
	}
	private void sendArchivePackage(final File zipPackage)
	{
		mHandle.post(new Runnable(){

				@Override
				public void run()
				{
					mProgressDialog.dismiss();
					try
					{
						updateView();
					}
					catch (IOException e)
					{
						errorDialog(e.toString());
					}
					Intent send = new Intent();
					send.setAction(Intent.ACTION_SEND);
					send.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zipPackage));
					send.setType("*/*");
					Intent send_is_qq = Others.isQQInstalled(getApplicationContext(), send);
					startActivity(Intent.createChooser(send_is_qq, getString(R.string.share_point)));
				}
			});
	}
	private void updateView() throws IOException
	{
		//读取SoEasy自带版本
		InputStream soeasy_is = getAssets().open("MyWorld_xjl_lyy/db/CURRENT");
		int soeasy_has_my_world_version = readVersion(soeasy_is);
		if (soeasy_has_my_world_version != -1)
		{
			soeasy_version = soeasy_has_my_world_version;
			soeasy_has_version_show.setText(Integer.toString(soeasy_has_my_world_version));
		}
		//读取手机内存档版本
		File my_world_file = new File(Environment.getExternalStorageDirectory().getPath() + "/games/com.mojang/minecraftWorlds/MyWorld_xjl_lyy/db/CURRENT");
		if (my_world_file.exists())
		{
			phone_has_version_show.setVisibility(View.VISIBLE);
			phone_has_version.setVisibility(View.VISIBLE);
			send_button.setVisibility(View.VISIBLE);
			delete_button.setVisibility(View.VISIBLE);
			unzip_button.setText(R.string.my_world_update);

			InputStream phone_is = new FileInputStream(my_world_file);
			int phone_has_my_world_version = readVersion(phone_is);
			if (phone_has_my_world_version != -1)
			{
				phone_version = phone_has_my_world_version;
				phone_has_version_show.setText(Integer.toString(phone_has_my_world_version));
			}
		}
		else
		{
			phone_version = -1;
			phone_has_version.setVisibility(View.GONE);
			phone_has_version_show.setVisibility(View.GONE);
			send_button.setVisibility(View.GONE);
			delete_button.setVisibility(View.GONE);
			unzip_button.setText(R.string.my_world_unzip_to_phone);
		}
		//读取备份区版本
		File backup_core_file = new File(backup_file,"db/CURRENT");
		if (backup_core_file.exists())
		{
			revoke_button.setVisibility(View.VISIBLE);
			delete_backup_button.setVisibility(View.VISIBLE);
			backup_has_version.setVisibility(View.VISIBLE);
			backup_has_version_show.setVisibility(View.VISIBLE);

			backup_version = readVersion(new FileInputStream(backup_core_file));
			if (backup_version != -1)
			{
				backup_has_version_show.setText(Integer.toString(backup_version));
			}
		}
		else
		{
			revoke_button.setVisibility(View.GONE);
			delete_backup_button.setVisibility(View.GONE);
			backup_has_version.setVisibility(View.GONE);
			backup_has_version_show.setVisibility(View.GONE);
			backup_version = -1;
		}
	}
	private int readVersion(InputStream is) throws IOException
	{
		StringBuffer cat_CURRENT = null;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		cat_CURRENT = new StringBuffer(br.readLine());
		is.close();
		if (cat_CURRENT != null)
		{
			cat_CURRENT.delete(0, 9);
			return Integer.parseInt(cat_CURRENT.toString());
		}
		return -1;
	}
	
	private void errorCanNotWrite(String error)
	{
		if (error != null)
		{
			error_info = error;
			error_can_not_write.setMessage(getString(R.string.sdcard_error_with_info, error));
		}
		error_can_not_write.show();
	}
	private void errorDialog(String error)
	{
		mProgressDialog.dismiss();
		if (error != null)
		{
			error_info = error;
			error_builder.setMessage(getString(R.string.error_message_with_infomation, error));
		}
		error_builder.show();
	}
	private DialogInterface.OnDismissListener errorDialogDismiss()
	{
		return new DialogInterface.OnDismissListener(){

			@Override
			public void onDismiss(DialogInterface p1)
			{
				error_info = null;
			}
		};
	}
}




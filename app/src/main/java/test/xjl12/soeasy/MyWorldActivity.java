package test.xjl12.soeasy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import com.maddog05.maddogdialogs.*;

public class MyWorldActivity extends AppCompatActivity
{
	private boolean is_storage_write_able;
	private int phone_version,internet_version,backup_version,downloaded_version = -1;
	private File my_world_archive_dir,backup_file;
    public static File downloaded_archive;
	private static final String archive_name = "MyWorld_xjl_lyy";
	private static final String help_show_key = "show_help_dialog";
	private static final String downloaded_version_key = "downloaded_archive_version";
	private String get_internet_version_result = null;
	private CoordinatorLayout mCl;
	private TextView internet_version_show,phone_has_version,phone_has_version_show,backup_has_version,backup_has_version_show;
	private Button download_button,revoke_button,delete_button,send_button,delete_backup_button,retry_button;
	private MaddogProgressDialog mProgressDialog;
	private AlertDialog.Builder success_builder,error_phone_has_version_bigger,help_builder;
	private ProgressDialog download_dialog;
	private SharedPreferences help_sp;
	private SharedPreferences.Editor help_edit;
	private Context context = this;
	private String check_internet_version_url,download_url;
    private Toolbar mToolbar;
	private boolean is_again = false;
	
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Others.DOWNLOAD_PROGRESS:
					if (msg.arg1 == 1) {
						download_dialog.setIndeterminate(false);
						download_dialog.setMax(100);
					}
					download_dialog.setProgress(msg.arg1);
					break;
				case Others.DOWNLOAD_SUCCESS:
					downloadSuccess();
					break;
				case Others.DOWNLOAD_FAILED:
					download_dialog.dismiss();
					Others.errorDialog((String) msg.obj, context, mHandle, true, false, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
							downloadArchive();
						}
					});
					break;
				case Others.GET_NETWORK_STRING_SUCCESS:
					get_internet_version_result = (String) msg.obj;
					internet_version_show.setText(get_internet_version_result);
					download_button.setVisibility(View.VISIBLE);
					break;
				case Others.GET_NETWORK_STRING_FAILED:
					if (is_again) {
						get_internet_version_result = getString(R.string.network_error);
						internet_version_show.setText(get_internet_version_result);
						Others.errorDialog((String) msg.obj, context, mHandle, true, false, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
								Others.getStringFromInternet(getString(R.string.my_world_web_version_url), mHandle);
							}
						});
						retry_button.setVisibility(View.VISIBLE);
					}
					else {
						download_url = getString(R.string.my_world_archive_url_2);
						check_internet_version_url = getString(R.string.my_world_web_version_url_2);
						Others.getStringFromInternet(check_internet_version_url,mHandle);
					}
					break;
				case Others.TASK_FAILED:
					mProgressDialog.dismiss();
					Others.errorDialog((String) msg.obj,context,mHandle,false,false,null);
					break;
			}
		}
	};

	private final int MY_REQUEST_CODE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_world);

		internet_version_show = (TextView) findViewById(R.id.my_world_content_soeaay_version_show_TextView);
		phone_has_version_show = (TextView) findViewById(R.id.my_world_content_phone_version_show_TextView);
		phone_has_version = (TextView) findViewById(R.id.my_world_content_phone_has_version_TextView);
		backup_has_version = (TextView) findViewById(R.id.my_world_content_backup_version_TextView);
		backup_has_version_show = (TextView) findViewById(R.id.my_world_content_backup_version_shoe_TextView);
		download_button = (Button) findViewById(R.id.my_world_content_download_Button);
		send_button = (Button) findViewById(R.id.my_world_content_send_Button);
		revoke_button = (Button) findViewById(R.id.my_world_content_revoke_Button);
		delete_button = (Button) findViewById(R.id.my_world_content_delete_Button);
		delete_backup_button = (Button) findViewById(R.id.my_world_content_delete_backup_Button);
		retry_button = (Button) findViewById(R.id.my_world_content_retry_button);
		mCl = (CoordinatorLayout) findViewById(R.id.my_world_mdCoordinatorLayout);
        mToolbar = (Toolbar) findViewById(R.id.my_world_mdToolbar);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.my_world_fab);

		Others.initActivity(this, mToolbar,mCl);

		help_sp = getSharedPreferences(Others.getRunningActivityName(context),MODE_APPEND);
		help_edit = help_sp.edit();
		
		my_world_archive_dir = new File(Environment.getExternalStorageDirectory().getPath() + "/games/com.mojang/minecraftWorlds/" + archive_name);
		backup_file = new File(getExternalFilesDir("Backup"), archive_name);
		downloaded_archive = new File(getExternalFilesDir("Download"),archive_name + ".zip");

		mProgressDialog = new MaddogProgressDialog(context);
		mProgressDialog.setTitle(R.string.running);
		mProgressDialog.setMessage(getString(R.string.running_mesage));
		mProgressDialog.setCancelable(false);

		//初始化对话框
		
		help_builder  = new AlertDialog.Builder(context);
		help_builder.setTitle(R.string.help);
		help_builder.setMessage(R.string.my_world_help);
		help_builder.setIcon(R.drawable.ic_lightbulb_outline);
		help_builder.setPositiveButton(R.string.ok_I_know, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					help_edit.putBoolean(help_show_key,false);
					help_edit.apply();
				}
			});
			
		if (help_sp.getBoolean(help_show_key,true))
		{
			help_builder.show();
		}
		downloaded_version = help_sp.getInt(downloaded_version_key,-1);

		error_phone_has_version_bigger = new AlertDialog.Builder(context);
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

		success_builder = new AlertDialog.Builder(context);
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
					updateView();
				}
			});

		download_dialog = new ProgressDialog(context);
		download_dialog.setIndeterminate(true);
		download_dialog.setTitle(R.string.downloading);
		download_dialog.setIcon(R.drawable.ic_file_download_black_24dp);
		download_dialog.setCancelable(true);
		download_dialog.setCanceledOnTouchOutside(true);
		download_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		download_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				Others.downloadStop();
				Toast.makeText(getApplicationContext(),R.string.download_canceled,Toast.LENGTH_SHORT).show();
			}
		});

		//初始化各个按钮
		
		download_button.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (phone_version > internet_version)
					{
						error_phone_has_version_bigger.show();
					}
					else if (is_storage_write_able)
					{
						downloadArchive();
					}
					else
					{
						Others.errorDialog(Others.checkStorageWritableWithInfo(context),context,mHandle,false,true,null);
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
										Message msg = Message.obtain();
										msg.what = Others.TASK_FAILED;
										msg.obj = e.toString();
										mHandle.sendMessage(msg);
									}
								}
							}).start();
					}
					else
					{
						Others.errorDialog(Others.checkStorageWritableWithInfo(context),context,mHandle,false,true,null);
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
										Message msg = Message.obtain();
										msg.what = Others.TASK_FAILED;
										msg.obj = null;
										mHandle.sendMessage(msg);
									}
								}
							}).start();
					}
					else
					{
						Others.errorDialog(Others.checkStorageWritableWithInfo(context),context,mHandle,false,true,null);
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
										Message msg = Message.obtain();
										msg.what = Others.TASK_FAILED;
										msg.obj = e.toString();
										mHandle.sendMessage(msg);
									}
								}
							}).start();
					}
					else
					{
						Others.errorDialog(Others.checkStorageWritableWithInfo(context),context,mHandle,false,true,null);
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
									else {
										Message msg = Message.obtain();
										msg.what = Others.TASK_FAILED;
										msg.obj = null;
										mHandle.sendMessage(msg);
									}
								}
							}).start();
					}
					else
					{
						Others.errorDialog(Others.checkStorageWritableWithInfo(context),context,mHandle,false,true,null);
					}
				}
			});
		retry_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				retry_button.setVisibility(View.GONE);
				internet_version_show.setText(R.string.reading);
				Others.getStringFromInternet(getString(R.string.my_world_web_version_url),mHandle);
			}
		});

		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					help_builder.show();
				}
			});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		updateView();
		is_storage_write_able = Others.checkIsStorageWritable(context);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_REQUEST_CODE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
        getMenuInflater().inflate(R.menu.activity_actions, menu);
		if (downloaded_archive.exists()) {
            menu.findItem(R.id.my_world_delete_downloaded).setVisible(true);
		}
		return super.onCreateOptionsMenu(menu);
    }

	@Override
	public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
		switch (requestCode) {
			case MY_REQUEST_CODE:
				if (grantResults.length == 0) {
					is_storage_write_able = false;
				}
		}
	}
	//下载存档
	private void downloadArchive() {
		if (downloaded_archive.exists() && downloaded_version == internet_version) {
			unzipArchive();
		}
		else {
			download_dialog.show();
			download_dialog.onStart();
			download_dialog.setProgress(0);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Others.downloadFileFromHttp(download_url, downloaded_archive, mHandle);
				}
			}).start();
		}
	}
	private void downloadSuccess() {
		help_edit.putInt(downloaded_version_key,internet_version);
		help_edit.apply();
		download_dialog.dismiss();
        mToolbar.getMenu().findItem(R.id.my_world_delete_downloaded).setVisible(true);
		unzipArchive();
	}
	//解压与更新
	private void unzipArchive()
	{
		mProgressDialog.show();
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
						Others.uncompressZip(downloaded_archive,my_world_archive_dir.getParentFile());
						finishProgressAtThread();
					}
					catch (final IOException e)
					{
						Message msg = Message.obtain();
						msg.what = Others.TASK_FAILED;
						msg.obj = e.toString();
						mHandle.sendMessage(msg);
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
					updateView();
					Intent send = new Intent();
					send.setAction(Intent.ACTION_SEND);
					send.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zipPackage));
					send.setType("*/*");
					Intent send_is_qq = Others.isQQInstalled(getApplicationContext(), send);
					startActivity(Intent.createChooser(send_is_qq, getString(R.string.share_point)));
				}
			});
	}
	private void updateView()
	{
		///读取网络版本
		Others.getStringFromInternet(check_internet_version_url,mHandle);
		try {
			//读取手机内存档版本

			File my_world_file = new File(Environment.getExternalStorageDirectory().getPath() + "/games/com.mojang/minecraftWorlds/MyWorld_xjl_lyy/db/CURRENT");
			if (my_world_file.exists()) {
				phone_has_version_show.setVisibility(View.VISIBLE);
				phone_has_version.setVisibility(View.VISIBLE);
				send_button.setVisibility(View.VISIBLE);
				delete_button.setVisibility(View.VISIBLE);
				download_button.setText(R.string.my_world_update);

				InputStream phone_is = new FileInputStream(my_world_file);
				int phone_has_my_world_version = readVersion(phone_is);
				if (phone_has_my_world_version != -1) {
					phone_version = phone_has_my_world_version;
					phone_has_version_show.setText(Integer.toString(phone_has_my_world_version));
				}
			} else {
				phone_version = -1;
				phone_has_version.setVisibility(View.GONE);
				phone_has_version_show.setVisibility(View.GONE);
				send_button.setVisibility(View.GONE);
				delete_button.setVisibility(View.GONE);
				download_button.setText(R.string.my_world_download_to_phone);
			}
			//读取备份区版本
			File backup_core_file = new File(backup_file, "db/CURRENT");
			if (backup_core_file.exists()) {
				revoke_button.setVisibility(View.VISIBLE);
				delete_backup_button.setVisibility(View.VISIBLE);
				backup_has_version.setVisibility(View.VISIBLE);
				backup_has_version_show.setVisibility(View.VISIBLE);

				backup_version = readVersion(new FileInputStream(backup_core_file));
				if (backup_version != -1) {
					backup_has_version_show.setText(Integer.toString(backup_version));
				}
			} else {
				revoke_button.setVisibility(View.GONE);
				delete_backup_button.setVisibility(View.GONE);
				backup_has_version.setVisibility(View.GONE);
				backup_has_version_show.setVisibility(View.GONE);
				backup_version = -1;
			}
		}
		catch (IOException e) {
			Others.errorDialog(e.toString(), context, mHandle, false, true, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
					updateView();
				}
			});
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
}




package test.xjl12.soeasy;

import android.content.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;

import android.support.v7.widget.Toolbar;

public class TestActivity extends AppCompatActivity
{
    TextView test_textview1;
	Button fc_button;
	private Handler handler = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        //Test Fragment
        /*if (findViewById(R.id.testFrameLayout) != null)
        {
            if (savedInstanceState != null)
            {
                return;
            }
            Fragment first = new Fragment();
            first.setArguments(getIntent().getExtras());
            
            getFragmentManager().beginTransaction().add(R.id.testFrameLayout,first).commit();
        }*/
		fc_button = (Button) findViewById(R.id.test_fc_Button);
        test_textview1 = (TextView) findViewById(R.id.testTextView1);
		Toolbar toolbar = (Toolbar) findViewById(R.id.test_mdToolbar);
		final CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.test_mdCoordinatorLayout);
		
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
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
														Intent intent = Others.getShot(TestActivity.this);
														if (intent != null)
														{
															startActivity(intent);
														}
														else
														{
															Toast.makeText(TestActivity.this,getString(R.string.Error_cannot_shot),Toast.LENGTH_LONG);
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
									{startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(TestActivity.this), item.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));}
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
    
    public void test1 (View view)
    {
        Intent web = new Intent();
        web.setAction("android.intent.action.VIEW");
        Uri baidu = Uri.parse("https:www.baidu.com");
        web.setData(baidu);
        startActivity(web);
    }
    public void TestFileWrite (View view) throws IOException
    {
        Log.i("Test","TestFileWrite button on click");
        FileOutputStream outstream = this.openFileOutput("test.txt",Context.MODE_APPEND);
        outstream.write("测试成功！".getBytes());
        outstream.close();
    }
    public void TestFileRead (View view) throws IOException
    {
        Log.i("Test","TestFileRead button on click");
		try
		{
			InputStream is = this.openFileInput("test.txt");
			test_textview1.setText(Others.getString(is));
			is.close();
		}
		catch (FileNotFoundException e)
		{
			Toast.makeText(this,R.string.Error_no_file,Toast.LENGTH_LONG).show();
		}
    }
	public void DeleteTestFile (View view)
	{
		Others.DeleteDirAllFile(getApplicationContext().getFilesDir());
		test_textview1.setText(R.string.delete_complete);
	}
	public void TestFC (View view)
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					TextView test_fc = null;
					test_fc.setText(null);
				}
			}).start();
	}
	public void OpenRawTestImage (View view)
	{
		if (getExternalCacheDir() != null) {
			final File cache_file = new File(getExternalCacheDir(), Others.RandomString(8));
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beautiful_background);
					try {
						FileOutputStream test_fos = new FileOutputStream(cache_file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, test_fos);
						test_fos.flush();
						test_fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							Uri test_image = Uri.fromFile(cache_file);
							Intent open_test_image = new Intent(Intent.ACTION_VIEW);
							open_test_image.setDataAndType(test_image, "image/*");
							startActivity(open_test_image);
						}
					});
				}
			}).start();
		}
		else {
			Others.errorDialogBuilder(getString(R.string.debug_message_error_storage_unusable),this,handler).show();
		}
	}
}

package test.xjl12.soeasy;

import android.content.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.content.FileProvider;
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
		CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.test_mdCoordinatorLayout);
		Others.initActivity(this,toolbar,mCl);
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
        web.setAction(Intent.ACTION_VIEW);
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
	public void OpenRawTestImage (final View view)
	{
		if (getExternalCacheDir() != null) {
			final File cache_file = new File(getExternalCacheDir(), Others.RandomString(8) + ".png");
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
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
								open_test_image.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
								test_image = FileProvider.getUriForFile(TestActivity.this,getString(R.string.fileprovider_authorities),cache_file);
								open_test_image.setDataAndType(test_image,"image/*");
							} else {
								open_test_image.setDataAndType(test_image, "image/*");
							}
							startActivity(open_test_image);
						}
					});
				}
			}).start();
		}
		else {
			Others.errorDialog(getString(R.string.debug_message_error_storage_unusable), this, handler, false, true, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					OpenRawTestImage(view);
				}
			});
		}
	}
}

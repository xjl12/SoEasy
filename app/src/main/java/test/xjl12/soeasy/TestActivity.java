package test.xjl12.soeasy;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.util.*;

public class TestActivity extends Activity
{
    TextView test_textview1;
	/** 获取SD卡内缓存目录 */
	public File getAppCacheDir(Context context,String name)
	{
		File cache_file = new File (context.getExternalCacheDir(),name);
		return cache_file;
	}
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        //Test Fragment
        /**if (findViewById(R.id.testFrameLayout) != null)
        {
            if (savedInstanceState != null)
            {
                return;
            }
            Fragment first = new Fragment();
            first.setArguments(getIntent().getExtras());
            
            getFragmentManager().beginTransaction().add(R.id.testFrameLayout,first).commit();
        }*/
        test_textview1 = (TextView) findViewById(R.id.testTextView1);
        //test_textview1.setText(Environment.getExternalStorageState());
        Log.i("TestActivity" , "TEST");
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.exit_action:
				Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
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
    public void test1 (View view)
    {
        Intent web = new Intent();
        web.setAction("android.intent.action.VIEW");
        Uri baidu = Uri.parse("https:www.baidu.com");
        web.setData(baidu);
        startActivity(web);
    }
    public void TestFileWrite (View view) throws FileNotFoundException, IOException
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
	public void OpenRawTestImage (View view) throws IOException
	{
		Log.i("TestActivity","Begin!");
		File cache_file = this.getAppCacheDir(getApplicationContext(),Others.RandomString(8));
		InputStream test_is = getResources().openRawResource(R.drawable.ic_menu_background);
		FileOutputStream test_fos = new FileOutputStream(cache_file);
		byte[] buffer = new byte[1024];
		int byte_count = 0;
		while ((byte_count = test_is.read(buffer)) != -1)
		{
			test_fos.write(buffer,0,byte_count);
		}
		test_fos.flush();
		test_is.close();
		test_fos.close();
		Uri test_image = Uri.fromFile(cache_file);
		Intent open_test_image = new Intent(Intent.ACTION_VIEW);
		open_test_image.setDataAndType(test_image,"image/*");
		startActivity(open_test_image);
	}
}

package test.xjl12.soeasy;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import test.xjl12.soeasy.*;

import android.support.v7.widget.Toolbar;
import android.content.res.*;

public class SecondActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

		final Toolbar second_toolbar = (Toolbar) findViewById(R.id.second_mdToolbar);
		final FloatingActionButton second_fab = (FloatingActionButton) findViewById(R.id.second_fab);
		final TextView textview = (TextView) findViewById(R.id.second_content_TextView1);

		setSupportActionBar(second_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		textview.setText(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE));
		Snackbar.make(findViewById(R.id.second_mdCoordinatorLayout), R.string.second_Activity_text, Snackbar.LENGTH_SHORT).setActionTextColor(getResources().getColor(R.color.colorAccent_Light)).setAction(R.string.second_Activity_snackbar_action, new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					finish();
				}
			}).show();

		second_fab.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View p1)
				{
					Intent share_intent = new Intent(Intent.ACTION_SEND);
					share_intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_from_soeasy,textview.getText()));
					share_intent.setType("text/plain");
					if (Others.isQQInstalled(SecondActivity.this))
					{
						share_intent.setPackage(getString(R.string.qq_name));
					}
					startActivity(Intent.createChooser(share_intent, getString(R.string.share_point)));
				}
			});
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.source_item:
				Intent source_web = new Intent(Intent.ACTION_VIEW);
				source_web.setData(Uri.parse(this.getResources().getStringArray(R.array.web_url)[0]));
				startActivity(source_web);
				break;
			default:
				Snackbar.make(findViewById(R.id.main_mdCoordinatorLayout),R.string.wrong,Snackbar.LENGTH_LONG)
					.setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
					.setAction(R.string.send_error, new View.OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							Intent share_intent = new Intent(Intent.ACTION_SEND);
							share_intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.send_error_message,Others.getAppVersionName(getApplicationContext()),Others.getRunningActivityName(SecondActivity.this),item.getTitle().toString()));
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
		return true;
	}
	@Override
	public void onResume ()
	{
		super.onResume();
		Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
	}
}

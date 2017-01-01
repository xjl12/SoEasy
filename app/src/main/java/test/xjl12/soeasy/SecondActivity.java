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
		final CoordinatorLayout sCl = (CoordinatorLayout) findViewById(R.id.second_mdCoordinatorLayout);

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
					startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.share_from_soeasy, textview.getText())).setType("text/plain")), getString(R.string.share_point)));
				}
			});

		second_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(final MenuItem p1)
				{
					switch (p1.getItemId())
					{
						case R.id.source_item:
							Intent source_web = new Intent(Intent.ACTION_VIEW);
							source_web.setData(Uri.parse(SecondActivity.this.getResources().getStringArray(R.array.web_url)[0]));
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
											Snackbar.make(sCl, R.string.Error_shot_error, Snackbar.LENGTH_LONG)
												.setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
												.setAction(R.string.retry, new View.OnClickListener(){

													@Override
													public void onClick(View p1)
													{
														Intent intent = Others.getShot(SecondActivity.this);
														if (intent != null)
														{
															startActivity(intent);
														}
														else
														{
															Toast.makeText(SecondActivity.this, getString(R.string.Error_cannot_shot), Toast.LENGTH_LONG);
														}
													}
												}).show();
										}	
									}
								}).start();
							break;
						default:
							Snackbar.make(sCl, R.string.wrong, Snackbar.LENGTH_LONG)
								.setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
								.setAction(R.string.send_error, new View.OnClickListener(){

									@Override
									public void onClick(View view)
									{
										startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(SecondActivity.this), p1.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));
									}
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
		getMenuInflater().inflate(R.menu.activity_actions, menu);
		return true;
	}

	/*@Override
	 public boolean onOptionsItemSelected(final MenuItem item)
	 {
	 switch (item.getItemId())
	 {
	 case R.id.homeAsUp:
	 finish();
	 break;
	 case R.id.source_item:
	 Intent source_web = new Intent(Intent.ACTION_VIEW);
	 source_web.setData(Uri.parse(this.getResources().getStringArray(R.array.web_url)[0]));
	 startActivity(source_web);
	 break;
	 /*default:
	 Snackbar.make(findViewById(R.id.second_mdCoordinatorLayout),R.string.wrong,Snackbar.LENGTH_LONG)
	 .setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
	 .setAction(R.string.send_error, new View.OnClickListener(){

	 @Override
	 public void onClick(View p1)
	 {
	 startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(),new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT,getString(R.string.send_error_message,Others.getAppVersionName(getApplicationContext()),Others.getRunningActivityName(SecondActivity.this),item.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));
	 }
	 }).show();
	 break;
	 }
	 return true;
	 }*/
	@Override
	public void onResume()
	{
		super.onResume();
		Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
	}
}

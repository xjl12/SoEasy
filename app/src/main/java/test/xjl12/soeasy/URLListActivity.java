package test.xjl12.soeasy;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import android.support.v7.widget.Toolbar;
import android.support.design.widget.*;
import android.graphics.*;

public class URLListActivity extends AppCompatActivity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urllist);
		Toolbar toolbar = (Toolbar) findViewById(R.id.urllist_mdToolbar);
		ListView url_ls = (ListView) findViewById(R.id.urllist);
		//FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.urllist_fab);
		final CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.urllist_mdCoordinatorLayout);
		
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
														Intent intent = Others.getShot(URLListActivity.this);
														if (intent != null)
														{
															startActivity(intent);
														}
														else
														{
															Toast.makeText(URLListActivity.this,getString(R.string.Error_cannot_shot),Toast.LENGTH_LONG);
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
									{startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(URLListActivity.this), item.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));}
								}).show();
							break;
					}
					return true;
				}
			});
		/*RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.urllist);
		mRecyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager (this);
		RecyclerView.Adapter mAdapter = new MyAdapter (this.getResources().getStringArray(R.array.web_url_title));*/
		
		ListAdapter url_la = new ArrayAdapter<String> (this,android.R.layout.simple_list_item_1,this.getResources().getStringArray(R.array.web_url_title));
		
		url_ls.setAdapter(url_la);
		url_ls.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					Intent url_intent = new Intent(Intent.ACTION_VIEW);
					Uri url = Uri.parse(URLListActivity.this.getResources().getStringArray(R.array.web_url)[p3]);
					url_intent.setData(url);
					startActivity(url_intent);
				}
		});
    }
	
}

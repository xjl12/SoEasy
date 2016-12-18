package test.xjl12.soeasy;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.view.*;
import android.content.*;
import java.net.*;
import android.net.*;
import android.support.v7.widget.*;

public class URLListActivity extends Activity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urllist);
		/*RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.urllist);
		mRecyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager (this);
		RecyclerView.Adapter mAdapter = new MyAdapter (this.getResources().getStringArray(R.array.web_url_title));*/
		ListAdapter url_la = new ArrayAdapter<String> (this,android.R.layout.simple_list_item_1,this.getResources().getStringArray(R.array.web_url_title));
		ListView url_ls = (ListView) findViewById(R.id.urllist);
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

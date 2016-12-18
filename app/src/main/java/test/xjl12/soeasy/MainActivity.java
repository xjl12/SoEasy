package test.xjl12.soeasy;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.util.*;
import android.view.View.*;

public class MainActivity extends AppCompatActivity
{
//    public final static String EXTRA_MESSAGE ="test.xjl12.soeasy.MESSAGE";
	//public LinearLayout main_LinearLayout;
	//public int set_theme = -1;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		/** if (savedInstanceState != null)
		{
			if (savedInstanceState.getInt("theme") != -1)
			{
				setTheme(savedInstanceState.getInt("theme"));
			}
		} */
        setContentView(R.layout.main);
		
		final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.main_DrawerLayout);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.main_mdToolbar);
		final NavigationView mNavigation = (NavigationView) findViewById(R.id.main_NavigationView);
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
		
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	    
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (this,mDrawerLayout,toolbar,R.string.app_name,R.string.app_name);
		mDrawerLayout.setDrawerListener(toggle);
		toggle.syncState();
		
		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					
				}
			});
		toolbar.setNavigationOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					fab.hide();
					mDrawerLayout.openDrawer(GravityCompat.START);
				}
			});
		mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

				@Override
				public boolean onNavigationItemSelected(MenuItem p1)
				{
					switch (p1.getItemId())
					{
						case R.id.navigation_item_games:
							Intent games_intent = new Intent (MainActivity.this,GamesActivity.class);
							startActivity(games_intent);
							break;
						case R.id.navigation_item_urllist:
							Intent url_list_intent = new Intent (MainActivity.this,URLListActivity.class);
							startActivity(url_list_intent);
							break;
						case R.id.navigation_item_test:
							Intent test_intent = new Intent (MainActivity.this,TestActivity.class);
							startActivity(test_intent);
							break;
						case R.id.navigation_item_debug:
							Intent debug_intent = new Intent (MainActivity.this,DebugActivity.class);
        					startActivity(debug_intent);
							break;
						case R.id.navigation_item_exit:
							Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
							finish();
							break;
					}
					mDrawerLayout.closeDrawer(GravityCompat.START);
					return true;
				}
			});
		//ImageView icon = (ImageView) mNavigation.getHeaderView(0).findViewById(R.id.);
		
    }
}
	
    /**@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }*/
	/**@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putInt("theme",set_theme);
	} */
	/** public void button1(View view)
	{
		Intent intent = new Intent (this, SecondActivity.class);
		EditText editText = (EditText) findViewById(R.id.mainEditText);
        String edittext = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE,edittext);
        if (editText.length() == 0)
        {
            Toast.makeText(this,R.string.second_Activity_text_null,Toast.LENGTH_SHORT).show();
            //Context context = null;
            //Toast.makeText(context, R.string.second_Activity_text_null,l).show();

        }
        else
        {
            startActivity(intent);
        }
	} */

   /** public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            
            case R.id.exit_action:
				Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
                finish();
                return true;
            case R.id.app_theme_choose_holo:
                set_theme = android.R.style.Theme_Holo;
				this.recreate();
                return true;
            case R.id.app_theme_choose_default:
				set_theme = R.style.AppTheme;
				this.recreate();
				return true;
			
        }
            
         return super.onOptionsItemSelected(item);
    }*/

/* public class ArticleFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO: Implement this method
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
}
*/

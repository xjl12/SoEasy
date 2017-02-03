package test.xjl12.soeasy;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import test.xjl12.soeasy.*;

import android.support.v7.widget.Toolbar;
import android.net.*;

public class GamesActivity extends AppCompatActivity
{
    public static int p = (int) (Math.random()*101);
    int t;
    TextView GamesTextview;
    TextView TimesView;
    TextInputEditText input;
    Button games_see_answers;
	FloatingActionButton fab;
	Toolbar toolbar;
	CoordinatorLayout mCl;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games);
		
        GamesTextview = (TextView) findViewById(R.id.games_MainTextView);
        TimesView = (TextView) findViewById(R.id.games_times_view);
        games_see_answers = (Button) findViewById(R.id.games_see_answers);
		input = (TextInputEditText) findViewById(R.id.games_m_input);
		fab = (FloatingActionButton) findViewById(R.id.games_fab);
		toolbar = (Toolbar) findViewById(R.id.games_mdToolbar);
		mCl = (CoordinatorLayout) findViewById(R.id.games_mdCoordinatorLayout);
		
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		t = 0;
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
														Intent intent = Others.getShot(GamesActivity.this);
														if (intent != null)
														{
															startActivity(intent);
														}
														else
														{
															Toast.makeText(GamesActivity.this,getString(R.string.Error_cannot_shot),Toast.LENGTH_LONG);
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
									{startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(GamesActivity.this), item.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));}
								}).show();
							break;
					}
					return true;
				}
			});
		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent share = new Intent(Intent.ACTION_SEND);
					share.putExtra(Intent.EXTRA_TEXT,getString(R.string.games_share,t));
					share.setType("text/plain");
					startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(),share),getString(R.string.share_point)));
				}
			});
		mCl.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(),p1,input);
				}
			});
        games_see_answers.setOnLongClickListener(new OnLongClickListener(){

                @Override
                public boolean onLongClick(View p1)
                {
                    Toast.makeText(GamesActivity.this ,Integer.toString(p),Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
		fab.hide();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    
    public void games_submit(View games_submit_view)
    {
        if (input.length() == 0 )//this.getString(R.string.games_edittext))
        {
            GamesTextview.setTextSize(20);
            GamesTextview.setText(R.string.games_null);
        }
        else
        {
            t++;
            GamesTextview.setTextSize(25);
            int input_number = Integer.parseInt(input.getText().toString());
            if (input_number == p)
            {
                GamesTextview.setTextSize(20);
                GamesTextview.setText(R.string.games_success);
                fab.show();
				
                if (t > 5)
                {
                    TimesView.setText(getResources().getString(R.string.games_times) + Integer.toString(t) + getResources().getString(R.string.games_times_more));
                }
                else 
                {
                    TimesView.setText(getResources().getString(R.string.games_times) + Integer.toString(t) + getResources().getString(R.string.games_times_less));
                }
                p = (int) (Math.random()*101);
                t = 0;
            }
            else
            {
                if (input_number > p)
                {
                    GamesTextview.setText(R.string.games_big);
                }
                else 
                {
                    GamesTextview.setText(R.string.games_small);
                }
            }
        }
        
    }
    public void games_see_answers(View games_see_answers_view)
    {
        if (t == 0 )
        {
            TimesView.setText(R.string.games_not_see_answers);
        }
        else
        {
            GamesTextview.setTextSize(40);
            GamesTextview.setText(this.getString(R.string.games_answers) + Integer.toString(p));
            t=0;
            p = (int) (Math.random()*101);
        }
    }
    public void games_restart (View games_restart_view)
    {
        p = (int) (Math.random()*101);
        t = 0;
        this.recreate();
    }

   
    public void games_exit (View games_exit_view)
    {
        finish();
    }
    
}

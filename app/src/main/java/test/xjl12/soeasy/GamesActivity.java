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
		Others.initActivity(this,toolbar,mCl);
		t = 0;

		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent share = new Intent(Intent.ACTION_SEND);
					share.putExtra(Intent.EXTRA_TEXT,getString(R.string.games_share,Integer.toString(t)));
					share.setType("text/plain");
					startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(),share),getString(R.string.share_point)));
				}
			});
		mCl.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(),view,input);
                return false;
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

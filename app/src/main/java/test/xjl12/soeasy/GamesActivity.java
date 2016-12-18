package test.xjl12.soeasy;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.support.design.widget.*;

public class GamesActivity extends Activity
{
    public static int p = (int) (Math.random()*101);
    public int t;
    public TextView MainTextview;
    public TextView TimesView;
    public TextInputEditText input;
    public Button games_see_answers;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games);
        t = 0;
        MainTextview = (TextView) findViewById(R.id.games_MainTextView);
        TimesView = (TextView) findViewById(R.id.games_times_view);
        games_see_answers = (Button) findViewById(R.id.games_see_answers);
		input = (TextInputEditText) findViewById(R.id.games_m_input);
        games_see_answers.setOnLongClickListener(new OnLongClickListener(){

                @Override
                public boolean onLongClick(View p1)
                {
                    Toast.makeText(GamesActivity.this ,Integer.toString(p),Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.exit_action:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void games_submit(View games_submit_view)
    {
        if (input.length() == 0 )//this.getString(R.string.games_edittext))
        {
            MainTextview.setTextSize(20);
            MainTextview.setText(R.string.games_null);
        }
        else
        {
            t++;
            MainTextview.setTextSize(25);
            int input_number = Integer.parseInt(input.getText().toString());
            if (input_number == p)
            {
                MainTextview.setTextSize(20);
                MainTextview.setText(R.string.games_success);
                
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
                    MainTextview.setText(R.string.games_big);
                }
                else 
                {
                    MainTextview.setText(R.string.games_small);
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
            MainTextview.setTextSize(40);
            MainTextview.setText(this.getString(R.string.games_answers) + Integer.toString(p));
            t=0;
            p = (int) (Math.random()*101);
        }
    }
    public void games_restart (View games_restart_view)
    {
        p = (int) (Math.random()*101);
        t = 0;
        Intent intent = getIntent();
        startActivity(intent);
        finish();
        //setContentView(R.layout.games);
    }

   
    public void games_exit (View games_exit_view)
    {
        finish();
    }
    
}

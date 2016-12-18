package test.xjl12.soeasy;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

public class SecondActivity extends Activity
{
	/**
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.second);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String edittext = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textview = new TextView(this);
        textview.setTextSize(50);
        textview.setText(edittext);
        System.out.println(edittext);
        setContentView(textview);
    } */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }
}

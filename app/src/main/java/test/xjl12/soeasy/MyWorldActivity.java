package test.xjl12.soeasy;
import android.support.v7.app.*;
import android.os.*;
import android.support.v7.widget.*;

public class MyWorldActivity extends AppCompatActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_world);
		
		Toolbar mToolbar = (Toolbar) findViewById(R.id.my_world_mdToolbar);
		
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
	
}

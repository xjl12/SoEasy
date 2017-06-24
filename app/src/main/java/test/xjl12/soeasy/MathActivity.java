package test.xjl12.soeasy;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MathActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        Toolbar toolbar = (Toolbar) findViewById(R.id.math_toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.math_fab);
        CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.math_mdCoordinatorLayout);
        Button clear_button = (Button) findViewById(R.id.math_clear_button);
        Button calculate_button = (Button) findViewById(R.id.math_calculate_button);
        final TextInputEditText input_edittext = (TextInputEditText) findViewById(R.id.math_input_edittext);
        TextView output_textview = (TextView) findViewById(R.id.math_output_textview);
        TextView output_point_textview = (TextView) findViewById(R.id.math_output_point_textview);

        Others.initActivity(this,toolbar,mCl);

        mCl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(),view,input_edittext);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

}

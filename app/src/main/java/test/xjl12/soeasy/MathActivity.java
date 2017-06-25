package test.xjl12.soeasy;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MathActivity extends AppCompatActivity {

    //定义计算模式
    int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        Toolbar toolbar = (Toolbar) findViewById(R.id.math_toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.math_fab);
        final CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.math_mdCoordinatorLayout);
        Button clear_button = (Button) findViewById(R.id.math_clear_button);
        Button calculate_button = (Button) findViewById(R.id.math_calculate_button);
        final TextInputEditText input_edittext = (TextInputEditText) findViewById(R.id.math_input_edittext);
        final TextView output_textview = (TextView) findViewById(R.id.math_output_textview);
        final TextView output_point_textview = (TextView) findViewById(R.id.math_output_point_textview);
        AppCompatSpinner spi = (AppCompatSpinner) findViewById(R.id.math_spinner);
        final MaterialProgressBar progressBar = (MaterialProgressBar) findViewById(R.id.math_progressbar);

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

        spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mode = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        calculate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_edittext.length() == 0 ) {
                    Snackbar.make(mCl,getString(R.string.empty_input),Snackbar.LENGTH_LONG).show();
                }
                else if (input_edittext.length() > 8) {
                    Snackbar.make(mCl,getString(R.string.math_input_too_large),Snackbar.LENGTH_LONG).show();
                }
                else {
                    output_textview.setText(R.string.calculating);
                    output_textview.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    final int input_number = Integer.parseInt(input_edittext.getText().toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int output = modeOne(input_number);

                        }
                    });
                }

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //计算模式核心流程
    private int modeOne (int input)
    {
        int output = 0;
        for (int i=1;i <= input;i++)
        {
            output +=  i;
        }
        return  output;
    }
}

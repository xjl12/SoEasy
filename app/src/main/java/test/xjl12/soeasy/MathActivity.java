package test.xjl12.soeasy;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.Arrays;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MathActivity extends AppCompatActivity {

    //定义计算模式
    int mode_selected = 0;

    boolean show_exact_progress = false;
    private volatile boolean stop = false;

    ProgressBar progressBar_exact;

    private static Handler mHandle = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        Toolbar toolbar = (Toolbar) findViewById(R.id.math_toolbar);
        final CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.math_mdCoordinatorLayout);
        final Button clear_button = (Button) findViewById(R.id.math_clear_button);
        final Button calculate_button = (Button) findViewById(R.id.math_calculate_button);
        final AppCompatButton stop_button = (AppCompatButton) findViewById(R.id.math_stop_button);
        final TextInputEditText input_edittext = (TextInputEditText) findViewById(R.id.math_input_edittext);
        final TextView output_textview = (TextView) findViewById(R.id.math_output_textview);
        final TextView output_point_textview = (TextView) findViewById(R.id.math_output_point_textview);
        final TextView point_textview = (TextView) findViewById(R.id.math_point_textview);
        final TextView time_cost_textview = (TextView) findViewById(R.id.math_time_cost_view);
        final TextView swith_textview = (TextView) findViewById(R.id.math_switch_textview);
        AppCompatSpinner spi = (AppCompatSpinner) findViewById(R.id.math_spinner);
        final MaterialProgressBar progressBar = (MaterialProgressBar) findViewById(R.id.math_progressbar);
        final SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.math_switch);
        progressBar_exact = (ProgressBar) findViewById(R.id.math_progressbar_exact);

        Others.initActivity(this,toolbar,mCl);

        mCl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(),mCl,input_edittext);
                return false;
            }
        });


        spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mode_selected = i;
                if (mode_selected == 4||mode_selected == 5) {
                    switchCompat.setVisibility(View.GONE);
                    swith_textview.setVisibility(View.GONE);
                    input_edittext.setInputType(InputType.TYPE_CLASS_TEXT);
                    point_textview.setText(Html.fromHtml(getResources().getStringArray(R.array.math_point)[mode_selected]));
                }
                else {
                    point_textview.setText(getResources().getStringArray(R.array.math_point)[mode_selected]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        point_textview.setText(getResources().getStringArray(R.array.math_point)[mode_selected]);
        calculate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(),mCl,input_edittext);
                if (input_edittext.length() == 0 ) {
                    Snackbar.make(mCl,getString(R.string.empty_input),Snackbar.LENGTH_LONG).show();
                }
                else if ((mode_selected != 4 && mode_selected !=5) && (input_edittext.length() > 10 || Long.parseLong(input_edittext.getText().toString()) > 2147483647)) {
                    Snackbar.make(mCl,getString(R.string.math_input_too_large),Snackbar.LENGTH_LONG).show();
                }

                else if (input_edittext.length() == 1 && Long.parseLong(input_edittext.getText().toString()) == 0) {
                    Snackbar.make(mCl,getString(R.string.math_not_zero),Snackbar.LENGTH_LONG).show();
                }
                else {
                    stop = false;
                    output_textview.setText(R.string.calculating);
                    output_textview.setVisibility(View.VISIBLE);
                    if (show_exact_progress) {
                        progressBar_exact.setVisibility(View.VISIBLE);
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    calculate_button.setVisibility(View.GONE);
                    clear_button.setVisibility(View.GONE);
                    output_point_textview.setVisibility(View.GONE);
                    time_cost_textview.setVisibility(View.GONE);
                    stop_button.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            final Long time_0 = System.currentTimeMillis();
                            final String output = calculateRun(input_edittext.getText().toString(),mode_selected);
                            final long time_1 = System.currentTimeMillis();
                            if (!stop) {
                                mHandle.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        output_point_textview.setVisibility(View.VISIBLE);
                                        output_textview.setText(output);
                                        progressBar.setVisibility(View.GONE);
                                        if (show_exact_progress) {
                                            progressBar_exact.setVisibility(View.GONE);
                                            progressBar_exact.setProgress(0);
                                        }
                                        clear_button.setVisibility(View.VISIBLE);
                                        calculate_button.setVisibility(View.VISIBLE);
                                        stop_button.setVisibility(View.GONE);
                                        time_cost_textview.setText(getString(R.string.time_cost, String.valueOf(time_1 - time_0)));
                                        time_cost_textview.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_button.setVisibility(View.GONE);
                output_point_textview.setVisibility(View.GONE);
                output_textview.setVisibility(View.GONE);
                input_edittext.setText(null);
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = true;
                stop_button.setVisibility(View.GONE);
                calculate_button.setVisibility(View.VISIBLE);
                output_textview.setVisibility(View.GONE);
                progressBar_exact.setVisibility(View.GONE);
                progressBar_exact.setProgress(0);
                progressBar.setVisibility(View.GONE);
                Snackbar.make(mCl,R.string.math_stop_point,Snackbar.LENGTH_SHORT).show();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                show_exact_progress = b;
                if (show_exact_progress) {
                    swith_textview.setText(R.string.math_switch_on);
                }
                else {
                    swith_textview.setText(R.string.math_switch_off);
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
    private String calculateRun (String inputString,int mode)
    {
        String output = null;
        try {
            int percent = 0;
            switch (mode) {
                case 0:
                    int input0 = Integer.valueOf(inputString);
                    long out_result = 0;
                    for (int i = 1; i <= input0; i++) {
                        if (stop) {
                            break;
                        }
                        out_result += i;
                        if (show_exact_progress) {
                            int percent_now = (int) Math.floor((float) i / input0 * 100);
                            if (percent_now > percent) {
                                setExactProgressInThread(percent_now);
                                percent = percent_now;
                            }
                        }
                    }
                    output = String.valueOf(out_result);
                    break;
                case 1:
                    int input1 = Integer.valueOf(inputString);
                    BigInteger big_out_one = BigInteger.ONE;
                    for (int i = 1; i <= input1; i++) {
                        if (stop) {
                            break;
                        }
                        big_out_one = big_out_one.multiply(BigInteger.valueOf(i));
                        if (show_exact_progress) {
                            int percent_now = (int) Math.floor((float) i / input1 * 100);
                            if (percent_now > percent) {
                                setExactProgressInThread(percent_now);
                                percent = percent_now;
                            }
                        }
                    }
                    output = big_out_one.toString();
                    break;
                case 2:
                    int input2 = Integer.valueOf(inputString);
                    BigInteger big_out_three = BigInteger.ONE;
                    BigInteger big_tmp_one;
                    final BigInteger bit_two = new BigInteger("2");
                    for (int i = 1; i <= input2; i++) {
                        if (stop) {
                            break;
                        }
                        big_tmp_one = bit_two;
                        if (i > 1) {
                            for (int n = 2; n <= i; n++) {
                                if (stop) {
                                    break;
                                }
                                big_tmp_one = big_tmp_one.multiply(bit_two);
                            }
                        }
                        big_out_three = big_out_three.add(big_tmp_one);
                        if (show_exact_progress) {
                            int percent_now = (int) Math.floor((float) i / input2 * 100);
                            if (percent_now > percent) {
                                setExactProgressInThread(percent_now);
                                percent = percent_now;
                            }
                        }
                    }
                    output = big_out_three.toString();
                    break;
                case 3:
                    int input3 = Integer.valueOf(inputString);
                    if (input3 > 2) {
                        BigInteger[] big_out_two = new BigInteger[input3];
                        big_out_two[0] = BigInteger.ZERO;
                        big_out_two[1] = BigInteger.ONE;
                        for (int l = 3; l <= input3; l++) {
                            if (stop) {
                                break;
                            }
                            big_out_two[l - 1] = big_out_two[l - 3].add(big_out_two[l - 2]);
                            if (show_exact_progress) {
                                int percent_now = (int) Math.floor((float) l / input3 * 100);
                                if (percent_now > percent) {
                                    setExactProgressInThread(percent_now);
                                    percent = percent_now;
                                }
                            }
                        }
                        output = big_out_two[input3 - 1].toString();
                    } else if (input3 == 1) {
                        output = "0";
                        setExactProgressInThread(1);
                    } else if (input3 == 2) {
                        output = "1";
                        setExactProgressInThread(2);
                    }
                    break;
                case 4:
                    String[] inputArray = inputString.split(" ");
                    double T = Double.parseDouble(inputArray[inputArray.length-1]);
                    double T2 = Math.pow(T,2);
                    inputArray = Arrays.copyOf(inputArray,inputArray.length-1);
                    int length = inputArray.length%2 == 1 ? inputArray.length + 1 : inputArray.length;
                    double temp1 = 0;
                    for (int i = 0;i<inputArray.length/2;i++)
                            temp1 += Double.parseDouble(inputArray[length/2 + i]) - Double.parseDouble(inputArray[i]);
                    double result = temp1/((length/2)*(inputArray.length/2)*T2)/100;
                    output = String.valueOf((double) Math.round(result*1000)/1000);
                    break;
                case 5:
                    String[] inputArray2 = inputString.split(" ");
                    double T22 = Double.parseDouble(inputArray2[inputArray2.length-1]);
                    double T222 = Math.pow(T22,2);
                    inputArray2 = Arrays.copyOf(inputArray2,inputArray2.length-1);
                    int length2 = inputArray2.length%2 == 1 ? inputArray2.length + 1 : inputArray2.length;
                    double temp12 = 0;
                    double temp22 = 0;
                    for (int i2 = 0;i2<inputArray2.length/2;i2++) {
                        temp22 = i2 == 0 ? Double.parseDouble(inputArray2[i2]):Double.parseDouble(inputArray2[i2]) - Double.parseDouble(inputArray2[i2 - 1]);
                        temp12 += Double.parseDouble(inputArray2[length2 / 2 + i2]) - Double.parseDouble(inputArray2[length2 / 2 + i2 - 1]) - temp22;
                    }
                    double result2 = temp12/((length2/2)*(inputArray2.length/2)*T222)/100;
                    output = String.valueOf((double) Math.round(result2*1000)/1000);
                    break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            output = getString(R.string.input_form_error);
        }
        return  output;
    }

    //线程中设置确切进度
    private void setExactProgressInThread (final int status) {
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                progressBar_exact.setProgress(status);
            }
        });
    }

}

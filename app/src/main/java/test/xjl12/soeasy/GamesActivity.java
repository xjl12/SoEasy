package test.xjl12.soeasy;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import android.support.v7.widget.Toolbar;

import java.util.Random;

public class GamesActivity extends AppCompatActivity {
    private Random random;
    private int p;
    private int t;
    private int min, max;
    private TextView gamesTextview, TimesView;
    private static TextView point_info;
    private TextInputEditText input;
    private FloatingActionButton fab;
    private CoordinatorLayout mCl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games);

        gamesTextview = (TextView) findViewById(R.id.games_MainTextView);
        TimesView = (TextView) findViewById(R.id.games_times_view);
        point_info = (TextView) findViewById(R.id.games_point_info);
        Button games_see_answers = (Button) findViewById(R.id.games_see_answers);
        Button submit_button = (Button) findViewById(R.id.games_submit);
        input = (TextInputEditText) findViewById(R.id.games_m_input);
        fab = (FloatingActionButton) findViewById(R.id.games_fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.games_mdToolbar);
        mCl = (CoordinatorLayout) findViewById(R.id.games_mdCoordinatorLayout);

        Others.initActivity(this, toolbar, mCl);
        random = new Random();
        initData();

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.games_share, Integer.toString(t)));
                share.setType("text/plain");
                startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), share), getString(R.string.share_point)));
            }
        });
        mCl.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(), view, input);
                return false;
            }
        });
        if (BuildConfig.DEBUG) {
            games_see_answers.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View p1) {
                    Toast.makeText(GamesActivity.this, Integer.toString(p), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        fab.hide();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 > i1) {
                    gamesTextview.setText(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_GO || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    gamesSubmit();
                }
                return true;
            }
        });
        submit_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gamesSubmit();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_actions, menu);
        menu.findItem(R.id.games_show_point_info_item).setVisible(true);
        /*checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    point_info.setVisibility(View.VISIBLE);
                } else {
                    point_info.setVisibility(View.GONE);
                }
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

    public static void showPointInfo() {
        if (point_info.getVisibility() != View.VISIBLE) {
            point_info.setVisibility(View.VISIBLE);
        }
        else {
            point_info.setVisibility(View.GONE);
        }
    }
    public void gamesSubmit() {
        if (input.length() == 0) {
            gamesTextview.setTextSize(20);
            gamesTextview.setText(R.string.games_null);
        } else {
            t++;
            gamesTextview.setTextSize(25);
            int input_number = Integer.parseInt(input.getText().toString());
            if (input_number == p) {
                gamesTextview.setTextSize(20);
                gamesTextview.setText(R.string.games_success);
                fab.show();
                Others.ChangeEdittextStatusAndHideSoftInput(this, mCl, input);
                if (t > 5) {
                    TimesView.setText(getResources().getString(R.string.games_times) + Integer.toString(t) + getResources().getString(R.string.games_times_more));
                } else {
                    TimesView.setText(getResources().getString(R.string.games_times) + Integer.toString(t) + getResources().getString(R.string.games_times_less));
                }
                initData();
            } else if (input_number > p) {
                gamesTextview.setText(R.string.games_big);
                if (input_number < max) {
                    max = input_number;
                    changePointInfo();
                }
            } else {
                gamesTextview.setText(R.string.games_small);
                if (input_number > min) {
                    min = input_number;
                    changePointInfo();
                }
            }

        }
        input.setText(null);
    }

    public void games_see_answers(View games_see_answers_view) {
        if (t == 0) {
            TimesView.setText(R.string.games_not_see_answers);
        } else {
            gamesTextview.setTextSize(40);
            gamesTextview.setText(this.getString(R.string.games_answers) + String.valueOf(p));
            initData();
        }
    }

    public void games_restart(View games_restart_view) {
        initData();
        this.recreate();
    }

    public void initData() {
        p = (random.nextInt(100) + 1);
        t = 0;
        min = 0;
        max = 100;
        changePointInfo();
    }

    private void changePointInfo() {
        point_info.setText(getString(R.string.games_point_info, min, max));
    }

    public void games_exit(View games_exit_view) {
        finish();
    }

}

package test.xjl12.soeasy;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import test.xjl12.soeasy.R;

public class AdditionActivity extends AppCompatActivity {

    private Button begin_game, raking, exitButton;
    private TextView additionGamePointTextview;
    private TextView additionGamePlayer1;
    private TextView additionGamePlayer2;
    private TextView additionGamePlayer3;
    private TextView additionGameCom1;
    private TextView additionGameCom2;
    private TextView additionGameCom3;
    private TextView pointTextview;
    private AppCompatSpinner spinner;
    private ConstraintLayout constraintLayout;
    private int com1, com2, com3, player1, player2, player3,default_num = 1;
    private int difficult = 0;
    private boolean comClick1, comClick2, comClick3, playerClick1, playerClick2, playerClick3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addition_toolbar);
        spinner = (AppCompatSpinner) findViewById(R.id.addition_welcom_spinner);
        begin_game = (Button) findViewById(R.id.addition_begin_game_botton);
        raking = (Button) findViewById(R.id.addition_welcom_ranking_button);
        exitButton = (Button) findViewById(R.id.addition_exit);
        constraintLayout = (ConstraintLayout) findViewById(R.id.addition_conlayout);
        CoordinatorLayout mCl = (CoordinatorLayout) findViewById(R.id.addition_mCl);

        Others.initActivity(this, toolbar, mCl);

        begin_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout.removeAllViews();
                getLayoutInflater().inflate(R.layout.addition_game,constraintLayout,true);
                initGameViews();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                difficult = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initGameViews() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touchView(view);
            }
        };
        additionGamePlayer1 = (TextView) findViewById(R.id.addition_game_player1);
        additionGamePlayer2 = (TextView) findViewById(R.id.addition_game_player2);
        additionGamePlayer3 = (TextView) findViewById(R.id.addition_game_player3);
        additionGameCom1 = (TextView) findViewById(R.id.addition_game_com1);
        additionGameCom2 = (TextView) findViewById(R.id.addition_game_com2);
        additionGameCom3 = (TextView) findViewById(R.id.addition_game_com3);
        TextView[] textViews = new TextView[] {additionGamePlayer1,additionGamePlayer2,additionGamePlayer3,additionGameCom3,additionGameCom2,additionGameCom1};
        for (int i = 0;i < textViews.length;i++) {
            changeText(textViews[i],default_num);
            textViews[i].setOnClickListener(click);

        }
        switch (difficult) {
            case 1:
                additionGamePlayer3.setVisibility(View.VISIBLE);
                additionGameCom3.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void changeText(TextView textView,int i) {
        String result = getString(R.string.addition_show_int,i);
        textView.setText(result);
    }
    private void touchView(final View view) {
        view.setBackgroundResource(R.drawable.addition_button_shape_pressed);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundResource(R.drawable.addition_button_shape_up);
            }
        },300);
    }
}

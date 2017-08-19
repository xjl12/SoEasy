package test.xjl12.soeasy;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class AdditionActivity extends AppCompatActivity {

    private Button begin_game, raking, exitButton;
    private CoordinatorLayout mCl;
    private FloatingActionButton fab;
    private TextView welcomeTextView;
    private TextView additionGamePointTextview;
    private TextView additionGamePlayer1;
    private TextView additionGamePlayer2;
    private TextView additionGamePlayer3;
    private TextView additionGameCom1;
    private TextView additionGameCom2;
    private TextView additionGameCom3;
    private TextView[] additionGameComs;
    private TextView[] additionGamePlayers;
    TextView[] textViews;
    private TextView pointTextview;
    private TextView scoreTextView;
    private TextView current = null;
    private TextView current2 = null;
    private AppCompatSpinner spinner;
    private ConstraintLayout constraintLayout;
    private Vibrator vibrator;
    private String winmessage;
    private AlertDialog.Builder gameOverBuilder, gameWinBuilder;
    private boolean isGameOver = false;
    private int difficult, score, target, wincom1, wincom2, wincom3, winplayer1, winplayer2, winplayer3 = 0;

    private Runnable afterComdoRunable = new Runnable() {
        @Override
        public void run() {
            changePlayerClickable(true);
            if (isGameOver) {
                gameOverBuilder.setMessage(getString(R.string.game_over_score, score));
                gameOverBuilder.show();
            } else {
                changeBout(false);
            }
        }
    };
    private Runnable computerDo = new Runnable() {
        @Override
        public void run() {
            changeBout(true);
            computerPlay();
        }
    };
    private Runnable computerTouch = new Runnable() {
        @Override
        public void run() {
            changeText(current2, target);
            changeClickable(false);
            changePlayerClickable(true);
            touchView(current, false);
            current = current2 = null;
            mHandle.postDelayed(afterComdoRunable, 500);
        }
    };
    private Handler mHandle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addition_toolbar);
        constraintLayout = (ConstraintLayout) findViewById(R.id.addition_conlayout);
        mCl = (CoordinatorLayout) findViewById(R.id.addition_mCl);

        Others.initActivity(this, toolbar, mCl);
        initBasicViews();

        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initBasicViews() {
        spinner = (AppCompatSpinner) findViewById(R.id.addition_welcom_spinner);
        begin_game = (Button) findViewById(R.id.addition_begin_game_botton);
        raking = (Button) findViewById(R.id.addition_welcom_ranking_button);
        exitButton = (Button) findViewById(R.id.addition_exit);
        welcomeTextView = (TextView) findViewById(R.id.addition_welcom_textview);

        DialogInterface.OnClickListener playgainListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restartGame();
                dialogInterface.dismiss();
            }
        };
        DialogInterface.OnClickListener notPlayListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        gameWinBuilder = new AlertDialog.Builder(this);
        gameWinBuilder.setTitle(R.string.you_won);
        gameWinBuilder.setCancelable(BuildConfig.DEBUG);
        gameWinBuilder.setPositiveButton(R.string.play_again, playgainListener);
        gameWinBuilder.setNegativeButton(R.string.not_play, notPlayListener);
        gameWinBuilder.setNeutralButton(R.string.share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_from_soeasy, winmessage));
                share.setType("text/plain");
                share = Others.isQQInstalled(AdditionActivity.this, share);
                startActivity(Intent.createChooser(share, getString(R.string.share_point)));
                finish();
            }
        });

        gameOverBuilder = new AlertDialog.Builder(this);
        gameOverBuilder.setTitle(R.string.game_over);
        gameOverBuilder.setCancelable(BuildConfig.DEBUG);
        gameOverBuilder.setPositiveButton(R.string.play_again, playgainListener);
        gameOverBuilder.setNegativeButton(R.string.not_play, notPlayListener);

        begin_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout.removeAllViews();
                getLayoutInflater().inflate(R.layout.addition_game, constraintLayout, true);
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
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.activity_actions, menu);
            return super.onCreateOptionsMenu(menu);
    }

    private void initGameViews() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.addition_game_player1:
                    case R.id.addition_game_player2:
                    case R.id.addition_game_player3:
                        current = (TextView) view;
                        changeClickable(true);
                        changePlayerClickable(false);
                        touchView(view, false);
                        break;
                    case R.id.addition_game_com1:
                    case R.id.addition_game_com2:
                    case R.id.addition_game_com3:
                        touchView(view, false);
                        int player = getValueFromTextView(current);
                        int com = getValueFromTextView((TextView) view);
                        player += com;
                        if (player >= 10) player -= 10;
                        if (player == 0) {
                            switch (difficult) {
                                case 0:
                                    winmessage = getString(R.string.game_win_with_message_easy, winplayer1, winplayer2, wincom1, wincom2);
                                    break;
                                case 1:
                                    winmessage = getString(R.string.game_win_with_message_difficult, winplayer1, winplayer2, winplayer3, wincom1, wincom2, wincom3);
                                    break;
                            }
                            gameWinBuilder.setMessage(winmessage);
                            gameWinBuilder.show();
                        }
                        score++;
                        changeScore();
                        changeText(current, player);
                        current = null;
                        mHandle.postDelayed(computerDo, 500);
                        break;
                }
            }
        };
        additionGamePlayer1 = (TextView) findViewById(R.id.addition_game_player1);
        additionGamePlayer2 = (TextView) findViewById(R.id.addition_game_player2);
        additionGamePlayer3 = (TextView) findViewById(R.id.addition_game_player3);
        additionGameCom1 = (TextView) findViewById(R.id.addition_game_com1);
        additionGameCom2 = (TextView) findViewById(R.id.addition_game_com2);
        additionGameCom3 = (TextView) findViewById(R.id.addition_game_com3);
        pointTextview = (TextView) findViewById(R.id.addition_game_point_textview);
        scoreTextView = (TextView) findViewById(R.id.addition_score_textview);
        additionGamePlayers = new TextView[]{additionGamePlayer1, additionGamePlayer2, additionGamePlayer3};
        additionGameComs = new TextView[]{additionGameCom1, additionGameCom2, additionGameCom3};
        textViews = new TextView[]{additionGamePlayer1, additionGamePlayer2, additionGamePlayer3, additionGameCom3, additionGameCom2, additionGameCom1};
        for (int i = 0; i < textViews.length; i++) {
            changeText(textViews[i], 1);
            textViews[i].setOnClickListener(click);
        }
        switch (difficult) {
            case 0:
                changeText(additionGameCom3, 0);
                changeText(additionGamePlayer3, 0);
                break;
            case 1:
                additionGamePlayer3.setVisibility(View.VISIBLE);
                additionGameCom3.setVisibility(View.VISIBLE);
                break;
        }
        score = 0;
        changeBout(false);
        changeScore();
        changeClickable(false);
    }

    private void changeText(TextView textView, int i) {
        String result = getString(R.string.addition_show_int, i);
        textView.setText(result);
    }

    private void changeScore() {
        scoreTextView.setText(getString(R.string.addition_show_score, score));
    }

    private void changeBout(boolean isComBout) {
        int stringRes = isComBout ? R.string.computer : R.string.player;
        String result = getString(R.string.addition_game_point, getString(stringRes));
        pointTextview.setText(result);
    }

    private void changeClickable(boolean clickable) {
        additionGameCom1.setEnabled(clickable);
        additionGameCom2.setEnabled(clickable);
        additionGameCom3.setEnabled(clickable);
    }

    private void changePlayerClickable(boolean clickable) {
        additionGamePlayer1.setEnabled(clickable);
        additionGamePlayer2.setEnabled(clickable);
        additionGamePlayer3.setEnabled(clickable);
    }

    private void touchView(final View view, boolean isWarn) {
        view.setClickable(false);
        int backgroundRes = isWarn ? R.drawable.addition_button_shape_warn : R.drawable.addition_button_shape_pressed;
        view.setBackgroundResource(backgroundRes);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundResource(R.drawable.addition_button_shape_up);
                view.setClickable(true);
            }
        }, 200);
    }

    private void warnShow() {
        Snackbar.make(mCl, R.string.warn_operation, Snackbar.LENGTH_LONG).show();
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100);
        }
    }

    private int getValueFromTextView(TextView textView) {
        String text = (String) textView.getText();
        String result = text.substring(2, text.length() - 2);
        return Integer.parseInt(result);
    }

    //Core Code

    private void computerPlay() {
        int[] players = new int[]{getValueFromTextView(additionGamePlayer1), getValueFromTextView(additionGamePlayer2), getValueFromTextView(additionGamePlayer3)};
        int[] coms = new int[]{getValueFromTextView(additionGameCom1), getValueFromTextView(additionGameCom2), getValueFromTextView(additionGameCom3)};
        for (int comIndex = 0; comIndex < coms.length; comIndex++) {
            for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
                int result = coms[comIndex] + players[playerIndex];
                if (result == 10) {
                    isGameOver = true;
                    target = 0;
                    current2 = additionGameComs[comIndex];
                    current = additionGamePlayers[playerIndex];
                    break;
                }
            }
            if (isGameOver) break;
        }
        if (!isGameOver) {
            Random random = new Random();
            TextView[][] possibleTextViews = new TextView[9][2];
            int[] possibleInt = new int[9];
            int possibleTextViewsIndex = 0;
            for (int comIndex = 0; comIndex < coms.length; comIndex++) {
                if (coms[comIndex] == 0) continue;
                for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
                    if (players[playerIndex] == 0) continue;
                    int comAndPlayer = coms[comIndex] + players[playerIndex];
                    if (comAndPlayer > 10) comAndPlayer -= 10;
                    int[] newComs = Arrays.copyOf(coms,3);
                    newComs[comIndex] = comAndPlayer;
                    boolean checkResult = true;
                    for (int index = 0; index < players.length; index++) {
                        if (players[index] == 0) continue;
                        int check = comAndPlayer + players[index];
                        if (check >= 10) check -= 10;
                        if (check == 0) {
                            checkResult = false;
                            break;
                        }
                        else {
                            int[] newPlayer = Arrays.copyOf(players,3);
                            newPlayer[index] = check;
                            checkResult = deepCheck(newComs,newPlayer);
                        }
                    }
                    if (checkResult) {
                        possibleInt[possibleTextViewsIndex] = comAndPlayer;
                        possibleTextViews[possibleTextViewsIndex][0] = additionGameComs[comIndex];
                        possibleTextViews[possibleTextViewsIndex][1] = additionGamePlayers[playerIndex];
                        possibleTextViewsIndex++;
                    }
                }
            }
            if (possibleTextViewsIndex != 0) {
                int resultIndex = random.nextInt(possibleTextViewsIndex);
                target = possibleInt[resultIndex];
                current2 = possibleTextViews[resultIndex][0];
                current = possibleTextViews[resultIndex][1];
            } else {
                wincom1 = coms[0];
                wincom2 = coms[1];
                wincom3 = coms[2];
                winplayer1 = players[0];
                winplayer2 = players[1];
                winplayer3 = players[2];
                int bound = 0;
                switch (difficult) {
                    case 0:
                        bound = 2;
                        break;
                    case 1:
                        bound = 3;
                        break;

                }
                int index1 = random.nextInt(bound);
                int index2 = random.nextInt(bound);
                target = coms[index1] + players[index2];
                current2 = additionGameComs[index1];
                current = additionGamePlayers[index2];
            }
        }
        touchView(current2, false);
        mHandle.postDelayed(computerTouch, 400);
    }
    private boolean deepCheck(int[] com,int[] player) {
        int possible = 0;
        for (int index1 = 0;index1 < com.length;index1++) {
            if (com[index1] == 0) continue;
            for (int index2 = 0;index2 < player.length;index2++) {
                if (player[index2] == 0) continue;
                int comAndPlayer = com[index1] + player[index2];
                if (comAndPlayer > 10) comAndPlayer -= 10;
                boolean checkResult = true;
                for (int index3 = 0;index3 < player.length;index3++) {
                    if (player[index3] == 0) continue;
                    int check = comAndPlayer + player[index3];
                    if (check >= 10) check -= 10;
                    if (check == 0) {
                        checkResult = false;
                        break;
                    }
                }
                if (checkResult) possible++;
            }
        }
        return (possible != 0) ;
    }

    private void restartGame() {
        for (int index = 0; index < textViews.length; index++) {
            changeText(textViews[index], 1);
        }
        switch (difficult) {
            case 0:
                changeText(additionGameCom3, 0);
                changeText(additionGamePlayer3, 0);
                break;
            case 1:
                additionGamePlayer3.setVisibility(View.VISIBLE);
                additionGameCom3.setVisibility(View.VISIBLE);
                break;
        }
        isGameOver = false;
        score = 0;
        changeBout(false);
        changeScore();
        changeClickable(false);
    }
}

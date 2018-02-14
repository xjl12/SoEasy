package test.xjl12.soeasy;

import android.app.ProgressDialog;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.content.FileProvider;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import com.maddog05.maddogdialogs.MaddogProgressDialog;
import com.xiaozhi.firework_core.FireWorkView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "test.xjl12.soeasy.MESSAGE";
    private EditText input;
    private CoordinatorLayout mCl;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder new_version, not_new_version;
    private MenuItem dev_sub_info,dev_sub_test,game_caishu,game_addition;
    private AppCompatImageView nav_dev_imageView,nav_game_imageview;
    private Context context = this;
    private String checkUpdateUrl,downloadUpdateUrl,updateLogUrl,network_error;
    private File updateAPK;
    private boolean is_failed_again,is_check_update_dialog_show = false;
    private DialogInterface.OnClickListener redownloadListener,reCheckListener;
    private MaddogProgressDialog checkingUpdateDialog;
    private FireWorkView fireWorkView;

    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
	        switch (msg.what) {
                case Others.DOWNLOAD_PROGRESS:
                    if (msg.arg1 == 1) {
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.setMax(100);
                    }
                    mProgressDialog.setProgress(msg.arg1);
                    break;
                case Others.DOWNLOAD_SUCCESS:
                    mProgressDialog.dismiss();
                    Toast.makeText(context,R.string.update_download_complete,Toast.LENGTH_LONG).show();
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromFile(updateAPK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(context,getString(R.string.fileprovider_authorities),updateAPK);
                        install.setDataAndType(uri,Others.getMimeTypeFromFile(updateAPK));
                    }
                    else {
                        install.setDataAndType(uri,Others.getMimeTypeFromFile(updateAPK));
                    }
                    startActivity(install);
                    break;
                case Others.DOWNLOAD_FAILED:
                    mProgressDialog.dismiss();
                    Others.errorDialog((String) msg.obj,context,mHandle,true,false,redownloadListener);
                    break;
                case Others.GET_NETWORK_STRING_SUCCESS:
                    String[] get_internet_version_result = ((String) msg.obj).split(Others.separator);
                    int internet_version_code = Integer.valueOf(get_internet_version_result[0]);
                    String internet_version = get_internet_version_result[1];
                    new_version.setMessage(getString(R.string.find_new_version,internet_version));
                    if (internet_version_code > Others.getAppVersionCode(context)) new_version.show();
                    if (is_check_update_dialog_show) {
                        checkingUpdateDialog.dismiss();
                        is_check_update_dialog_show = false;
                        if (internet_version_code <= Others.getAppVersionCode(context))
                            not_new_version.show();
                    }
                    break;
                case Others.GET_NETWORK_STRING_FAILED:
                    if (! is_failed_again) {
                        is_failed_again = true;
                        network_error = (String) msg.obj;
                        checkUpdateUrl = getString(R.string.apk_version_code_url_2);
                        downloadUpdateUrl = getString(R.string.apk_url_2);
                        updateLogUrl = getString(R.string.update_log_url_2);
                        Others.getStringFromInternet(checkUpdateUrl,mHandle);
                    }
                    else {
                        if (is_check_update_dialog_show) {
                            checkingUpdateDialog.dismiss();
                            is_check_update_dialog_show = false;
                            is_failed_again = false;
                            Others.errorDialog(getString(R.string.check_update_failed, checkUpdateUrl,msg.obj,getString(R.string.apk_version_code_url),network_error), MainActivity.this, mHandle, true, false, reCheckListener);
                        }
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* if (savedInstanceState != null)
		 {
		 if (savedInstanceState.getInt("theme") != -1)
		 {
		 setTheme(savedInstanceState.getInt("theme"));
		 }
		 } */
        setContentView(R.layout.main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_DrawerLayout);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_mdToolbar);
        final NavigationView mNavigation = (NavigationView) findViewById(R.id.main_NavigationView);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        mCl = (CoordinatorLayout) findViewById(R.id.main_mdCoordinatorLayout);
        input = (EditText) findViewById(R.id.main_m_input);
        final Typeface light_typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Others.initActivity(this, toolbar, mCl);

        startFireWork();

        View header = mNavigation.getHeaderView(0);
        TextView mNavigation_header_version = header.findViewById(R.id.navigation_header_app_version);
        TextView app_name = header.findViewById(R.id.navigation_header_m_app_name);
        mNavigation_header_version.setText(Others.getAppVersionName(getApplicationContext()));
        app_name.setTypeface(light_typeface);
        Menu navigation_menu = mNavigation.getMenu();
        dev_sub_info = navigation_menu.findItem(R.id.main_develop_sub_info);
        dev_sub_test = navigation_menu.findItem(R.id.main_develop_sub_test);
        game_addition = navigation_menu.findItem(R.id.navigation_item_addition);
        game_caishu = navigation_menu.findItem(R.id.navigation_item_caishu_games);
        nav_dev_imageView = navigation_menu.findItem(R.id.navigation_item_develop).getActionView().findViewById(R.id.navigation_imgview);
        nav_game_imageview = navigation_menu.findItem(R.id.navigation_item_games).getActionView().findViewById(R.id.navigation_imgview);

        nav_dev_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNavigationDevSubState();
            }
        });
        nav_game_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNavigationGamesSubState();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerOpened(View view) {
                fireWorkView.stopAnim();
                fab.hide();
                super.onDrawerOpened(view);
            }

            public void onDrawerClosed(View view) {
                startFireWork();
                fab.show();
                super.onDrawerClosed(view);
            }
        };
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        //检查更新
        checkUpdateUrl = getString(R.string.apk_version_code_url);
        downloadUpdateUrl = getString(R.string.apk_url);
        updateLogUrl = getString(R.string.update_log_url);
        updateAPK = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),getString(R.string.downloaded_update_file_name));
        redownloadListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                downloadUpdate();
            }
        };
        reCheckListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkUpdateUrl = getString(R.string.apk_version_code_url);
                downloadUpdateUrl = getString(R.string.apk_url);
                updateLogUrl = getString(R.string.update_log_url);
                is_check_update_dialog_show = true;
                checkingUpdateDialog.show();
                Others.getStringFromInternet(checkUpdateUrl,mHandle);
            }
        };
        Others.getStringFromInternet(checkUpdateUrl,mHandle);
        //Dialog init

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(true);
		mProgressDialog.setTitle(R.string.downloading);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Others.downloadStop();
            }
        });
		
		new_version = new AlertDialog.Builder(this);
		new_version.setTitle(R.string.find_new_version_title);
		new_version.setIcon(R.drawable.ic_system_update_alt_black_24dp);
		new_version.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        downloadUpdate();
                    }
                });
        new_version.setNeutralButton(R.string.update_later,null);
        new_version.setNegativeButton(R.string.view_update_log, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateLogUrl));
                startActivity(intent);
            }
        });

                fab.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View p1) {
                        if (input.length() != 0) {
                            Intent second_activity = new Intent(getApplicationContext(), SecondActivity.class);
                            second_activity.putExtra(EXTRA_MESSAGE, input.getText().toString());
                            startActivity(second_activity);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.second_Activity_text_null, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        not_new_version = new AlertDialog.Builder(context);
        not_new_version.setTitle(R.string.update);
        not_new_version.setIcon(R.drawable.ic_check_black);
        not_new_version.setMessage(R.string.soeasy_version_newest);
        not_new_version.setPositiveButton(R.string.ok_I_know,null);

        checkingUpdateDialog = new MaddogProgressDialog(context);
        checkingUpdateDialog.setTitle(R.string.check_update);
        checkingUpdateDialog.setMessage(R.string.updating_message);
        checkingUpdateDialog.setCancelable(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(), mCl, input);
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mCl.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View p1, MotionEvent p2) {
                Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(), p1, input);
                return false;
            }
        });

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(final MenuItem p1) {
                switch (p1.getItemId()) {
                    case R.id.navigation_item_games:
                        changeNavigationGamesSubState();
                        break;
                    case R.id.navigation_item_addition:
                        Intent intent = new Intent(getApplicationContext(),AdditionActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_caishu_games:
                        Intent games_intent = new Intent(getApplicationContext(), GamesActivity.class);
                        startActivity(games_intent);
                        break;
                    case R.id.navigation_item_my_world:
                        startActivity(new Intent(getApplicationContext(), MyWorldActivity.class));
                        break;
                    case R.id.navigation_item_urllist:
                        Intent url_list_intent = new Intent(getApplicationContext(), URLListActivity.class);
                        startActivity(url_list_intent);
                        break;
                    case R.id.navigation_item_develop:
                        changeNavigationDevSubState();
                        break;
                    case R.id.main_develop_sub_test:
                        Intent test_intent = new Intent(getApplicationContext(), TestActivity.class);
                        startActivity(test_intent);
                        break;
                    case R.id.main_develop_sub_info:
                        Intent debug_intent = new Intent(getApplicationContext(), DebugActivity.class);
                        debug_intent.putExtra(EXTRA_MESSAGE, Others.getAppVersionName(getApplicationContext()));
                        startActivity(debug_intent);
                        break;
                    case R.id.navigation_item_math:
                        Intent math_intent = new Intent(getApplicationContext(), MathActivity.class);
                        startActivity(math_intent);
                        break;
                    case R.id.navigation_item_update:
                        is_check_update_dialog_show = true;
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        checkingUpdateDialog.show();
                        Others.getStringFromInternet(checkUpdateUrl,mHandle);
                        break;
                    case R.id.navigation_item_exit:
                        Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
                        finish();
                        break;
                    default:
                        Snackbar.make(mCl, R.string.wrong, Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.colorAccent_Light))
                                .setAction(R.string.send_error, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        startActivity(Intent.createChooser(Others.isQQInstalled(getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, getString(R.string.send_error_message, Others.getAppVersionName(getApplicationContext()), Others.getRunningActivityName(MainActivity.this), p1.getTitle().toString())).setType("text/plain")), getString(R.string.Error_no_item_action)));
                                    }
                                }).show();
                        break;
                }
                return true;
            }
        });
        if (!BuildConfig.DEBUG) {
            Others.DeleteDirAllFile(getExternalFilesDir("Log"));
        }
    }

    private void downloadUpdate() {
        if (Others.checkIsStorageWritable(context)) {
            mProgressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Others.downloadFileFromHttp(downloadUpdateUrl,updateAPK,mHandle);
                }
            }).start();
        }
        else {
            Others.errorDialog(Others.checkStorageWritableWithInfo(context), context, mHandle, false, true,redownloadListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getMenuInflater().inflate(R.menu.activity_actions, menu);
        menu.findItem(R.id.force_exit_item).setVisible(true);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Others.ChangeEdittextStatusAndHideSoftInput(getApplicationContext(), mCl, input);
        //Others.DeleteDirAllFile(getApplicationContext().getExternalCacheDir());
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        if (dev_sub_info.isVisible() || dev_sub_test.isVisible()) {
            changeNavigationDevSubState();
        }
        if (game_caishu.isVisible() || game_addition.isVisible()) {
            changeNavigationGamesSubState();
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onStop();
    }

    private void changeNavigationDevSubState() {
        if (dev_sub_info.isVisible() || dev_sub_test.isVisible()) {
            dev_sub_info.setVisible(false);
            dev_sub_test.setVisible(false);
            nav_dev_imageView.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        } else {
            dev_sub_info.setVisible(true);
            dev_sub_test.setVisible(true);
            nav_dev_imageView.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }
    }

    private void changeNavigationGamesSubState() {
        if (game_caishu.isVisible() || game_addition.isVisible()) {
            game_caishu.setVisible(false);
            game_addition.setVisible(false);
            nav_game_imageview.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        } else {
            game_caishu.setVisible(true);
            game_addition.setVisible(true);
            nav_game_imageview.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }
    }

    private void startFireWork() {
        fireWorkView = new FireWorkView(this,R.drawable.firework);
        CoordinatorLayout.LayoutParams mlayoutParams = new CoordinatorLayout.LayoutParams(-1,-1);
        mCl.addView(fireWorkView,mlayoutParams);
        fireWorkView.playAnim();
    }
}

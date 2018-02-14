package test.xjl12.soeasy;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.view.inputmethod.*;
import android.webkit.MimeTypeMap;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.*;

import android.support.v7.widget.Toolbar;

import java.lang.Process;

import android.accounts.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Others {
    public static final int DOWNLOAD_PROGRESS = 0;
    public static final int DOWNLOAD_SUCCESS = 1;
    public static final int DOWNLOAD_FAILED = 2;
    public static final int DOWNLOAD_STOP = 3;
    public static final int GET_NETWORK_STRING_SUCCESS = 4;
    public static final int GET_NETWORK_STRING_FAILED = 5;
    public static final int TASK_FAILED = 6;
    //public static final int ADDITION_ACTIVITY_RESTART_GAME = 7;
    //public static final int URL_CHECK_FAILED = 7;
    //public static final int URL_CHECK_SUCCESS = 8;

    public static final String separator = ";";

    private static volatile boolean is_stop = false;

    //初始化activity
    public static void initActivity(final AppCompatActivity mdActivity,final Toolbar toolbar, final CoordinatorLayout mCl) {
        mdActivity.setSupportActionBar(toolbar);
        mdActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mdActivity.getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.help_development:
                        Intent help_development_web = new Intent(Intent.ACTION_VIEW);
                        help_development_web.setData(Uri.parse(mdActivity.getResources().getStringArray(R.array.web_url)[1]));
                        mdActivity.startActivity(help_development_web);
                        break;
                    case R.id.source_item:
                        Intent source_web = new Intent(Intent.ACTION_VIEW);
                        source_web.setData(Uri.parse(mdActivity.getResources().getStringArray(R.array.web_url)[0]));
                        mdActivity.startActivity(source_web);
                        break;
                    case R.id.force_exit_item:
                        Others.DeleteDirAllFile(mdActivity.getExternalCacheDir());
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                        break;
                    case R.id.my_world_delete_downloaded:
                        MyWorldActivity.downloaded_archive.delete();
                        item.setVisible(false);
                        break;
                    case R.id.games_show_point_info_item:
                        GamesActivity.showPointInfo();
                        break;
                   // case R.id.addition_replay_game:
                     //   break;
                    case R.id.shot_item:
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                Intent shot_share = Others.SendShot(mdActivity);
                                if (shot_share != null) {
                                    mdActivity.startActivity(shot_share);
                                } else {
                                    Snackbar.make(mCl, R.string.Error_shot_error, Snackbar.LENGTH_LONG)
                                            .setActionTextColor(mdActivity.getResources().getColor(R.color.colorAccent_Light))
                                            .setAction(R.string.retry, new View.OnClickListener() {

                                                @Override
                                                public void onClick(View p1) {
                                                    Intent intent = Others.getShot(mdActivity);
                                                    if (intent != null) {
                                                        mdActivity.startActivity(intent);
                                                    } else {
                                                        Toast.makeText(mdActivity, mdActivity.getString(R.string.Error_cannot_shot), Toast.LENGTH_LONG);
                                                    }
                                                }
                                            }).show();
                                }
                            }
                        }).start();
                        break;
                    default:
                        Snackbar.make(mCl, R.string.wrong, Snackbar.LENGTH_LONG)
                                .setActionTextColor(mdActivity.getResources().getColor(R.color.colorAccent_Light))
                                .setAction(R.string.send_error, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View p1) {
                                        mdActivity.startActivity(Intent.createChooser(Others.isQQInstalled(mdActivity, new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, mdActivity.getString(R.string.send_error_message, Others.getAppVersionName(mdActivity), Others.getRunningActivityName(mdActivity), item.getTitle().toString())).setType("text/plain")), mdActivity.getString(R.string.Error_no_item_action)));
                                    }
                                }).show();
                        break;
                }
                return true;
            }
        });
    }

    //判断程序是否安装
    public static boolean isAppInstalled(Context context, String package_name) {
        try {
            context.getPackageManager().getPackageInfo(package_name, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //判断我的世界版本
    public static boolean CheckMyWorldVersion(Context context) {
        int versionCode = -1;
        String my_world = context.getString(R.string.my_world_name);
        List<PackageInfo> p_infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo p_info : p_infos) {
            if (p_info.packageName.equals(my_world)) {
                versionCode = p_info.versionCode;
            }
        }

        if (versionCode == 870160005) {
            return true;
        } else {
            return false;
        }
    }

    //判断QQ是否安装
    public static Intent isQQInstalled(Context context, Intent intent) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getResources().getString(R.string.qq_name), 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            return intent;
        } else {
            return intent.setClassName(context.getString(R.string.qq_name),context.getString(R.string.qq_class_name));
        }
    }

    //获取程序版本号
    public static String getAppVersionName(Context context) {
        String app_version;
        try {
            PackageInfo app_pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_version = app_pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, R.string.Error_PackageManager_NameNotFoundException, Toast.LENGTH_LONG).show();
            app_version = context.getResources().getString(R.string.unknow);
        }
        return app_version;
    }

    public static int getAppVersionCode(Context context) {
        int app_version_code = -1;
        try {
            PackageInfo app_pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_version_code = app_pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, R.string.Error_PackageManager_NameNotFoundException, Toast.LENGTH_LONG).show();
        }
        return app_version_code;
    }

    //获取Activity名字
    public static String getRunningActivityName(Context context) {
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
    }

    public static void errorDialog(final String error_info, final Context context, final Handler mHandle,boolean is_network_error,boolean is_storage_error,DialogInterface.OnClickListener retryListener) {
        AlertDialog.Builder error_builder = new AlertDialog.Builder(context);
        error_builder.setTitle(R.string.error);
        final String message;
        if (is_storage_error && error_info != null) {
            message = context.getString(R.string.sdcard_error_with_info,error_info);
        }
        else if (is_storage_error) {
            message = context.getString(R.string.sdcard_error);
        }
        else if (is_network_error && error_info != null) {
            message = context.getString(R.string.network_error_message_with_info,error_info);
        }
        else if (is_network_error) {
            message = context.getString(R.string.network_error_message);
        }
        else if (error_info != null) {
            message = context.getString(R.string.error_message_with_infomation,error_info);
        }
        else {
            message = context.getString(R.string.error_message);
        }
        error_builder.setMessage(message);
        if (retryListener != null) {
            error_builder.setPositiveButton(R.string.retry, retryListener);
            error_builder.setNegativeButton(R.string.feebback, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface p1, int p2) {
                    feebbackErrorInfo(message, context, mHandle);
                    p1.dismiss();
                }
            });
            error_builder.setNeutralButton(R.string.cancel,null);
        }
        else {
            error_builder.setNeutralButton(R.string.cancel, null);
            error_builder.setPositiveButton(R.string.feebback, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface p1, int p2) {
                    feebbackErrorInfo(message, context, mHandle);
                    p1.dismiss();
                }
            });
        }
        error_builder.setIcon(R.drawable.ic_warning);
        error_builder.show();
    }

    //反馈
    public static void feebbackErrorInfo(final String info, final Context context, final Handler mHandle) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Intent feebback;
                final String feebback_message = context.getString(R.string.send_error_message, Others.getAppVersionName(context), Others.getRunningActivityName(context), info == null ? context.getString(R.string.wrong) : info);
                if (Others.isAppInstalled(context, context.getString(R.string.qq_name))) {
                    feebback = new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=1062256455"));
                    mHandle.post(new Runnable() {

                        @Override
                        public void run() {
                            ClipboardManager clip = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                            clip.setText(feebback_message);
                        }

                    });
                } else {
                    feebback = new Intent(Intent.ACTION_SEND);
                    feebback.setType("text/plain");
                }
                feebback.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                feebback.putExtra(Intent.EXTRA_TEXT, feebback_message);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                context.startActivity(feebback);
            }
        }).start();
        Toast.makeText(context, context.getString(R.string.feebback_point), Toast.LENGTH_LONG).show();
    }

    //判断存储设备是否可写
    public static boolean checkIsStorageWritable(Context context) {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String checkStorageWritableWithInfo(Context context) {
        try {
            File test_file = new File(context.getExternalCacheDir(), RandomString(5));
            OutputStream os = new FileOutputStream(test_file);
            os.write(RandomString(16).getBytes());
            os.flush();
            test_file.delete();
        } catch (IOException e) {
            return e.toString();
        }
        return null;
    }

    //获取目录结构
    public static String getDiretoryStructureForString(File dir, String dir_name) {
        StringBuffer result = new StringBuffer();
        if (dir.isDirectory()) {
            File[] lists = dir.listFiles();
            if (lists.length == 0 && dir_name == null) {
                result.append(dir.getName() + File.separator);
            } else if (lists.length == 0) {
                result.append(dir_name + File.separator + dir.getName() + File.separator);
            } else {
                for (File file : lists) {
                    if (dir_name == null) {
                        result.append(getDiretoryStructureForString(file, dir.getName()));
                    } else {
                        result.append(getDiretoryStructureForString(file, dir_name + File.separator + dir.getName()));
                    }
                }
            }
        } else {
            result.append(dir_name + File.separator + dir.getName() + separator);
        }
        return result.toString();
    }

    //获取文件扩展名

    public static String getFileExtension (File file) {
        String extension = null;
        String name = file.getName();
        String[] filenameSplit = name.split("\\.");
        if (filenameSplit.length > 1) {
            extension = filenameSplit[filenameSplit.length - 1];
            if (extension.contains(File.separator)) {
                extension = null;
            }
        }
        return extension;
    }

    //获取文件属性
    public static String getMimeTypeFromFile (File file) {
        String fileExtension = getFileExtension(file);
        String fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        return fileMimeType;
    }
    //流复制
    public static void copyAcordingStream(InputStream is, OutputStream os) throws IOException {
        byte[] cache = new byte[1024];
        int length;
        while ((length = is.read(cache)) > 0) {
            os.write(cache, 0, length);
        }
    }

    /*	复制文件或文件夹
     例如:复制/abc目录到/abcd/
     则应该传入/abc的File对象和/abcd/abc的File对象
     */
    public static void fileCopy(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] list = src.list();
            for (String list_file_name : list) {
                File src_file = new File(src, list_file_name);
                File dest_file = new File(dest, list_file_name);
                fileCopy(src_file, dest_file);
            }
        } else {
            InputStream is = new FileInputStream(src);
            OutputStream os = new FileOutputStream(dest);
            copyAcordingStream(is, os);
            is.close();
            os.flush();
            os.close();
        }
    }

    /*	解压assets中指定目录到sd卡指定目录
     例如将指定assets下abc目录所有文件解压到/sdcard/abc中
     则应传入Context,abc的String对象,/sdcard/abc的File对象
     */
    public static void assetsCopy(AssetManager am, String assets_path, File dest) throws IOException {
        if (dest.isDirectory()) {
            String[] file_names = am.list(assets_path);
            for (String file_name : file_names) {
                File dest_file = new File(dest, file_name);
                String[] sub_list = am.list(assets_path + "/" + file_name);
                if (sub_list.length > 0) {
                    dest_file.mkdir();
                }
                assetsCopy(am, assets_path + "/" + file_name, dest_file);
            }
        } else {
            InputStream is = am.open(assets_path);
            OutputStream os = new FileOutputStream(dest);
            copyAcordingStream(is, os);
            is.close();
            os.flush();
            os.close();
        }
    }
    //ZIP压缩方法，支持文件夹压缩

    public static void zipCompression(File src, File target) throws IOException {
        InputStream is;
        ZipOutputStream zipOs = new ZipOutputStream(new FileOutputStream(target));
        ZipEntry zipEntry;
        String[] lists = getDiretoryStructureForString(src, null).split(separator);
        for (String file : lists) {
            File src_file;
            if (file.endsWith(File.separator)) {
                zipEntry = new ZipEntry(file);
                zipOs.putNextEntry(zipEntry);
            } else {
                src_file = new File(src.getParentFile(), file);
                zipEntry = new ZipEntry(file);
                is = new FileInputStream(src_file);
                zipOs.putNextEntry(zipEntry);
                copyAcordingStream(is, zipOs);
                is.close();
            }
        }
        zipOs.flush();
        zipOs.close();
    }

    //Zip解压算法
    public static void uncompressZip(File zipFile, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
            if (!targetDir.exists()) throw new IOException("Nkdir failed.Is your storage writable?");
        }
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;
        String target_path = targetDir.getPath();
        if (!target_path.endsWith(File.separator)) target_path = target_path + File.separator;
        while ((entry = zis.getNextEntry()) != null) {
            File target = new File(target_path + entry.getName());
            File target_parent = target.getParentFile();
            if (!target_parent.exists()) {
                target_parent.mkdirs();
                if (!targetDir.exists()) throw new IOException("Nkdir failed.Is your storage writable?");
            }
            OutputStream fos = new FileOutputStream(target);
            copyAcordingStream(zis, fos);
            fos.flush();
            fos.close();
        }
        zis.close();
    }

    //改变编辑框为非输入状态并收回软键盘
    public static void ChangeEdittextStatusAndHideSoftInput(Context context, View parent_view, EditText edittext) {
        parent_view.setFocusable(true);
        parent_view.setFocusableInTouchMode(true);
        parent_view.requestFocus();

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }

    //获取屏幕截图并发送(screencap)
    public static Intent SendShot(Context context) {
        File shot_file = new File(context.getExternalCacheDir(), Others.RandomString(5) + ".png");
        ProcessBuilder pb = new ProcessBuilder(new String[]{"screencap", "-p", shot_file.toString()});
        try {
            Process proc = pb.start();
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
        }
        if (shot_file.exists()) {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shot_file));
            send.setType("image/png");
            return Intent.createChooser(isQQInstalled(context, send), context.getString(R.string.share_point));
        }
        return null;
    }

    //改变Snackbar背景,字体颜色
    public static void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) {
        View view = snackbar.getView();
        if (view != null) {
            view.setBackgroundColor(backgroundColor);
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
        }
    }

    //产生一个随机的字符串
    public static String RandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    //删除目录内所有文件文件
    public static boolean DeleteDirAllFile(File dir) {
        if (dir != null) {
            if (dir.isDirectory()) {
                File[] list_files = dir.listFiles();
                for (File file : list_files) {
                    DeleteDirAllFile(file);
                }

            }
            return dir.delete();
        }
        return false;
    }

    //输入流转文本
    public static String getString(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer("");
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            //sb.append("\n");
        }

        return sb.toString();
    }

    // 获取当前活动的截屏
    public static Intent getShot(Activity activity) {
        File shot_file = new File(activity.getExternalCacheDir(), RandomString(5) + ".png");
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int status_bar_height = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, status_bar_height, width, height - status_bar_height);
        view.destroyDrawingCache();
        try {
            FileOutputStream shot_fos = new FileOutputStream(shot_file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, shot_fos);
            shot_fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Intent.createChooser(isQQInstalled(activity.getApplicationContext(), new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shot_file)).setType("image/png")), activity.getString(R.string.share_point));
    }

    //网络
    public static void getStringFromInternet(final String url, final Handler mHandle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL mURL = new URL(url);
                    connection = (HttpURLConnection) mURL.openConnection();
                    if (connection instanceof HttpsURLConnection) {
                        getStringFromInternetUsingHttps(url,mHandle);
                    }
                    else {
                        connection.setRequestMethod("GET");
                        connection.setReadTimeout(5000);
                        connection.setConnectTimeout(10000);
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            InputStream is = connection.getInputStream();
                            String response = getString(is);
                            is.close();
                            Message success_msg = Message.obtain();
                            success_msg.obj = response;
                            success_msg.what = GET_NETWORK_STRING_SUCCESS;
                            mHandle.sendMessage(success_msg);
                        } else {
                            throw new NetworkErrorException("The response code is" + Integer.toString(responseCode));
                        }
                    }
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = GET_NETWORK_STRING_FAILED;
                    msg.obj = e.toString();
                    mHandle.sendMessage(msg);
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void getStringFromInternetUsingHttps(final String url, final Handler mHandle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                try {
                    URL mURL = new URL(url);
                    connection = (HttpsURLConnection) mURL.openConnection();
                    initHttps();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(10000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = connection.getInputStream();
                        String response = getString(is);
                        is.close();
                        Message success_msg = Message.obtain();
                        success_msg.obj = response;
                        success_msg.what = GET_NETWORK_STRING_SUCCESS;
                        mHandle.sendMessage(success_msg);
                    } else {
                        throw new NetworkErrorException("The response code is" + Integer.toString(responseCode));
                    }
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = GET_NETWORK_STRING_FAILED;
                    msg.obj = e.toString();
                    mHandle.sendMessage(msg);
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    //下载文件
    public static void downloadFileFromHttp(String url, File download_file, Handler mHandle) {
        if (download_file.exists()) download_file.delete();
        if (!download_file.getParentFile().exists()) download_file.getParentFile().mkdirs();
        HttpURLConnection connection = null;
        Message failed_message = Message.obtain();
        failed_message.what = DOWNLOAD_FAILED;
        try {
            URL mURL = new URL(url);
            connection = (HttpURLConnection) mURL.openConnection();
            if (connection instanceof HttpsURLConnection) {
                initHttps();
            }
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(10000);
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                int file_length = connection.getContentLength();
                InputStream is = connection.getInputStream();
                FileOutputStream fos = new FileOutputStream(download_file);
                byte[] buff = new byte[1024];
                int progress, progress_temp = 0;
                int length = -1;
                int count = 0;
                while ((length = is.read(buff)) > 0 && !is_stop) {
                    fos.write(buff, 0, length);
                    if (file_length > 0) {
                        count += length;
                        progress = (int) ((float) count / file_length * 100);
                        if (progress > progress_temp) {
                            Message progress_message = Message.obtain();
                            progress_message.what = DOWNLOAD_PROGRESS;
                            progress_temp = progress_message.arg1 = progress;
                            mHandle.sendMessage(progress_message);
                        }
                    }
                }
                is.close();
                fos.flush();
                fos.close();
                if (is_stop) return;
                mHandle.sendEmptyMessage(DOWNLOAD_SUCCESS);
            } else {
                throw new NetworkErrorException("The response code is" + Integer.toString(responseCode));
            }
        } catch (Exception e) {
            failed_message.obj = e.toString();
            mHandle.sendMessage(failed_message);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (is_stop) {
                if (download_file.exists()) {
                    download_file.delete();
                }
                is_stop = false;
            }
        }
    }

    public static void initHttps() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null,new TrustManager[] {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }},new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
    }
/*
    public static void checkURL(final String url,final Handler mHandle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL mURL = new URL(url);
                    connection = (HttpURLConnection) mURL.openConnection();
                    if (connection instanceof HttpsURLConnection) {
                        initHttps();
                    }
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(10000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        mHandle.sendEmptyMessage(URL_CHECK_SUCCESS);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                mHandle.sendEmptyMessage(URL_CHECK_FAILED);
            }
        }).start();
    }
*/
    public static void downloadStop() {
        is_stop = true;
    }

   /* public static void downloadFileThroughDownloadManager (String url,String fileName,String title,String des,Context context) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(des);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
/*
    public static Activity getGlobleActivity() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException
	{
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        for (Object activityRecord:activities.values())
		{
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord))
			{
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }
        return null;
    }
    */

}

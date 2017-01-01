package test.xjl12.soeasy;

import android.app.*;

public class CrashApplication extends Application {
    @Override
    public void onCreate() 
	{
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
    }
}

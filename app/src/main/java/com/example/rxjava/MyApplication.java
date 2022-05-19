package com.example.rxjava;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by maozonghong
 * <p>
 * on 5/19/22
 **/
public class MyApplication extends Application {

    public static final String TAG="MyApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate");
//        HookHelper.hookAMSAidl();
//        HookHelper.hookHandler();
//        HookHelper.hookInstrumentation();
    }
}

package com.example.yzy.myapplication;

import android.app.Application;

/**
 * Created by yzy on 2017/3/22.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}

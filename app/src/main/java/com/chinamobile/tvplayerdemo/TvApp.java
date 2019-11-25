package com.chinamobile.tvplayerdemo;

import android.app.Application;

import com.chinamobile.tvplayerdemo.tools.CrashHandler;
import com.chinamobile.tvplayerdemo.tools.GlobalToast;

public class TvApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);//全局收集异常类，并且log储存在本地
        GlobalToast.init(this);

    }
}

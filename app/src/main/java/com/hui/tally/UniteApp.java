package com.hui.tally;

import android.app.Application;

import com.hui.tally.db.DBManager;

//表示全局應用的類
public class UniteApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化數據庫
        DBManager.initDB(getApplicationContext());
    }
}

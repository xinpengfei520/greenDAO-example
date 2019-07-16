package com.xpf.android.greendao;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.xpf.android.greendao.bean.DaoMaster;
import com.xpf.android.greendao.bean.DaoSession;

/**
 * Created by x-sir on 2019-07-15 :)
 * Function:
 */
public class MyApplication extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        initGreenDao();
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"test.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}

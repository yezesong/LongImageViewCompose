/**
 * Copyright (C) 2016 NetDragon Websoft Inc.
 */
package com.nd.pad.longimagecompose;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;

import cn.bmob.v3.Bmob;

/**
 * @author hustdhg
 * @since 2016/09/21
 */
public class MyApplication extends Application {

    private static final String TAG="MyApplication";
    public  static Context mConetext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        mConetext=this;
        Bmob.initialize(this,"a5dac1c2c2862e3fd56c1d1ae08a55ad");
        initDir();
    }

    /**
     * 初始化目录
     */
    public void initDir(){
        File file = new File(FileSystem.PATH, "longimage");
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
/**
 * Copyright (C) 2016 NetDragon Websoft Inc.
 */
package com.nd.pad.longimagecompose;

import android.app.Application;
import android.util.Log;

import cn.bmob.v3.Bmob;

/**
 * @author hustdhg
 * @since 2016/09/21
 */
public class MyApplication extends Application {

    private static final String TAG="MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        Bmob.initialize(this,"a5dac1c2c2862e3fd56c1d1ae08a55ad");

    }
}
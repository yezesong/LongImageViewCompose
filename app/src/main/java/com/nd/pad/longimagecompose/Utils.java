package com.nd.pad.longimagecompose;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * provide serval util
 * Created by hustdhg on 2017/2/20 0020.
 */

public class Utils {

    /**
     * 通知系统刷新数据库，让图片可以马上显示在相册中
     *
     * @param imgPath
     */
    public void notifySystemUri(String imgPath){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(imgPath));
        intent.setData(uri);
        MyApplication.mConetext.sendBroadcast(intent);
    }



}

/**
 * Copyright (C) 2016 NetDragon Websoft Inc.
 */
package com.nd.pad.longimagecompose;

import cn.bmob.v3.BmobObject;

/**
 * bmob object to storage msg
 *
 * @author hustdhg
 * @since 2016/09/27
 */
public class FeedBack extends BmobObject{

    private String advice;

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }
}
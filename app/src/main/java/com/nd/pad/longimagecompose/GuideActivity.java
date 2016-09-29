/**
 * Copyright (C) 2016 NetDragon Websoft Inc.
 */
package com.nd.pad.longimagecompose;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 引导用户页
 *
 * @author hustdhg
 * @since 2016/09/27
 */
public class GuideActivity extends AppCompatActivity {

    // Views

    private ImageView mImageView;
    private CardView mCardView;
    private Button mBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

        mImageView = (ImageView) findViewById(R.id.logo);
        mCardView = (CardView) findViewById(R.id.cd_show);
        mBtn = (Button) findViewById(R.id.btn_enter);

        ObjectAnimator o1 = ObjectAnimator.ofFloat(mImageView, "scaleX", 0f, 1f);
        ObjectAnimator o2 = ObjectAnimator.ofFloat(mImageView, "scaleY", 0f, 1f);

        ObjectAnimator o3 = ObjectAnimator.ofFloat(mCardView, "scaleX", 0f, 1f);
        ObjectAnimator o4 = ObjectAnimator.ofFloat(mCardView, "scaleY", 0f, 1f);

        ObjectAnimator o5 = ObjectAnimator.ofFloat(mBtn, "scaleX", 0f, 1f);
        ObjectAnimator o6 = ObjectAnimator.ofFloat(mBtn, "scaleY", 0f, 1f);

        AnimatorSet as = new AnimatorSet();
        as.playTogether(o1, o2, o3, o4, o5, o6);
        as.setInterpolator(new OvershootInterpolator());
        as.setDuration(1500);
        as.start();


        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


}
package com.nd.pad.longimagecompose;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements FileSystem.OnMakeListener {

    // 制作长图的按钮
    private FloatingActionButton fab_add;

    private Toolbar toolbar;

    private Toolbar mTb_child;


    /**
     * 和列表视图相关
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private QuickAdapter mAdapter;

    // 显示进度条
    private ProgressDialog mAlertDialog;

    // 监听文件夹变化
    private MyFileObserver myFileObserver;

    // tag
    private static final String TAG = "MainActivity";

    // isDeleted
    private boolean isDeleted = true;

    // push advice btn
    private Button btn_push;
    private EditText et_msg;
    private Button btn_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");


        myFileObserver = new MyFileObserver(FileSystem.PATH + File.separator + FileSystem.DIR_NAME);
        myFileObserver.startWatching();

        EventBus.getDefault().register(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTb_child = (Toolbar) findViewById(R.id.tl_child);
        mTb_child.setTitle(R.string.adviceMe);
        mTb_child.setTitleTextColor(Color.WHITE);

        btn_push = (Button) findViewById(R.id.btn_push);
        et_msg = (EditText) findViewById(R.id.et_advice);
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // push msg to bmob
                // notify the fab to show
                // the snackbar show
                if (et_msg.getText().toString() != null && !et_msg.getText().toString().equals("")) {
                    FeedBack feedBack = new FeedBack();
                    feedBack.setAdvice(et_msg.getText().toString());
                    feedBack.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Snackbar.make(mRecyclerView, R.string.pushSuccess, Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(mRecyclerView, R.string.pushFailed, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.nsv));
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                fab_add.show();


            }
        });
        btn_cancel = (Button) findViewById(R.id.btn_cacel);
        btn_cancel.setOnClickListener(view1 ->
                {
                    BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.nsv));
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    fab_add.show();


                }

        );


        // 交互提示dialog//////////////////////////////
        mAlertDialog = new ProgressDialog(MainActivity.this);
        mAlertDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mAlertDialog.setTitle(R.string.note);
        mAlertDialog.setMessage(getResources().getString(R.string.notexit));
        mAlertDialog.setIcon(R.drawable.icon);
        mAlertDialog.setCancelable(false);

        Log.d("tag", "test");


        //---------         --------
        fab_add = (FloatingActionButton) findViewById(R.id.fab);
        fab_add.setOnClickListener(view -> {
                    ObjectAnimator oba = ObjectAnimator.ofFloat(fab_add, "rotation", 0f, 90f);
                    oba.setInterpolator(new LinearInterpolator());
                    oba.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //跳转到长图制作的界面
                            MultiImageSelector.create()
                                    .showCamera(false)
                                    .start(MainActivity.this, 474);
                        }
                    });
                    oba.start();


                }
        );


        // 列表初始化
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new QuickAdapter();
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        //---------   图片点击事件，图片长按事件~    --------
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + baseQuickAdapter.getItem(i)), "image/*");
                startActivity(intent);


            }
        });

        // load the data of file dir
        loadData();

        mRecyclerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void SimpleOnItemLongClick(final BaseQuickAdapter baseQuickAdapter, View view, final int i) {
                int position = i;

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.note)
                        .setMessage(R.string.msg)
                        .setIcon(R.drawable.icon)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File file = new File((String) baseQuickAdapter.getItem(position));
                                baseQuickAdapter.remove(position);
                                Snackbar.make(mRecyclerView, R.string.delSuccess, Snackbar.LENGTH_SHORT)
                                        .setAction(R.string.cancelDel, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                // 重新加载布局
                                                loadData();
                                                isDeleted = false;

                                            }
                                        }).setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        Log.d(TAG, "onDismissed");

                                        if (isDeleted) {
                                            // 本地文件中删除
                                            if (baseQuickAdapter.getItemCount() > i) {
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                            }

                                        } else {
                                            isDeleted = true;
                                        }


                                    }
                                }).show();


                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                dialog.show();


            }
        });

        // check whether the app is first start
        checkForFirst();


    }

    // 检查是否是第一次启动应用
    private void checkForFirst() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean isFirstStart = sp.getBoolean("isFirst", true);
        if (isFirstStart) {
            startActivity(new Intent(MainActivity.this, GuideActivity.class));
            sp.edit().putBoolean("isFirst", false).apply();

        }


    }





    @Override
    public void startMake() {
        // 开始制作
        mAlertDialog.show();


    }

    /**
     * 加载数据
     */
    private void loadData() {
        // 从本地文件夹加载图片并且显示出来(本地图片的名字就是要在底部显示的内容)
        Observable.just(1)
                .map(new Func1<Integer, List<String>>() {

                    @Override
                    public List<String> call(Integer integer) {
                        FileSystem fs = FileSystem.getInstance();
                        List<String> path = fs.getAllImgs();
                        Collections.reverse(path);
                        return path;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        if (strings.size() == 0) {
                            ImageView imageView = new ImageView(MainActivity.this);
                            imageView.setImageResource(R.drawable.welcome);
                            mAdapter.setEmptyView(imageView);
                        } else {
                            mAdapter.setNewData(strings);

                        }
                    }
                });


    }

    @Override
    public void endMake(String path) {
//         制作完成

        loadData();
        mAlertDialog.setMessage(getResources().getString(R.string.finish));
        mAlertDialog.dismiss();
        Snackbar.make(mRecyclerView, R.string.finish, Snackbar.LENGTH_SHORT).show();


    }

    // 拦截退出按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.drawable.icon)
                    .setTitle(R.string.note)
                    .setMessage(R.string.exit)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            alert.show();


        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 474 && resultCode == RESULT_OK) {
            List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            FileSystem.getInstance().setMakeListener(this);
            FileSystem.getInstance().composeImages(paths);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UIMessage msg) {
        endMake(msg.name);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,AboutMe.class));

            return true;
        } else if (id == R.id.advice) {

            BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.nsv));
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                fab_add.hide();

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }


        } else {
            // id ==R.id.note
            startActivity(new Intent(MainActivity.this, GuideActivity.class));

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myFileObserver.stopWatching();
    }
}

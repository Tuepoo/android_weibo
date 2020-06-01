package com.tuepoo.weibo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tuepoo.weibo.ActivityManager;
import com.tuepoo.weibo.BaseApplication;
import com.tuepoo.weibo.R;
import com.tuepoo.weibo.fragment.FragmentController;
import com.tuepoo.weibo.utils.ExampleUtil;
import com.tuepoo.weibo.utils.ToastUtils;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

/**
 * 该类主要演示如何进行授权、SSO登陆。
 * 参考博客：http://blog.csdn.net/nihaoqiulinhe/article/details/16822279
 */

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, WbShareCallback {

    private long firstTime = 0;

    private RadioGroup rg_tab;
    private FragmentController controller;
    private BaseApplication application;
    private RadioButton rb_home;
    private ImageView iv_add;
    private EditText msgText;

    private WbShareHandler shareHandler;

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        controller = FragmentController.getInstance(this, R.id.fl_content);
        controller.showFragment(0); //默认展示第一个
        initView();
        registerMessageReceiver();
        //添加Activity到容器中
        ActivityManager.getInstance().addActivity(this);
    }

    private void initView() {
        shareHandler = new WbShareHandler(MainActivity.this);
        shareHandler.registerApp();
        rg_tab = (RadioGroup) this.findViewById(R.id.rg_tab);
        rg_tab.setOnCheckedChangeListener(this); //监听回调
        rb_home = (RadioButton) this.findViewById(R.id.rb_home);
        application = (BaseApplication) getApplication();
        rb_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (application.isIfIndexFragment()) {
                    controller.homeFragment.refresh();
                }
                application.setIfIndexFragment(true);
            }
        });

        iv_add = (ImageView) this.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
//                startActivity(intent);
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                TextObject textObject = new TextObject();
                textObject.text = "这是一条测试微博";
                weiboMessage.textObject = textObject;
                shareHandler.shareMessage(weiboMessage, true);
            }
        });


    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        shareHandler.doResultIntent(data,this);
    }

    @Override
    public void onWbShareSuccess() {
        Toast.makeText(this, "发布成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWbShareFail() {
        Toast.makeText(this,
                "发布失败" + "Error Message: ",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWbShareCancel() {
        Toast.makeText(this, "取消发布", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home:
                controller.showFragment(0);
                break;
            case R.id.rb_message:
                controller.showFragment(1);
                application.setIfIndexFragment(false);
                break;
            case R.id.rb_discover:
                controller.showFragment(2);
                application.setIfIndexFragment(false);
                break;
            case R.id.rb_my:
                controller.showFragment(3);
                application.setIfIndexFragment(false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            ToastUtils.showToast(this, "再按一次退出应用", Toast.LENGTH_LONG);
            firstTime = secondTime;
        } else {
            finish();
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }

    private void setCostomMsg(String msg){
        if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(View.VISIBLE);
        }
    }

}

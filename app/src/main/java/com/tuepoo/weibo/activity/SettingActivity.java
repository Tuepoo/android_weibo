package com.tuepoo.weibo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tuepoo.weibo.ActivityManager;
import com.tuepoo.weibo.BaseActivity;
import com.tuepoo.weibo.R;
import com.tuepoo.weibo.api.LogoutAPI;
import com.tuepoo.weibo.constants.AccessTokenKeeper;
import com.tuepoo.weibo.constants.Constants;
import com.tuepoo.weibo.utils.DataCleanUtils;
import com.tuepoo.weibo.utils.TitleBuilder;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;


public class SettingActivity extends BaseActivity {

    private LinearLayout ll_cleanCache;
    private TextView tv_cache;
    private TextView tv_logout;

    private AlertDialog.Builder builder;

    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAccessToken = AccessTokenKeeper.readAccessToken(SettingActivity.this);
        builder = new AlertDialog.Builder(SettingActivity.this);
        initView();
        ActivityManager.getInstance().addActivity(this);
    }

    private void initView() {
        new TitleBuilder(this)
                .setLeftImage(R.drawable.detail_left_sel)
                .setTitleText("设置")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        ll_cleanCache = (LinearLayout) findViewById(R.id.ll_cleanCache);
        tv_cache = (TextView) findViewById(R.id.tv_cache);
        tv_logout = (TextView) findViewById(R.id.tv_logout);

        tv_cache.setText(getCacheSize());

        //缓存清理
        upCache();

        //退出登录
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("确定注销并退出微博吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                                com.sina.weibo.sdk.auth.AccessTokenKeeper.clear(getApplicationContext());
                                mAccessToken = new Oauth2AccessToken();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void upCache() {
        ll_cleanCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("确定要清楚缓存吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanUtils.cleanExternalCache(SettingActivity.this);
                                tv_cache.setText(getCacheSize());
                                showToast("缓存清理成功");
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


    private void logout() {
        new LogoutAPI(SettingActivity.this, Constants.APP_KEY, AccessTokenKeeper.readAccessToken(SettingActivity.this))
                .logout(new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            String result = JSON.parseObject(s).getString("result");
                            if ("true".equalsIgnoreCase(result)) {
                                AccessTokenKeeper.clear(SettingActivity.this);
                                mAccessToken = null;
                                ActivityManager.getInstance().exit();
                            }
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        showToast("注销失败");
                    }
                });
    }

    /**
     * 获取缓存大小
     */
    private String getCacheSize() {
        try {
            return DataCleanUtils.getCacheSize(getExternalCacheDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

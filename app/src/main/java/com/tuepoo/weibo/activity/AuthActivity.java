package com.tuepoo.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tuepoo.weibo.BaseActivity;
import com.tuepoo.weibo.R;
import com.tuepoo.weibo.utils.TitleBuilder;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * 该类主要演示如何进行授权、SSO登陆。
 */

public class AuthActivity extends BaseActivity {

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
//        AuthInfo mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(AuthActivity.this);

        new TitleBuilder(this)
                .setLeftText("关闭")
                .setTitleText("微博授权登陆")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        // SSO 授权, ALL IN ONE   如果手机安装了微博客户端则使用客户端授权,没有则进行网页授权
        findViewById(R.id.obtain_token_via_signature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new SelfWbAuthListener());
            }
        });
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {

        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            AuthActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = token;
                    if (mAccessToken.isSessionValid()) {
                        // save Token to SharedPreferences
                        com.sina.weibo.sdk.auth.AccessTokenKeeper.writeAccessToken(AuthActivity.this, mAccessToken);
                        Toast.makeText(AuthActivity.this,
                                "授权成功", Toast.LENGTH_SHORT).show();
                        intent2Activity(MainActivity.class);
                        finish();
                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(AuthActivity.this,
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(AuthActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }
}


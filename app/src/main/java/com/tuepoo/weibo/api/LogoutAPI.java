package com.tuepoo.weibo.api;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

public class LogoutAPI extends AbsOpenApi {
    /** 注销地址（URL） */
    private static final String REVOKE_OAUTH_URL = "https://api.weibo.com/oauth2/revokeoauth2";

    /**
     * 构造函数。
     *
     * @param oauth2AccessToken Token 实例
     */
    public LogoutAPI(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }

    /**
     * 异步取消用户的授权。
     *
     * @param listener 异步请求回调接口
     */
    public void logout(RequestListener listener) {
        requestAsync(REVOKE_OAUTH_URL, new WeiboParameters(mAppKey), HTTPMETHOD_POST, listener);
    }
}

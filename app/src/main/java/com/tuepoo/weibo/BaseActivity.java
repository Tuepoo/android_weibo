package com.tuepoo.weibo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tuepoo.weibo.api.RequestWeiboApi;
import com.tuepoo.weibo.constants.CommonConstants;
import com.tuepoo.weibo.constants.Constants;
import com.tuepoo.weibo.utils.LoggerUtils;
import com.tuepoo.weibo.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

/**
 * Created by liudong on 2016/4/27.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG;
    protected BaseApplication application;
    protected SharedPreferences sp;

    protected RequestWeiboApi weiboApi;
    protected ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        application = (BaseApplication) getApplication();
        sp = getSharedPreferences(CommonConstants.SP_name, MODE_PRIVATE);

        weiboApi = new RequestWeiboApi(this);
        imageLoader = ImageLoader.getInstance();

        WbSdk.install(BaseActivity.this, new AuthInfo(BaseActivity.this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
    }

    protected void showToast(String msg) {
        ToastUtils.showToast(this, msg, Toast.LENGTH_LONG);
    }

    protected void showLog(String msg) {
        LoggerUtils.show(TAG, msg);
    }
}

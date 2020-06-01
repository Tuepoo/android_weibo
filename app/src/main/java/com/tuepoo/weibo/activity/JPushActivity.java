package com.tuepoo.weibo.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.tuepoo.weibo.BaseActivity;
import com.tuepoo.weibo.R;

public class JPushActivity extends BaseActivity {

    private TextView content_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpush);
        initVeiw();
    }

    private void initVeiw(){
        content_view = (TextView)findViewById(R.id.content_view);
    }

}

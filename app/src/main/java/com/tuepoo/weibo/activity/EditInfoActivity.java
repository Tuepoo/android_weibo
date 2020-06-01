package com.tuepoo.weibo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tuepoo.weibo.ActivityManager;
import com.tuepoo.weibo.BaseActivity;
import com.tuepoo.weibo.R;
import com.tuepoo.weibo.fragment.UserFragment;
import com.tuepoo.weibo.utils.TitleBuilder;
import com.tuepoo.weibo.utils.ToastUtils;

public class EditInfoActivity extends BaseActivity {

    private EditText tv_name;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        userName = getIntent().getStringExtra("userName");
        initView();
        ActivityManager.getInstance().addActivity(this);
    }

    private void initView(){
        new TitleBuilder(this)
                .setTitleText("编辑资料")
                .setLeftImage(R.drawable.userinfo_left_sel)
                .setRightText("完成")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(EditInfoActivity.this, "修改成功", Toast.LENGTH_LONG);
                        UserFragment.ed_name = tv_name.getText().toString();
                        finish();
                    }
                });
        tv_name = (EditText)findViewById(R.id.tv_name);
        tv_name.setText(userName);
    }

}

package com.tuepoo.weibo.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tuepoo.weibo.BaseActivity;
import com.tuepoo.weibo.R;
import com.tuepoo.weibo.adapter.FansAdapter;
import com.tuepoo.weibo.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.tuepoo.weibo.api.SimpleRequestListener;
import com.tuepoo.weibo.entity.User;
import com.sina.weibo.sdk.exception.WeiboException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchActivity extends BaseActivity {

    private RecyclerView rv_search;
    private EditText search_user;
    private LinearLayoutManager manager;
    private FansAdapter adapter;
    private HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter;
    int MAX_RANGE = 100;

    private List<User> users;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        search_user = (EditText) findViewById(R.id.search_user);
        users = new ArrayList<>();
        rv_search = (RecyclerView) findViewById(R.id.rv_search);
        manager = new LinearLayoutManager(this);
        rv_search.setLayoutManager(manager);
        adapter = new FansAdapter(SearchActivity.this, users);
        headerAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(adapter);
        rv_search.setAdapter(headerAndFooterRecyclerViewAdapter);
        search_user.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
//                    ToastUtils.showToast(SearchActivity.this, search_user.getText().toString(), Toast.LENGTH_LONG);
//                    setItem();
                    weiboApi.usersShow("", search_user.getText().toString(), new SimpleRequestListener(SearchActivity.this, null) {
                        @Override
                        public void onComplete(String s) {
                            super.onComplete(s);
                            //获取用户信息并设置
                            Log.e("搜索用户：",s);
                            user = JSON.parseObject(s, User.class);
                            users.clear();
                            users.add(user);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onWeiboException(WeiboException e) {
                            super.onWeiboException(e);

                        }
                    });
                }
                return false;
            }
        });
    }

    private void setItem() {
        users.clear();
        Random r = new Random();
        int fans = r.nextInt(100);
        users.add(new User(search_user.getText().toString(),"粉丝：" + fans));
        adapter.notifyDataSetChanged();
    }

}

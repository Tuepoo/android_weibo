package com.tuepoo.weibo.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.tuepoo.weibo.ActivityManager;
import com.tuepoo.weibo.BaseActivity;
import com.tuepoo.weibo.R;
import com.tuepoo.weibo.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.tuepoo.weibo.adapter.StatusAdapter;
import com.tuepoo.weibo.api.RequestWeiboApi;
import com.tuepoo.weibo.api.SimpleRequestListener;
import com.tuepoo.weibo.entity.Status;
import com.tuepoo.weibo.entity.response.StatusTimeLineResponse;
import com.tuepoo.weibo.fragment.WeiBoItemSpace;
import com.tuepoo.weibo.utils.TitleBuilder;
import com.sina.weibo.sdk.exception.WeiboException;

import java.util.ArrayList;
import java.util.List;

public class HotWbActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView rv_hot_wb;
    private SwipeRefreshLayout mSwipeRefresh;
    private StatusAdapter adapter;
    private HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter = null;
    private LinearLayoutManager manager;
    private List<Status> statuses = new ArrayList<>();
    private int lastVisibleItem;
    private int curPage = 1;
    //控制一次加载完成后才能继续下拉加载
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_wb);
        initView();
        loadData(1);
        ActivityManager.getInstance().addActivity(this);
    }

    private void initView(){
        new TitleBuilder(this)
                .setTitleText("热门微博")
                .setLeftImage(R.drawable.userinfo_left_sel)
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        rv_hot_wb = (RecyclerView)findViewById(R.id.rv_hot_wb);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget_hot_wb);
        manager = new LinearLayoutManager(HotWbActivity.this);
        rv_hot_wb.setLayoutManager(manager);
        adapter = new StatusAdapter(HotWbActivity.this, statuses);
        headerAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(adapter);
        rv_hot_wb.setAdapter(headerAndFooterRecyclerViewAdapter);

        //设置分割线
        rv_hot_wb.addItemDecoration(new WeiBoItemSpace(8));

        //设置加载图标颜色
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mSwipeRefresh.setOnRefreshListener(this);

        //第一次进入页面显示加载图标
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(true);
            }
        });

        //上拉刷新
        rv_hot_wb.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem == adapter.getItemCount()) {

                    //当加载更多数据时(下拉刷新时)，屏蔽有可能的重复的上拉操作
                    boolean isRefreshing = mSwipeRefresh.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        //网络请求
                        loadData(curPage + 1);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });
    }

    private void loadData(final int page){
        RequestWeiboApi api = new RequestWeiboApi(HotWbActivity.this);
        api.statusesPublic_timeline(page, new SimpleRequestListener(HotWbActivity.this, null) {
            @Override
            public void onComplete(String s) {
                super.onComplete(s);

                if (page == 1) {
                    statuses.clear();
                }
                curPage = page;

                //处理返回的json字符串
                addData(JSON.parseObject(s, StatusTimeLineResponse.class));

                isLoading = false;
            }

            @Override
            public void onWeiboException(WeiboException e) {
                super.onWeiboException(e);
                isLoading = false;
            }

            @Override
            public void onAllDone() {
                super.onAllDone();
                mSwipeRefresh.setRefreshing(false);
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        });
    }

    private void addData(StatusTimeLineResponse resBean) {
        for (Status status : resBean.getStatuses()) {
            if (!statuses.contains(status)) {
                statuses.add(status);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        loadData(1);
    }

}

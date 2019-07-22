package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.library.MyPickAdapter;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgMiningMenuListEntity;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 采选历史页面
 */
public class PickHistoryActivity extends AppCompatActivity {
    //采选历史页面
    @Bind(R.id.rcl_pick_history_list)
    RecyclerView mPickHistoryList;
    //错误内容文字
    @Bind(R.id.tvError)
    TextView tvError;
    //错误内容页面
    @Bind(R.id.lLError)
    LinearLayout lLError;
    //加载
    @Bind(R.id.load)
    FrameLayout load;

    private MyPickAdapter mAdapter;

    private UserInfo mUserInfo;
    private String mToken;

    private List<PgMiningMenuListEntity> mListData;
    private ProgressDialog mProgressDialog;
    private int index =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_history);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);
        initMembers();
    }

    //错误界面
    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }

    //初始化成员
    private void initMembers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mPickHistoryList.setLayoutManager(linearLayoutManager);

        //Constant.FLAG_PICK_HISTORY  从历史页面进入
        mAdapter = new MyPickAdapter(PickHistoryActivity.this, Constant.FLAG_PICK_HISTORY);
        mPickHistoryList.setAdapter(mAdapter);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        if (mUserInfo != null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, null, "加载中...");
            }

            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            mToken = s;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (lLError != null) {
                                lLError.setVisibility(View.VISIBLE);
                            }
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            Ln.d("PickHistoryActivity:initMembers:getToken:error:" + throwable.getMessage());
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("PickHistoryActivity:initMembers:getToken:OK");
                            initData();
                        }
                    });
        }else {
            if (lLError != null) {
                lLError.setVisibility(View.VISIBLE);
            }
        }
    }
    //初始化数据
    private void initData() {
        PersonalCenterManager.getInstance().getHistoryMiningMenuList(
                mUserInfo.getUserid(),
                mToken,
                mUserInfo.getOrg_id(),
                1, 30).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgMiningMenuListEntity>>() {
                    @Override
                    public void call(List<PgMiningMenuListEntity> pgMiningMenuListEntities) {
                        mListData = pgMiningMenuListEntities;
                        mAdapter.setData(mListData);
                        Ln.d("PickHistoryActivity:initData:getHistoryMiningMenuList:mListData:" + mListData);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                        }
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("PickHistoryActivity:initData:getHistoryMiningMenuList:error:" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (mListData.size() == 0) {
                            lLError.setVisibility(View.VISIBLE);
                            tvError.setText("暂无数据");
                        } else {
                            lLError.setVisibility(View.GONE);

                        }
                        Ln.d("PickHistoryActivity:initData:getHistoryMiningMenuList:ok");
                        mAdapter.notifyDataSetChanged();
                    }
                });
        mPickHistoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) mPickHistoryList.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    PersonalCenterManager.getInstance().getHistoryMiningMenuList(
                            mUserInfo.getUserid(),
                            mToken,
                            mUserInfo.getOrg_id(),
                            index++, 30).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<List<PgMiningMenuListEntity>>() {
                                @Override
                                public void call(List<PgMiningMenuListEntity> pgMiningMenuListEntities) {
                                    if (pgMiningMenuListEntities == null || pgMiningMenuListEntities.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    mListData.addAll(pgMiningMenuListEntities);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    loading = false;
                                    load.setVisibility(View.GONE);
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    loading = false;
                                    load.setVisibility(View.GONE);
                                    mAdapter.notifyDataSetChanged();

                                }
                            });

                }
            }
        });
    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("采选历史页面");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("采选历史页面");
        MobclickAgent.onPause(this);
    }
}

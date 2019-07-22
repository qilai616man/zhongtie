package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.personal.MiningMenuAdapter;
import com.crphdm.dl2.listener.OnChangeCountListener;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgMiningMenuDetail;
import com.digital.dl2.business.core.obj.PgResult;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 采选页面
 */

public class PickActivity extends AppCompatActivity {
    @Bind(R.id.tv_ebook_count)
    TextView mEBookCount;
    @Bind(R.id.tv_total_ebook_price)
    TextView mTotalEbookPrice;
    @Bind(R.id.tv_ebook_discount)
    TextView mEbookDiscount;
    @Bind(R.id.rl_pick_info)
    RelativeLayout mPickInfo;
    @Bind(R.id.v_divider)
    View mDivider;
    @Bind(R.id.ll_commit)
    LinearLayout mCommit;
    @Bind(R.id.rcl_pick_list)
    RecyclerView mPickList;
    @Bind(R.id.tv_comment)
    TextView mComment;
    @Bind(R.id.v_framelayout)
    FrameLayout mFramelayout;
    @Bind(R.id.btn_commit)
    TextView mBtnCommit;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private int mWhichActivity;
    private int mMiningMenuId;

    private MiningMenuAdapter mAdapter;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private PgMiningMenuDetail mMiningMenuDetail;
    //无网络界面
    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }

    //提交审核按钮
    @OnClick(R.id.btn_commit)
    void commit() {
//        Toast.makeText(PickActivity.this, "提交审核", Toast.LENGTH_SHORT).show();

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
                            //错误
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            Ln.d("PickActivity:commit:getToken:error:" + throwable.getMessage());
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            Ln.d("PickActivity:commit:getToken:ok");
                            PersonalCenterManager.getInstance().submitMiningMenu(
                                    mUserInfo.getUserid(),
                                    mToken,
                                    mUserInfo.getOrg_id())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Ln.d("PickActivity:commit:submitMiningMenu:提交审核采选单成功");
                                            } else {
                                                Ln.d("PickActivity:commit:submitMiningMenu:提交审核采选单失败");
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            Ln.d("PickActivity:commit:submitMiningMenu:error:" + throwable.getMessage());
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            refreshStatus(Constant.MINING_MENU_STATE_SHEN_HE_ZHONG);
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        mWhichActivity = getIntent().getIntExtra(Constant.PICK_ACTIVITY_BY_WHICH, Constant.FROM_USER_CENTER);
        mMiningMenuId = getIntent().getIntExtra(Constant.PICK_MINING_MENU_ID, 0);

        initMembers();
        setListener();
    }

    private void initMembers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPickList.setLayoutManager(linearLayoutManager);
        mPickList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new MiningMenuAdapter();
        mPickList.setAdapter(mAdapter);

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, "加载中...");
        }

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

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
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(PickActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        initData();
                    }
                });
    }
    //初始胡数据
    private void initData() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, "加载中...");
        }

        Ln.d("PickActivity:initData:getMiningMenuDetail");

        PersonalCenterManager.getInstance().getMiningMenuDetail(mUserInfo.getUserid(), mToken, mUserInfo.getOrg_id(), mMiningMenuId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<PgMiningMenuDetail>() {
                    @Override
                    public void call(PgMiningMenuDetail pgMiningMenuDetail) {
                        mMiningMenuDetail = pgMiningMenuDetail;
                        Ln.d("PickActivity:initData:mMiningMenuDetail:" + mMiningMenuDetail.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("PickActivity:initData:getMiningMenuDetail:error:" + throwable.getMessage());
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("PickActivity:initData:getMiningMenuDetail:完成");
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (mMiningMenuDetail.getShoppingCartItemList().size() == 0) {
                            lLError.setVisibility(View.VISIBLE);
                            tvError.setText("暂无数据");
                        } else {
                            lLError.setVisibility(View.GONE);
                            refreshData();
                        }
                    }
                });
    }
    //刷新数据
    private void refreshData() {
        mAdapter.setData(PickActivity.this, Constant.BOOK_TYPE_E_BOOK, mMiningMenuDetail.getShoppingCartItemList());
        mAdapter.notifyDataSetChanged();

        mEBookCount.setText("" + mMiningMenuDetail.getNumber());
        mTotalEbookPrice.setText("￥" + mMiningMenuDetail.getPreferentialPrice());
        mEbookDiscount.setText("￥" + mMiningMenuDetail.getAllPrice());

        refreshStatus(mMiningMenuDetail.getState());
//        refreshStatus(Constant.MINING_MENU_STATE_WEI_TI_JIAO);
    }
    //刷新状态
    private void refreshStatus(int state) {
//        if (mWhichActivity == Constant.FROM_CLOUD_BOOK_MARKET) {//从云书城进来
//
//        } else {//个人中心
        if (state == Constant.MINING_MENU_STATE_TONG_GUO) {

        } else if (state == Constant.MINING_MENU_STATE_WEI_TONG_GUO) {//未通过审核状态时  可以编辑
            mBtnCommit.setText("再次提交");
            mComment.setVisibility(View.VISIBLE);
            mDivider.setVisibility(View.GONE);
        } else if (state == Constant.MINING_MENU_STATE_SHEN_HE_ZHONG) {//正在审核状态时 页面不可编辑
            mBtnCommit.setText("审核中");
            mBtnCommit.setEnabled(false);
            mBtnCommit.setBackgroundResource(R.drawable.range_gray_2radius);

            mComment.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);

            mFramelayout.setVisibility(View.VISIBLE);
            mFramelayout.setClickable(true);
        } else {
//                (state == Constant.MINING_MENU_STATE_WEI_TI_JIAO)
            mBtnCommit.setText("提交审核");
            mComment.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);
        }
//        }
    }
    //设置监听
    private void setListener() {
        mAdapter.setListener(new OnChangeCountListener() {
            @Override
            public void onAdd(final int id, int type) {
                Ln.d("PickActivity:mAdapter:onAdd:id:" + id + ",type:" + type);
                if (mUserInfo != null) {
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
                                    //错误
                                    Ln.d("PickActivity:setListener:onAdd:getToken:error:" + throwable.getMessage());
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    //完成
                                    CloudBookstoreManager.getInstance().addMiningListById(
                                            mUserInfo.getOrg_id(),
                                            mUserInfo.getUserid(),
                                            mToken,
                                            id,
                                            2,
                                            1)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<PgResult>() {
                                                @Override
                                                public void call(PgResult result) {
                                                    if (result.isStatus()) {
                                                        Ln.d("PickActivity:setListener:onAdd:采选单数量添加成功");
                                                    } else {
                                                        Ln.d("PickActivity:setListener:onAdd:采选单数量添加失败:" + result.getMessage());
                                                    }
                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {
                                                    //错误
                                                    Ln.d("PickActivity:setListener:onAdd:addMiningList:error:" + throwable.getMessage());
//                                                    Toast.makeText(PickActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    //完成
                                                    Ln.d("PickActivity:setListener:onAdd:采选单数量添加完成");
                                                    initData();
                                                }
                                            });
                                }
                            });
                }
            }

            @Override
            public void onReduce(final int id, int type) {
                Ln.d("PickActivity:mAdapter:onReduce:id:" + id + ",type:" + type);
                if (mUserInfo != null) {
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
                                    //错误
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    //完成
                                    PersonalCenterManager.getInstance().deleteMiningList(
                                            mUserInfo.getUserid(),
                                            mUserInfo.getOrg_id(), mToken, id, 2, 1)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<Boolean>() {
                                                @Override
                                                public void call(Boolean aBoolean) {
                                                    if (aBoolean) {
                                                        Ln.d("CloudBookstoreBookDetailActivity:采选单数量减少成功");
                                                    } else {
                                                        Ln.d("CloudBookstoreBookDetailActivity:采选单数量减少失败");

                                                    }
                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {

                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    initData();
                                                }
                                            });
                                }
                            });

                }
            }

            @Override
            public void onDelete(final int id, final int type, final int number) {
                Ln.d("PickActivity:mAdapter:onDelete:id:" + id + ",type:" + type + ",number:" + number);
                if (mUserInfo != null) {
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
                                    //错误
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    //完成
                                    PersonalCenterManager.getInstance().deleteMiningList(
                                            mUserInfo.getUserid(),
                                            mUserInfo.getOrg_id(), mToken, id, type, number)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<Boolean>() {
                                                @Override
                                                public void call(Boolean aBoolean) {
                                                    if (aBoolean) {
                                                        Ln.d("CloudBookstoreBookDetailActivity:采选单数量减少成功");
                                                    } else {
                                                        Ln.d("CloudBookstoreBookDetailActivity:采选单数量减少失败");

                                                    }
                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {

                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    initData();
                                                }
                                            });
                                }
                            });

                }
            }
        });
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("采选页面");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("采选页面");
        MobclickAgent.onPause(this);
    }
    //创建Menu菜单的项目
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_pick, menu);
        return true;
    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_menu_pick_history) {
            startActivity(new Intent(PickActivity.this, PickHistoryActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgReviewMiningCategories;
import com.digital.dl2.business.core.obj.PgReviewMiningListData;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 审批采选单
 */

public class ApprovalListActivity extends AppCompatActivity {
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;
    @Bind(R.id.load)
    FrameLayout load;
    private int index = 2;

    private ApprovalListAdapter mAdapter;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private List<PgReviewMiningListData> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_list);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("审批采选单");
        }

        initMembers();
    }
    //错误提示页面监听
    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }
    //创建Menu菜单的项目
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_approval_list, menu);
        RelativeLayout shopcartLayout = (RelativeLayout) menu.findItem(R.id.action_shopcart).getActionView();

        shopcartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ApprovalListActivity.this, ApprovalPickHistoryActivity.class));
            }
        });
        return true;
    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //初始化成员
    private void initMembers() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new ApprovalListAdapter();
        recycler.setAdapter(mAdapter);


        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        if (mUserInfo != null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ApprovalListActivity.this, null, "加载中");
            }

            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            mToken = s;
                            Ln.d("ApprovalListActivity:initMembers:getTokenAsync:" + mToken);
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
                            Ln.d("ApprovalListActivity:initMembers:getTokenAsync:error:" + throwable.getMessage());
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("ApprovalListActivity:initMembers:getTokenAsync:ok");
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
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ApprovalListActivity.this, null, "加载中");
        }
        PersonalCenterManager.getInstance().getReviewMiningList(
                mUserInfo.getUserid(),
                mToken,
                mUserInfo.getOrg_id(),
                Constant.APPROVAL_STATUS_NO,
                1, 50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgReviewMiningListData>>() {
                    @Override
                    public void call(List<PgReviewMiningListData> pgReviewMiningListDatas) {
                        mListData = pgReviewMiningListDatas;
                        Ln.d("ApprovalListActivity:initData:getReviewMiningList:mListData:" + mListData);
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
                        Ln.d("ApprovalListActivity:initData:getReviewMiningList:error:" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (lLError != null) {
                            if (mListData.size() == 0) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            } else {
                                lLError.setVisibility(View.GONE);
                            }
                        }
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("ApprovalListActivity:initData:getReviewMiningList:ok");
                        mAdapter.notifyDataSetChanged();
                    }
                });

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) recycler.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    PersonalCenterManager.getInstance().getReviewMiningList(
                            mUserInfo.getUserid(),
                            mToken,
                            mUserInfo.getOrg_id(),
                            Constant.APPROVAL_STATUS_NO,
                            index++, 30)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<List<PgReviewMiningListData>>() {
                                @Override
                                public void call(List<PgReviewMiningListData> pgReviewMiningListDatas) {
                                    if (pgReviewMiningListDatas == null || pgReviewMiningListDatas.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    mListData.addAll(pgReviewMiningListDatas);
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
    //审批员采选单适配器
    class ApprovalListAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        private String mRejectStr;
        private String mAdapterToken;

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CustomViewHolder customViewHolder = new CustomViewHolder(getLayoutInflater().inflate(R.layout.item_approval_select, parent, false));

            return customViewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            final PgReviewMiningListData reviewMining = mListData.get(position);
            Ln.d("ApprovalListAdapter:PgReviewMiningListData:" + reviewMining.toString());

//            mTime.setText(format.format(mMiningMenuDetail.getMiningDate()*1000));
            holder.selectNumber.setText(String.valueOf(reviewMining.getDemandId()));
            holder.time.setText(format.format(reviewMining.getSubmitTime() * 1000));
            holder.submitPeople.setText(reviewMining.getSubmitPeople());
            holder.priceAll.setText(String.valueOf(reviewMining.getAllPrice()));
            holder.pricePrivilege.setText(String.valueOf(reviewMining.getPreferentialPrice()));
            holder.editTextWriteComment.setText(reviewMining.getReject() == null ? "" : reviewMining.getReject());

            if (reviewMining.getStatus() == 0) {//待审核
                holder.state.setText("待审核");
            } else if (reviewMining.getStatus() == 1) {//已审核
                holder.state.setText("已审核");
            } else if (reviewMining.getStatus() == 2) {//打回
                holder.state.setText("打回");
            }

            List<PgReviewMiningCategories> list = reviewMining.getCategories();
            Ln.d("ApprovalListAdapter:PgReviewMiningCategories:" + list);

            holder.bookName.setText(getContents(true, list));
            holder.bookNumber.setText(getContents(false, list));


            holder.editTextWriteComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mRejectStr = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUserInfo != null) {
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(ApprovalListActivity.this, null, "加载中");
                        }
                        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        mAdapterToken = s;
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                        Ln.d("ApprovalListAdapter:btnCancel:getTokenAsync:error:" + throwable.getMessage());
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        modifyReviewMining(reviewMining.getDemandId(), mRejectStr, 2);
                                    }
                                });
                    } else {
//                        Toast.makeText(ApprovalListActivity.this, "UserInfo == null", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUserInfo != null) {
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(ApprovalListActivity.this, null, "加载中");
                        }
                        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        mAdapterToken = s;
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                        Ln.d("ApprovalListAdapter:btnOk:getTokenAsync:error:" + throwable.getMessage());
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        modifyReviewMining(reviewMining.getDemandId(), mRejectStr, 1);
                                    }
                                });
                    } else {
//                        Toast.makeText(ApprovalListActivity.this, "UserInfo == null", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.itemLinerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ApprovalPickDetailActivity.class);
                    intent.putExtra(Constant.PICK_MINING_MENU_ID, reviewMining.getDemandId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListData == null ? 0 : mListData.size();
        }

        private void modifyReviewMining(int id, String rejece, int status) {
            PersonalCenterManager.getInstance().modifyReviewMining(
                    mUserInfo.getUserid(),
                    mAdapterToken,
                    mUserInfo.getOrg_id(),
                    id, rejece, status).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            initData();
                        }
                    });
        }

        //目录
        private String getContents(boolean isName, List<PgReviewMiningCategories> contents) {
            if (contents != null && !contents.isEmpty()) {
                String str = "";
                for (PgReviewMiningCategories s : contents) {
                    if (isName) {
                        str += s.getName() + "\n";
                    } else {
                        str += String.valueOf(s.getNumber()) + "\n";
                    }
                }
                return str;
            }

            return null;
        }
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.select_number)
        TextView selectNumber;
        @Bind(R.id.time)
        TextView time;

        //        @Bind(R.id.book_container)
//        LinearLayout bookContainer;
        @Bind(R.id.tv_item_approval_select_book_name)
        TextView bookName;
        @Bind(R.id.tv_item_approval_select_book_number)
        TextView bookNumber;

        @Bind(R.id.submit_people)
        TextView submitPeople;
        @Bind(R.id.price_all)
        TextView priceAll;
        @Bind(R.id.state)
        TextView state;
        @Bind(R.id.price_privilege)
        TextView pricePrivilege;
        @Bind(R.id.edit_text_write_comment)
        EditText editTextWriteComment;
        @Bind(R.id.btn_cancel)
        Button btnCancel;
        @Bind(R.id.btn_ok)
        Button btnOk;
        @Bind(R.id.item_liner_layout)
        LinearLayout itemLinerLayout;

        CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("审批采选单页");
        MobclickAgent.onResume(this);
    }
    //停止
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("审批采选单页");
        MobclickAgent.onPause(this);
    }
}
